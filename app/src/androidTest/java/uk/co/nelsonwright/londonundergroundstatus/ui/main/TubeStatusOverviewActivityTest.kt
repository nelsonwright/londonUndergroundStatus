package uk.co.nelsonwright.londonundergroundstatus.ui.main

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.PerformException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLineStatus

const val ITEM_BELOW_THE_FOLD = 14

@RunWith(AndroidJUnit4::class)
@LargeTest
class TubeStatusOverviewActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(TubeStatusOverviewActivity::class.java)

    private val expectedTubeLines = listOf(
        "Bakerloo",
        "Central",
        "Circle",
        "District",
        "DLR",
        "Hammersmith & City",
        "Jubilee",
        "London Overground",
        "Metropolitan",
        "Northern",
        "Piccadilly",
        "TfL Rail",
        "Tram",
        "Victoria",
        "Waterloo & City"
    )

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun cleanup() {
        Intents.release()
    }

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

    @Test
    fun showsActionBarOverflowRefresh() {
        // Open the options menu OR open the overflow menu, depending on whether
        // the device has a hardware or software overflow menu button.
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())

        onView(withText("Refresh"))
            .check(matches(isDisplayed()))
    }

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

    @Test(expected = PerformException::class)
    fun shouldNotDisplayUnknownLine() {
        // Attempt to scroll to an item that contains the non-existent text.
        onView(withId(R.id.lines_recycler_view))
            .perform(
                // scrollTo will fail the test if no item matches.
                RecyclerViewActions.scrollTo<ViewHolder>(
                    hasDescendant(withText("unknown tube line"))
                )
            )
    }

    @Test
    fun shouldFindBakerloo() {
        expectedTubeLines.forEach { tubeName ->
            // Attempt to scroll to an item that contains the specified text.
            onView(withId(R.id.lines_recycler_view))
                .perform(
                    // scrollTo will fail the test if no item matches.
                    RecyclerViewActions.scrollTo<ViewHolder>(
                        hasDescendant(withText(tubeName))
                    )
                )
        }
    }

    @Test
    fun shouldStartWaterlooAndCityActivity() {
        // First, scroll to the position that needs to be matched and click on it.
        onView(withId(R.id.lines_recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(
                    ITEM_BELOW_THE_FOLD,
                    click()
                )
            )

        intended(
            allOf(
                hasComponent(TubeStatusDetailsActivity::class.java.name),
                hasExtra(EXTRA_TUBE_LINE, expectedWaterlooTubeLine()),
                hasExtra(EXTRA_LINE_COLOUR, "#70c3ce")
            )
        )
    }

    @Test
    fun shouldShowFooterCorrectly() {
        // First, scroll to the bottom . . .
        onView(withId(R.id.lines_recycler_view))
            .perform(
                RecyclerViewActions.scrollTo<ViewHolder>(
                    hasDescendant(withSubstring("Contains OS data"))
                )
            )

        onView(withText("Powered by TfL Open Data"))
            .check(isCompletelyBelow(withText("Waterloo & City")))

        onView(withSubstring("Contains OS data"))
            .check(isCompletelyBelow(withText("Powered by TfL Open Data")))
    }

    private fun expectedWaterlooTubeLine(): TubeLine {
        return TubeLine(
            id = "waterloo-city",
            name = "Waterloo & City",
            lineStatuses = listOf(
                TubeLineStatus(
                    statusSeverity = 4,
                    severityDescription = "Planned Closure",
                    reason = "WATERLOO & CITY LINE: No service until further notice."
                )
            )
        )
    }
}
