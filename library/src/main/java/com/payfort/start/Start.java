package com.payfort.start;

import static com.payfort.start.Preconditions.checkArgument;
import static com.payfort.start.Preconditions.checkNotNull;

/**
 * A class for creation card {@link Token}.
 * This implementation is thread-safe. It is recommended to use single instance of this class due to performance reasons.
 */
public class Start {

    private final String apiKey;

    /**
     * Constructs new instance using api key.
     * This <a href="https://docs.start.payfort.com/guides/api_keys/#how-to-get-api-keys">instruction</> tells how to get API keys for SDK.
     *
     * @param apiKey a api key to be used for communication with API
     * @throws NullPointerException if api key is {@code null}
     */
    public Start(String apiKey) {
        this.apiKey = checkNotNull(apiKey);
    }

    /**
     * Creates token asynchronously. Result will be returned via {@link TokenCallback} passed to arguments.
     *
     * @param card          a card to be precessed and token will be received for. Can't be {@code null}
     * @param tokenCallback a callback to be called with results. Can't be {@code null}
     * @param amountInCents an amount in cents. Optional argument. Can be {@code null}, Can't be zero.
     * @param currency      a currency code of amount according to ISO4217.Optional argument. Can be {@code null}
     * @throws NullPointerException     if card or tokenCallback is {@code null}
     * @throws IllegalArgumentException if card amountInCents is zero or negative
     */
    public void createToken(Card card, TokenCallback tokenCallback, Integer amountInCents, String currency) {
        checkNotNull(card, "Card must be not null!");
        checkNotNull(tokenCallback, "TokenCallback must be not null!");
        checkArgument(amountInCents == null || amountInCents > 0, "Amount must be positive!");
    }

}
