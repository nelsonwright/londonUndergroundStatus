package uk.co.nelsonwright.londonundergroundstatus.ui.main

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.CoreMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uk.co.nelsonwright.londonundergroundstatus.R


@RunWith(AndroidJUnit4::class)
@LargeTest
class TubeStatusOverviewActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(TubeStatusOverviewActivity::class.java)

    @Test
    fun showsRefreshDateLabel() {
        onView(withId(R.id.refresh_date)).check(matches(isDisplayed()))
    }

    @Test
    fun showsStatusForNowAsDefault() {
        onView(withId(R.id.status_date_spinner)).perform(click())

        onData(`is`(instanceOf(String::class.java)))
            .atPosition(0)
            .perform(click())

        onView(withId(R.id.now_or_weekend))
            .check(matches(withText("now")))
    }

    @Test
    fun canSelectStatusForWeekend() {
        onView(withId(R.id.status_date_spinner)).perform(click())

        onData(`is`(instanceOf(String::class.java)))
            .atPosition(1)
            .perform(click())

        onView(withId(R.id.now_or_weekend))
            .check(matches(withSubstring("weekend")))
    }

    //hmm, this passes sometimes, but mostly not . . . race condition?
//    @Test
//    fun testActionBarOverflowRefresh() {
//        // Open the options menu OR open the overflow menu, depending on whether
//        // the device has a hardware or software overflow menu button.
//        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
//
//        // Click the item.
//        onView(withText("Refresh"))
//            .perform(click())
//
//        onView(withId(R.id.swipe_refresh))
//            .check(matches(isRefreshing()))
//    }

    @Test
    fun shouldNotShowErrorMessageByDefault() {
        onView(withId(R.id.loading_error_message))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun shouldNotShowErrorButtonByDefault() {
        onView(withId(R.id.refresh_button))
            .check(matches(not(isDisplayed())))
    }
}
