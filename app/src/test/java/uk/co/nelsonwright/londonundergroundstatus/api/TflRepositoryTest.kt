package uk.co.nelsonwright.londonundergroundstatus.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import uk.co.nelsonwright.londonundergroundstatus.testutils.RxImmediateSchedulerRule
import uk.co.nelsonwright.londonundergroundstatus.testutils.observeOnce


private const val FORMATTED_NOW_DATE = "formatted now date"

class TflRepositoryTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val schedulers = RxImmediateSchedulerRule()

    private val statusSuspended = TubeLineStatus(4, "Part Suspended", "A 6 minute service is operating")
    private val statusClosure = TubeLineStatus(4, "Planned Closure", "No service due to  operational restrictions")
    private val expectedTubeLinesList = listOf(
        TubeLine("bakerloo", "Bakerloo", listOf(statusSuspended)),
        TubeLine("circle", "Circle", listOf(statusSuspended, statusClosure))
    )

    private val mockApi = mockk<TflApiInterface>()
    private lateinit var repo: TflRepository
    private val calendarUtils = mockk<CalendarUtils>()
    private val weekendPair = Pair("Saturday", "Sunday")


    @Before
    fun setup() {
        every { mockApi.getLinesStatusNow(any(), any()) } returns getTubeLinesObservable()
        every { mockApi.getLinesStatusForWeekend(any(), any(), any(), any()) } returns getTubeLinesObservable()
        every { calendarUtils.getFormattedLocateDateTime() } returns FORMATTED_NOW_DATE
        every { calendarUtils.getWeekendDates() } returns weekendPair
        repo = TflRepositoryImpl(mockApi, calendarUtils)
    }

    @Test
    fun shouldRequestTubeLinesForNow() {
        repo.loadTubeLinesForNow()
        verify { mockApi.getLinesStatusNow(APPLICATION_ID, APPLICATION_KEY) }
    }

    @Test
    fun shouldRequestTubeLinesForWeekend() {
        repo.loadTubeLinesForWeekend()
        verify {
            mockApi.getLinesStatusForWeekend(
                APPLICATION_ID,
                APPLICATION_KEY,
                weekendPair.first,
                weekendPair.second
            )
        }
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