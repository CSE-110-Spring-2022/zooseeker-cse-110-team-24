package com.example.zooseekerteam24;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TestSwitchingBriefAndDetailed {

    @Rule
    public ActivityTestRule<SearchActivity> mActivityTestRule = new ActivityTestRule<>(SearchActivity.class);

    @Test
    public void testSwitchingBriefAndDetailed() {
        ViewInteraction appCompatImageView = onView(
                allOf(withClassName(is("androidx.appcompat.widget.AppCompatImageView")), withContentDescription("Search"),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withId(R.id.searchView),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageView.perform(click());

        ViewInteraction searchAutoComplete = onView(
                allOf(withClassName(is("android.widget.SearchView$SearchAutoComplete")),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete.perform(replaceText("gorilla"), closeSoftKeyboard());

        ViewInteraction searchAutoComplete2 = onView(
                allOf(withClassName(is("android.widget.SearchView$SearchAutoComplete")), withText("gorilla"),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete2.perform(pressImeActionButton());

        ViewInteraction materialTextView = onView(
                allOf(withId(R.id.tvAdded), withText("Add"),
                        childAtPosition(
                                withParent(withId(R.id.lvResults)),
                                1),
                        isDisplayed()));
        materialTextView.perform(click());

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.icPlanner), withContentDescription("Planner"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.btmNavi),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.icDirection), withContentDescription("Direction"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.btmNavi),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.nextBtn), withText("Next"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        4),
                                2),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.nextBtn), withText("Next"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        4),
                                2),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.nextBtn), withText("Next"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        4),
                                2),
                        isDisplayed()));
        materialButton3.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.directionsText), withText("Walk 4500.0 meters along\nHippo Trail from\nTreetops Way / Hippo Trail\nto Monkey Trail / Hippo Trail."),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView.check(matches(withText("Walk 4500.0 meters along\nHippo Trail from\nTreetops Way / Hippo Trail\nto Monkey Trail / Hippo Trail.")));

        ViewInteraction bottomNavigationItemView3 = onView(
                allOf(withId(R.id.icPlanner), withContentDescription("Planner"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.btmNavi),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView3.perform(click());

        ViewInteraction bottomNavigationItemView4 = onView(
                allOf(withId(R.id.icDirection), withContentDescription("Direction"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.btmNavi),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView4.perform(click());

        ViewInteraction switch_ = onView(
                allOf(withId(R.id.directionSwitch), withText("Brief"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        switch_.perform(click());

        ViewInteraction materialButton4 = onView(
                allOf(withId(R.id.nextBtn), withText("Next"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        4),
                                2),
                        isDisplayed()));
        materialButton4.perform(click());

        ViewInteraction materialButton5 = onView(
                allOf(withId(R.id.nextBtn), withText("Next"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        4),
                                2),
                        isDisplayed()));
        materialButton5.perform(click());

        ViewInteraction materialButton6 = onView(
                allOf(withId(R.id.nextBtn), withText("Next"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        4),
                                2),
                        isDisplayed()));
        materialButton6.perform(click());

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.directionsText), withText("Walk 1400.0 meters along\nTreetops Way from\nTreetops Way / Fern Canyon Trail\nto Treetops Way / Orangutan Trail."),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        textView2.check(matches(withText("Walk 1400.0 meters along\nTreetops Way from\nTreetops Way / Fern Canyon Trail\nto Treetops Way / Orangutan Trail.")));

        ViewInteraction bottomNavigationItemView5 = onView(
                allOf(withId(R.id.icPlanner), withContentDescription("Planner"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.btmNavi),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView5.perform(click());

        ViewInteraction materialTextView2 = onView(
                allOf(withId(R.id.tvDelete), withText("x"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.rvPlanner),
                                        0),
                                2),
                        isDisplayed()));
        materialTextView2.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
