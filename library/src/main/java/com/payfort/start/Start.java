package com.payfort.start;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.webkit.WebView;

import com.payfort.start.error.StartApiException;
import com.payfort.start.web.RetrofitUtils;
import com.payfort.start.web.StartApi;
import com.payfort.start.web.StartApiFactory;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.payfort.start.util.Preconditions.checkArgument;
import static com.payfort.start.util.Preconditions.checkNotNull;
import static com.payfort.start.util.Preconditions.checkState;
import static com.payfort.start.web.RetrofitUtils.enqueueWithCondition;
import static com.payfort.start.web.RetrofitUtils.enqueueWithRetry;

/**
 * A class for creation card {@link Token}.
 * This implementation is thread-safe. It is recommended to use single instance of this class due to performance reasons.
 */
public class Start {

    private static final int MAX_REQUEST_ATTEMPTS = 4;
    private static final long RETRY_DELAY_MS = 2000;
    private final StartApi startApi;

    /**
     * Constructs new instance using api key.
     * This <a href="https://docs.start.payfort.com/guides/api_keys/#how-to-get-api-keys">instruction</> tells how to get API keys for SDK.
     *
     * @param apiKey a api key to be used for communication with API
     * @throws NullPointerException if api key is {@code null}
     */
    public Start(String apiKey) {
        checkNotNull(apiKey);
        this.startApi = StartApiFactory.newStartApi(apiKey);
    }

    /**
     * Creates token asynchronously. Result will be returned via {@link TokenCallback} passed to arguments.
     *
     * @param activity      an activity. May be used to show dialog with {@link WebView} to perform token verification.
     * @param card          a card to be precessed and token will be received for. Can't be {@code null}
     * @param tokenCallback a callback to be called with results. Can't be {@code null}
     * @param amountInCents an amount in cents. Optional argument. Can be {@code null}, Can't be zero.
     * @param currency      a currency code of amount according to ISO4217.Optional argument. Can be {@code null}
     * @throws NullPointerException     if card or tokenCallback is {@code null}
     * @throws IllegalArgumentException if card amountInCents is zero or negative
     */
    public void createToken(Activity activity, Card card, TokenCallback tokenCallback, Integer amountInCents, String currency) {
        checkNotNull(activity, "Activity must be not null!");
        checkNotNull(card, "Card must be not null!");
        checkNotNull(tokenCallback, "TokenCallback must be not null!");
        checkArgument(amountInCents == null || amountInCents > 0, "Amount must be positive!");

        TokenRequest tokenRequest = new TokenRequest(activity, tokenCallback, amountInCents, currency);
        Call<Token> tokenCall = startApi.createToken(card.number, card.cvc, card.expirationMonth, card.expirationYear, card.owner);
        enqueueWithRetry(tokenCall, new NewTokenCallback(tokenRequest), MAX_REQUEST_ATTEMPTS);
    }

    private void onTokenCreated(TokenRequest tokenRequest, Token token) {
        if (token.isVerificationRequired()) {
            processTokenVerification(tokenRequest, token);
        } else {
            tokenRequest.tokenCallback.onSuccess(token);
        }
    }

    private void processTokenVerification(TokenRequest tokenRequest, Token token) {
        Call<TokenVerification> call = startApi.createTokenVerification(token.getId(), tokenRequest.amountInCents, tokenRequest.currency);
        enqueueWithRetry(call, new CreateTokenVerificationCallback(tokenRequest, token), MAX_REQUEST_ATTEMPTS);
    }

    private void onTokenVerificationCreated(TokenRequest tokenRequest, TokenVerification tokenVerification, Token token) {
        if (tokenVerification.isEnrolled()) {
            verifyTokenInBrowser(tokenRequest, token);
        } else {
            tokenRequest.tokenCallback.onSuccess(token);
        }
    }

