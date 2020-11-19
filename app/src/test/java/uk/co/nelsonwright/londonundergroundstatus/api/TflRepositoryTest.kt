package uk.co.nelsonwright.londonundergroundstatus.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils


private const val FORMATTED_NOW_DATE = "formatted now date"

@ExperimentalCoroutinesApi
class TflRepositoryTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val mockApi = mockk<TflApiInterface>()
    private lateinit var repo: TflRepository
    private val calendarUtils = mockk<CalendarUtils>()
    private val weekendPair = Pair("Saturday", "Sunday")

    @Before
    fun setup() {
        coEvery { mockApi.getLinesStatusNow(any(), any()) } returns stubbedTubeLines()
        coEvery { mockApi.getLinesStatusForWeekend(any(), any(), any(), any()) } returns stubbedTubeLines()
        every { calendarUtils.getFormattedLocateDateTime() } returns FORMATTED_NOW_DATE
        every { calendarUtils.getWeekendDates() } returns weekendPair
        repo = TflRepositoryImpl(mockApi, calendarUtils)
    }

    @Test
    fun shouldRequestTubeLinesForNow() = runBlockingTest {
        repo.loadTubeLines(isWeekendSelected = false)
        coVerify { mockApi.getLinesStatusNow(APPLICATION_ID, APPLICATION_KEY) }
    }

    @Test
    fun shouldRequestTubeLinesForWeekend() = runBlockingTest {
        repo.loadTubeLines(isWeekendSelected = true)

        coVerify {
            mockApi.getLinesStatusForWeekend(
                APPLICATION_ID,
                APPLICATION_KEY,
                weekendPair.first,
                weekendPair.second
            )
        }
    }

    private fun stubbedTubeLines(): List<TubeLine> {
        return listOf(TubeLine())
    }
}