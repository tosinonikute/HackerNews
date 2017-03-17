package com.hackernewsapp.story;


import android.os.SystemClock;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.hackernewsapp.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class OpenStoryInChromeTabTest {

    private IdlingResource idlingResource;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void openStoryInChromeTabTest() {

        waitFor(10000); // wait for RecyclerView to finish loading

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.stories_recyclerview),
                        withParent(allOf(withId(R.id.swipeContainer),
                                withParent(withId(R.id.layout_story_root)))),
                        isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        waitFor(5000); // wait for discussion activity to finish loading

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.float_button),
                        withParent(allOf(withId(R.id.main_comment_content),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        floatingActionButton.perform(click());

        SystemClock.sleep(10000); // sleep the system clock
        cleanUp(); // cleanup

    }

    public void waitFor(long waitingTime) {
        //SystemClock.sleep(waitingTime);  // This is replaced with IdlingResource.

        // Make sure Espresso does not time out
        IdlingPolicies.setMasterPolicyTimeout(waitingTime * 2, TimeUnit.MILLISECONDS);
        IdlingPolicies.setIdlingResourceTimeout(waitingTime * 2, TimeUnit.MILLISECONDS);
        // Now we wait

        idlingResource = new ElapsedTimeIdlingResource(waitingTime);
        Espresso.registerIdlingResources(idlingResource);
    }

    public void cleanUp(){
        // Clean up
        Espresso.unregisterIdlingResources(idlingResource);
    }

}
