package uk.co.nelsonwright.londonundergroundstatus.api

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import uk.co.nelsonwright.londonundergroundstatus.testutils.RxImmediateSchedulerRule
import uk.co.nelsonwright.londonundergroundstatus.testutils.observeOnce


private const val APP_ID = "APP_ID"
private const val APP_KEY = "APP_KEY"
private const val FORMATTED_NOW_DATE = "formatted now date"

class TflRepositoryTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val schedulers = RxImmediateSchedulerRule()

    private val statusSuspended = TubeLineStatus(4, "Part Suspended", "A 6 minute service is operating")
    private val statusClosure = TubeLineStatus(4, "Planned Closure", "No service due to  operational restrictions")
    private val expectedTubeLinesList = listOf<TubeLine>(
        TubeLine("bakerloo", "Bakerloo", listOf(statusSuspended)),
        TubeLine("bakerloo", "Circle", listOf(statusSuspended, statusClosure))
    )

    private val mockApi = mockk<TflApiInterface>()
    private lateinit var repo: TflRepository
    private val calendarUtils = mockk<CalendarUtils>()
    private val weekendPair = Pair<String, String>("Saturday", "Sunday")
    private val mockContext = mockk<Context>()


    @Before
    fun setup() {
        every { mockApi.getLinesStatusNow(APP_ID, APP_KEY) } returns getTubeLinesObservable()
        every { mockApi.getLinesStatusForWeekend(APP_ID, APP_KEY, any(), any()) } returns getTubeLinesObservable()
        every { calendarUtils.getFormattedLocateDateTime() } returns FORMATTED_NOW_DATE
        every { calendarUtils.getWeekendDates() } returns weekendPair
        every { mockContext.getString(R.string.applicationId) } returns APP_ID
        every { mockContext.getString(R.string.applicationKey) } returns APP_KEY
        repo = TflRepository(mockApi, calendarUtils, mockContext)
    }

    @Test
    fun shouldRequestTubeLinesForNow() {
        repo.loadTubeLinesForNow()
        verify { mockApi.getLinesStatusNow(APP_ID, APP_KEY) }
    }

    @Test
    fun shouldRequestTubeLinesForWeekend() {
        repo.loadTubeLinesForWeekend()
        verify { mockApi.getLinesStatusForWeekend(APP_ID, APP_KEY, weekendPair.first, weekendPair.second) }
    }

    @Test
    fun shouldReturnTubeLines() {
        repo.loadTubeLinesForNow()

        repo.getTubeLines().observeOnce {
            assertThat(it.tubeLines).isEqualTo(expectedTubeLinesList)
            assertThat(it.timestamp).isEqualTo(FORMATTED_NOW_DATE)
        }
    }

    private fun getTubeLinesObservable(): Observable<List<TubeLine>> {
        return Observable.just(expectedTubeLinesList)
    }

}