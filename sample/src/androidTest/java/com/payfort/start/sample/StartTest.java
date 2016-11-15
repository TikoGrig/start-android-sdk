package com.payfort.start.sample;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.payfort.start.Card;
import com.payfort.start.Start;
import com.payfort.start.Token;
import com.payfort.start.TokenVerification;
import com.payfort.start.sample.support.WaitForResultTokenCallback;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.payfort.start.sample.support.ViewMatchersExt.startsWithText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class StartTest {

    static final String TEST_OPEN_KEY = "test_open_k_84493c9cebc499dfa6ac";
    static final String LIVE_OPEN_KEY = "live_open_k_55e06cde7fe8d3141a7e";

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule(MainActivity.class);

    @Test
    public void testGetTokenVerificationNotRequired() throws Exception {
        Start start = new Start(TEST_OPEN_KEY);
        WaitForResultTokenCallback callback = new WaitForResultTokenCallback();
        start.createToken(activityRule.getActivity(), new Card("4111 1111 1111 1111", "111", 12, 2020, "John Doe"), callback, 100, "USD");
        callback.waitForResult();

        assertTrue(callback.hasResult());
        assertFalse(callback.isCanceled());
        assertNull(callback.getError());
        assertNotNull(callback.getToken());
        assertFalse(callback.getToken().isVerificationRequired());
    }

    @Test
    public void testGetTokenVerificationRequired() throws Exception {
        Start start = new Start(LIVE_OPEN_KEY);
        WaitForResultTokenCallback callback = new WaitForResultTokenCallback();
        start.createToken(activityRule.getActivity(), new Card("4111 1111 1111 1111", "111", 12, 2020, "John Doe"), callback, 100, "USD");
        callback.waitForResult();

        assertTrue(callback.hasResult());
        assertFalse(callback.isCanceled());
        assertNull(callback.getError());
        Token token = callback.getToken();
        assertNotNull(token);
        assertTrue(token.isVerificationRequired());
        assertNotNull(token.getVerification());
        assertNotNull(token.getVerification().getId());
    }

    @Test
    public void testGetTokenVerificationRequiredSubmitForm() throws Exception {
        Start start = new Start(LIVE_OPEN_KEY);
        MainActivity mainActivity = activityRule.getActivity();
        mainActivity.start = start;

        fillCard("4111 1111 1111 1111", "12", "2020", "111", "John Doe");
        closeSoftKeyboard();
        onView(withId(R.id.payButton)).perform(click());
        checkToastAppears("Your token is ");
    }

    @Test
    public void testGetTokenVerificationNotEnrolled() throws Exception {
        Start start = new Start(LIVE_OPEN_KEY);

        WaitForResultTokenCallback callback = new WaitForResultTokenCallback();
        start.createToken(activityRule.getActivity(), new Card("4005550000000001", "111", 12, 2020, "John Doe"), callback, 100, "USD");
        callback.waitForResult();

        Token token = callback.getToken();
        assertNotNull(token);
        assertTrue(token.isVerificationRequired());
        TokenVerification verification = token.getVerification();
        assertNotNull(verification);
        assertNotNull(verification.getId());
        assertFalse(verification.isEnrolled());
    }

    @Test
    public void testGetTokenVerificationNotEnrolledSubmitForm() throws Exception {
        Start start = new Start(LIVE_OPEN_KEY);
        MainActivity mainActivity = activityRule.getActivity();
        mainActivity.start = start;

        fillCard("4005550000000001", "12", "2020", "111", "John Doe");
        closeSoftKeyboard();
        onView(withId(R.id.payButton)).perform(click());
        checkToastAppears("Your token is ");
    }

    @Test
    public void testGetTokenVerifyInWebView() throws Exception {
        Start start = new Start(LIVE_OPEN_KEY);

        WaitForResultTokenCallback callback = new WaitForResultTokenCallback();
        start.createToken(activityRule.getActivity(), new Card("5453010000064154", "111", 12, 2020, "John Doe"), callback, 100, "USD");
        callback.waitForResult();

        Token token = callback.getToken();
        assertNotNull(token);
        assertTrue(token.isVerificationRequired());
        TokenVerification verification = token.getVerification();
        assertNotNull(verification);
        assertNotNull(verification.getId());
        assertTrue(verification.isEnrolled());
        assertTrue(verification.isFinalized());
    }

    @Test
    public void testGetTokenVerifyInWebViewSubmitForm() throws Exception {
        Start start = new Start(LIVE_OPEN_KEY);
        MainActivity mainActivity = activityRule.getActivity();
        mainActivity.start = start;

        fillCard("5453010000064154", "12", "2020", "111", "John Doe");
        closeSoftKeyboard();
        onView(withId(R.id.payButton)).perform(click());
        checkToastAppears("Your token is ");
    }

    private void checkToastAppears(String textPrefix) {
        onView(startsWithText(textPrefix)).inRoot(withDecorView(not(is(activityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    private void fillCard(String number, String month, String year, String cvc, String owner) {
        onView(withId(R.id.numberEditText)).perform(typeText(number));
        onView(withId(R.id.monthEditText)).perform(typeText(month));
        onView(withId(R.id.yearEditText)).perform(typeText(year));
        onView(withId(R.id.cvcEditText)).perform(typeText(cvc));
        onView(withId(R.id.ownerEditText)).perform(typeText(owner));
    }

}