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
import uk.co.nelsonwright.londonundergroundstatus.shared.RxImmediateSchedulerRule
import uk.co.nelsonwright.londonundergroundstatus.shared.TimeHelper
import uk.co.nelsonwright.londonundergroundstatus.shared.observeOnce
import java.util.*


private const val APP_ID = "APP_ID"
private const val APP_KEY = "APP_KEY"

class TflRepositoryTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val schedulers = RxImmediateSchedulerRule()

    private val statusSuspended = TubeLineStatus("Part Suspended", "A 6 minute service is operating")
    private val statusClosure = TubeLineStatus("Planned Closure", "No service due to  operational restrictions")
    private val expectedTubeLinesList = listOf<TubeLine>(
        TubeLine("bakerloo", "Bakerloo", listOf(statusSuspended)),
        TubeLine("bakerloo", "Circle", listOf(statusSuspended, statusClosure))
    )

    private val mockApi = mockk<TflApiInterface>()
    private lateinit var repo: TflRepository
    private val mockTimeHelper = mockk<TimeHelper>()
    private val currentDateTime = Calendar.getInstance()
    private val calendarUtils = CalendarUtils(mockTimeHelper)


    @Before
    fun setup() {
        currentDateTime.set(2020, Calendar.MAY, 2, 13, 34)
        every { mockTimeHelper.getCurrentDateTime() } returns currentDateTime
        every { mockApi.getLinesStatusNow(APP_ID, APP_KEY) } returns getTubeLinesObservable()
        every { mockApi.getLinesStatusForWeekend(APP_ID, APP_KEY, any(), any()) } returns getTubeLinesObservable()
        repo = TflRepository(mockApi, calendarUtils)
    }

    @Test
    fun shouldRequestTubeLinesForNow() {
        repo.loadTubeLinesForNow(APP_ID, APP_KEY)
        verify { mockApi.getLinesStatusNow(APP_ID, APP_KEY) }
    }

    @Test
    fun shouldRequestTubeLinesForWeekend() {
        repo.loadTubeLinesForWeekend(APP_ID, APP_KEY)
        verify { mockApi.getLinesStatusForWeekend(APP_ID, APP_KEY, any(), any()) }
    }

    @Test
    fun shouldReturnTubeLines() {
        repo.loadTubeLinesForNow(APP_ID, APP_KEY)

        repo.getTubeLines().observeOnce {
            assertThat(it.tubeLines).isEqualTo(expectedTubeLinesList)
        }
    }

    private fun getTubeLinesObservable(): Observable<List<TubeLine>> {
        return Observable.just(expectedTubeLinesList)
    }

}