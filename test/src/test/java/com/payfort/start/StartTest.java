package com.payfort.start;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * Test for {@link Start} class.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class StartTest {

    private Start start;

    @Before
    public void setUp() {
        start = new Start("key");
    }

    @Test(expected = NullPointerException.class)
    public void testCardNotNull() throws Exception {
        start.createToken(null, new EmptyTokenCallback(), 100, "USD");
        Assert.fail();
    }

    @Test(expected = NullPointerException.class)
    public void testCallbackNotNull() throws Exception {
        start.createToken(new Card("4111111111111111", "111", 12, 2020, "John Doe"), null, 100, "USD");
        Assert.fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCurrencyNotZero() throws Exception {
        start.createToken(new Card("4111111111111111", "111", 12, 2020, "John Doe"), new EmptyTokenCallback(), 0, "USD");
        Assert.fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCurrencyNotNegative() throws Exception {
        start.createToken(new Card("4111111111111111", "111", 12, 2020, "John Doe"), new EmptyTokenCallback(), -1, "USD");
        Assert.fail();
    }

    private static final class EmptyTokenCallback implements TokenCallback {

        @Override
        public void onSuccess(Token token) {
            // do nothing
        }

        @Override
        public void onError(Exception error) {
            // do nothing
        }

        @Override
        public void onCancel() {
            // do nothing
        }
    }

}
