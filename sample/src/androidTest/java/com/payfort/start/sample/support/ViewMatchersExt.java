package com.payfort.start.sample.support;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.widget.EditText;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class ViewMatchersExt {

    public static Matcher<View> hasNotErrorText() {
        return new BoundedMatcher<View, EditText>(EditText.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("has not error text");
            }

            @Override
            protected boolean matchesSafely(EditText view) {
                return view.getError() == null;
            }
        };
    }

    public static Matcher<View> startsWithText(String prefix) {
        return withText(Matchers.startsWith(prefix));
    }

}
