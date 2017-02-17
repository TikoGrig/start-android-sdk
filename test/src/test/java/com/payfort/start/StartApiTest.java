package com.payfort.start;

import com.payfort.start.support.WaitForResultWebCallback;
import com.payfort.start.web.StartApi;
import com.payfort.start.web.StartApiFactory;

import org.junit.Test;

import retrofit2.Call;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * Test for {@link StartApi} class.
 */
public class StartApiTest {

    static final String TEST_OPEN_KEY = "test_open_k_84493c9cebc499dfa6ac";
    static final String LIVE_OPEN_KEY = "live_open_k_55e06cde7fe8d3141a7e";

    @Test
    public void testCreateNewTokenVerificationNotRequired() throws Exception {
        StartApi startApi = StartApiFactory.newStartApi(TEST_OPEN_KEY);

        Call<Token> tokenCall = startApi.createToken("4111 1111 1111 1111", "123", 11, 2019, "John Doe");
        WaitForResultWebCallback<Token> getTokenCallback = new WaitForResultWebCallback<>();
        tokenCall.enqueue(getTokenCallback);
        assertTrue(getTokenCallback.waitForResult());

        assertNull(getTokenCallback.getError());
        assertNotNull(getTokenCallback.getResponse());
        assertNotNull(getTokenCallback.getResponse().body());
        assertFalse(getTokenCallback.getResponse().body().isVerificationRequired());
    }

    @Test
    public void testCreateNewTokenVerificationRequired() throws Exception {
        StartApi startApi = StartApiFactory.newStartApi(LIVE_OPEN_KEY);

        Call<Token> tokenCall = startApi.createToken("4111 1111 1111 1111", "123", 11, 2019, "John Doe");
        WaitForResultWebCallback<Token> getTokenCallback = new WaitForResultWebCallback<>();
        tokenCall.enqueue(getTokenCallback);
        assertTrue(getTokenCallback.waitForResult());

        assertNull(getTokenCallback.getError());
        assertNotNull(getTokenCallback.getResponse());
        assertNotNull(getTokenCallback.getResponse().body());
        assertTrue(getTokenCallback.getResponse().body().isVerificationRequired());
    }

    @Test
    public void testGetTokenVerificationNotEnrolled() throws Exception {
        StartApi startApi = StartApiFactory.newStartApi(LIVE_OPEN_KEY);
        Call<Token> tokenCall = startApi.createToken("4005550000000001", "123", 11, 2019, "John Doe");
        WaitForResultWebCallback<Token> getTokenCallback = new WaitForResultWebCallback<>();
        tokenCall.enqueue(getTokenCallback);
        assertTrue(getTokenCallback.waitForResult());

        Token token = getTokenCallback.getResponse().body();
        assertTrue(token.isVerificationRequired());

        Call<TokenVerification> tokenVerificationCall = startApi.createTokenVerification(token.getId(), 100, "USD");
        WaitForResultWebCallback<TokenVerification> newTokenVerificationCallback = new WaitForResultWebCallback<>();
        tokenVerificationCall.enqueue(newTokenVerificationCallback);
        assertTrue(newTokenVerificationCallback.waitForResult());

        assertNull(newTokenVerificationCallback.getError());
        assertNotNull(newTokenVerificationCallback.getResponse());
        assertNotNull(newTokenVerificationCallback.getResponse().body());
        assertFalse(newTokenVerificationCallback.getResponse().body().isEnrolled());
    }

    @Test
    public void testGetTokenVerificationEnrolled() throws Exception {
        StartApi startApi = StartApiFactory.newStartApi(LIVE_OPEN_KEY);
        Call<Token> tokenCall = startApi.createToken("5453010000064154", "123", 11, 2019, "John Doe");
        WaitForResultWebCallback<Token> getTokenCallback = new WaitForResultWebCallback<>();
        tokenCall.enqueue(getTokenCallback);
        assertTrue(getTokenCallback.waitForResult());

        String token = getTokenCallback.getResponse().body().getId();
        WaitForResultWebCallback<TokenVerification> newTokenVerificationCallback = new WaitForResultWebCallback<>();
        startApi.createTokenVerification(token, 100, "USD").enqueue(newTokenVerificationCallback);
        assertTrue(newTokenVerificationCallback.waitForResult());

        WaitForResultWebCallback<TokenVerification> getTokenVerificationCallback = new WaitForResultWebCallback<>();
        startApi.getTokenVerification(token).enqueue(getTokenVerificationCallback);
        assertTrue(getTokenVerificationCallback.waitForResult());

        assertNull(getTokenVerificationCallback.getError());
        assertNotNull(getTokenVerificationCallback.getResponse());
        assertNotNull(getTokenVerificationCallback.getResponse().body());
        assertFalse(getTokenVerificationCallback.getResponse().body().isFinalized());
    }
}
