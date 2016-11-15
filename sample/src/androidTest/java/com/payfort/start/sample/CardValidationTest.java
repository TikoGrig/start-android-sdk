package com.payfort.start.sample;

import android.support.test.espresso.ViewAssertion;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.payfort.start.sample.support.ViewMatchersExt.hasNotErrorText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CardValidationTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule(MainActivity.class);

    @Test
    public void checkCardValidationWithInvalidFields() {
        fillCard("4111111111111112", "13", "2000", "11", "");
        closeSoftKeyboard();
        onView(withId(R.id.payButton)).perform(click());

        ViewAssertion errorTextMather = matches(hasErrorText("Invalid field"));
        onView(withId(R.id.numberEditText)).check(errorTextMather);
        onView(withId(R.id.monthEditText)).check(errorTextMather);
        onView(withId(R.id.yearEditText)).check(errorTextMather);
        onView(withId(R.id.cvcEditText)).check(errorTextMather);
        onView(withId(R.id.ownerEditText)).check(errorTextMather);
    }

    @Test
    public void checkCardValidationWithValidFields() {
        fillCard("4111111111111111", "11", "2020", "111", "John");
        closeSoftKeyboard();
        onView(withId(R.id.payButton)).perform(click());

        onView(withId(R.id.numberEditText)).check(matches(hasNotErrorText()));
        onView(withId(R.id.monthEditText)).check(matches(hasNotErrorText()));
        onView(withId(R.id.yearEditText)).check(matches(hasNotErrorText()));
        onView(withId(R.id.cvcEditText)).check(matches(hasNotErrorText()));
        onView(withId(R.id.ownerEditText)).check(matches(hasNotErrorText()));
    }

    private void fillCard(String number, String month, String year, String cvc, String owner) {
        onView(withId(R.id.numberEditText)).perform(typeText(number));
        onView(withId(R.id.monthEditText)).perform(typeText(month));
        onView(withId(R.id.yearEditText)).perform(typeText(year));
        onView(withId(R.id.cvcEditText)).perform(typeText(cvc));
        onView(withId(R.id.ownerEditText)).perform(typeText(owner));
    }
}