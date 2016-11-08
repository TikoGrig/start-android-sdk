package com.payfort.start;

import android.app.Activity;

import com.payfort.start.support.EmptyTokenCallback;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.payfort.start.StartApiTest.TEST_OPEN_KEY;

/**
 * Test for {@link Start} class.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class StartTest {

    private Start start;
    private Activity activity;

    @Before
    public void setUp() {
        start = new Start(TEST_OPEN_KEY);
        activity = Robolectric.setupActivity(TestActivity.class);
    }

    @Test(expected = NullPointerException.class)
    public void testCardNotNull() throws Exception {
        start.createToken(activity, null, new EmptyTokenCallback(), 100, "USD");
        Assert.fail();
    }

    @Test(expected = NullPointerException.class)
    public void testCallbackNotNull() throws Exception {
        start.createToken(activity, new Card("4111111111111111", "111", 12, 2020, "John Doe"), null, 100, "USD");
        Assert.fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCurrencyNotZero() throws Exception {
        start.createToken(activity, new Card("4111111111111111", "111", 12, 2020, "John Doe"), new EmptyTokenCallback(), 0, "USD");
        Assert.fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCurrencyNotNegative() throws Exception {
        start.createToken(activity, new Card("4111111111111111", "111", 12, 2020, "John Doe"), new EmptyTokenCallback(), -1, "USD");
        Assert.fail();
    }
}