    private void verifyTokenInBrowser(TokenRequest tokenRequest, Token token) {
        Call<TokenVerification> call = startApi.getTokenVerification(token.getId());

        String url = String.format(Locale.US, "%stokens/%s/verification/verify", StartApiFactory.BASE_URL, token.getId());
        Dialog verificationDialog = new VerificationWebViewDialog(tokenRequest.context, url, new VerificationDialogCallback(call, tokenRequest));
        verificationDialog.show();

        CheckTokenVerificationCallback verificationCallback = new CheckTokenVerificationCallback(tokenRequest, token, verificationDialog);
        enqueueWithCondition(call, verificationCallback, new VerificationStatusRetryCondition(), RETRY_DELAY_MS);
    }

    private final class NewTokenCallback implements Callback<Token> {

        private final TokenRequest tokenRequest;

        private NewTokenCallback(TokenRequest tokenRequest) {
            this.tokenRequest = tokenRequest;
        }

        @Override
        public void onResponse(Response<Token> response) {
            if (response.isSuccess()) {
                Token token = response.body();
                onTokenCreated(tokenRequest, token);
            } else {
                tokenRequest.tokenCallback.onError(new StartApiException("Error creating new token. Response code is " + response.code()));
            }
        }

        @Override
        public void onFailure(Throwable t) {
            tokenRequest.tokenCallback.onError(new StartApiException(t));
        }
    }

    private final class CreateTokenVerificationCallback implements Callback<TokenVerification> {

        private final TokenRequest tokenRequest;
        private final Token token;

        private CreateTokenVerificationCallback(TokenRequest tokenRequest, Token token) {
            this.tokenRequest = tokenRequest;
            this.token = token;
        }

        @Override
        public void onResponse(Response<TokenVerification> response) {
            checkState(response.isSuccess(), "Response isn't successful");
            checkState(response.body().isFinalized(), "Token is not finalized!");

            tokenRequest.tokenCallback.onSuccess(token);
        }

        @Override
        public void onFailure(Throwable t) {
            throw new IllegalStateException("Should not be called! Request can be canceled or be successful");
        }
    }

    private final class CheckTokenVerificationCallback implements Callback<TokenVerification> {

        private final TokenRequest tokenRequest;
        private final Token token;
        private final Dialog verificationDialog;

        private CheckTokenVerificationCallback(TokenRequest tokenRequest, Token token, Dialog verificationDialog) {
            this.tokenRequest = tokenRequest;
            this.token = token;
            this.verificationDialog = verificationDialog;
        }

        @Override
        public void onResponse(Response<TokenVerification> response) {
            if (response.isSuccess()) {
                verificationDialog.dismiss();
                TokenVerification tokenVerification = response.body();
                onTokenVerificationCreated(tokenRequest, tokenVerification, token);
            } else {
                tokenRequest.tokenCallback.onError(new StartApiException("Error verifying token. Response code is " + response.code()));
            }
        }

        @Override
        public void onFailure(Throwable t) {
            tokenRequest.tokenCallback.onError(new StartApiException(t));
        }
    }

    private final class VerificationStatusRetryCondition implements RetrofitUtils.RetryCondition<TokenVerification> {

        @Override
        public boolean doRetry(TokenVerification tokenVerification) {
            return !tokenVerification.isFinalized();
        }
    }

    private final class VerificationDialogCallback implements VerificationWebViewDialog.Callback {

        private final Call<TokenVerification> call;
        private final TokenRequest tokenRequest;

        private VerificationDialogCallback(Call<TokenVerification> call, TokenRequest tokenRequest) {
            this.call = call;
            this.tokenRequest = tokenRequest;
        }

        @Override
        public void onDialogCanceled() {
            call.cancel();
            tokenRequest.tokenCallback.onCancel();
        }
    }

    private static final class TokenRequest {

        private final Context context;
        private final TokenCallback tokenCallback;
        private final Integer amountInCents;
        private final String currency;

        private TokenRequest(Context context, TokenCallback tokenCallback, Integer amountInCents, String currency) {
            this.context = context;
            this.tokenCallback = tokenCallback;
            this.amountInCents = amountInCents;
            this.currency = currency;
        }
    }
}