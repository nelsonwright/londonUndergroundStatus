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
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.TubeStatusApplication
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.di.DaggerAppComponent
import uk.co.nelsonwright.londonundergroundstatus.ui.main.shared.statusPartSuspended
import uk.co.nelsonwright.londonundergroundstatus.ui.main.shared.statusPlannedClosure
import uk.co.nelsonwright.londonundergroundstatus.ui.main.shared.stubbedTubeLinesNow
import uk.co.nelsonwright.londonundergroundstatus.ui.main.shared.stubbedTubeLinesWeekend
import uk.co.nelsonwright.londonundergroundstatus.ui.main.testmocks.AppModuleMock


@RunWith(AndroidJUnit4::class)
@LargeTest
class TubeStatusActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(TubeStatusActivity::class.java)

    private val expectedTubeLinesNow = stubbedTubeLinesNow()
    private val expectedTubeLinesWeekend = stubbedTubeLinesWeekend()
    private val bottomTubeLine = expectedTubeLinesNow.size - 1

    @Before
    fun setup() {
        Intents.init()

        val tubeApp = InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .applicationContext as TubeStatusApplication

        // hmm, need to rework this, now we no longer inject the serviceLocator in the activity
        val mockedComponent = DaggerAppComponent
            .builder()
            .appModule(AppModuleMock(tubeApp))
            .build()

        tubeApp.setAppComponent(mockedComponent)
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
    fun shouldShowExpectedTubeLinesForNow() {
        expectedTubeLinesNow.forEach {
            // Attempt to scroll to an item that contains the specified text.
            onView(withId(R.id.lines_recycler_view))
                .perform(
                    // scrollTo will fail the test if no item matches.
                    RecyclerViewActions.scrollTo<ViewHolder>(
                        hasDescendant(withText(it.name))
                    )
                )
        }
    }


    // hmm, sometimes this and shouldShowFooterCorrectly are flaky
    @Test
    fun shouldShowExpectedTubeLinesForWeekend() {
        onView(withId(R.id.status_date_spinner)).perform(click())

        // select "weekend" . . .
        onData(instanceOf(String::class.java))
            .atPosition(1)
            .perform(click())

        Thread.sleep(2000)

        expectedTubeLinesWeekend.forEach {
            // Attempt to scroll to an item that contains the specified text.
            onView(withId(R.id.lines_recycler_view))
                .perform(
                    // scrollTo will fail the test if no item matches.
                    RecyclerViewActions.scrollTo<ViewHolder>(
                        hasDescendant(withText(it.name))
                    )
                )
        }
    }


    @Test
    fun shouldStartDetailActivity() {
        // First, scroll to the position that needs to be matched and click on it.
        onView(withId(R.id.lines_recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(
                    bottomTubeLine,
                    click()
                )
            )

//        intended(
//            allOf(
//                hasComponent(TubeStatusDetailsActivity::class.java.name),
//                hasExtra(EXTRA_TUBE_LINE, victoriaTubeLine()),
//                hasExtra(EXTRA_LINE_COLOUR, "#009fe0")
//            )
//        )
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
            .check(isCompletelyBelow(withText("Victoria")))

        onView(withSubstring("Contains OS data"))
            .check(isCompletelyBelow(withText("Powered by TfL Open Data")))
    }

    private fun victoriaTubeLine(): TubeLine {
        return TubeLine(
            id = "victoria",
            name = "Victoria",
            lineStatuses = listOf(statusPartSuspended(), statusPlannedClosure())
        )
    }
}
