package com.payfort.start;

import com.payfort.start.web.StartApi;
import com.payfort.start.web.StartApiFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import retrofit2.Call;
import retrofit2.Response;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link Card} class.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class StartApiTest {

    static final String TEST_OPEN_KEY = "test_open_k_84493c9cebc499dfa6ac";

    private StartApi startApi;

    @Before
    public void setUp() {
        startApi = StartApiFactory.newStartApi(TEST_OPEN_KEY);
    }

    @Test
    public void testCreateNewToken() throws Exception {
        Call<Token> tokenCall = startApi.createToken("4242424242424242", "123", 11, 2016, "John Doe");
        Response<Token> tokenResponse = tokenCall.execute();
        assertTrue(tokenResponse.isSuccessful());
        assertNotNull(tokenResponse.body().getId());
    }
}
