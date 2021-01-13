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
import uk.co.nelsonwright.londonundergroundstatus.shared.TimeHelper
import uk.co.nelsonwright.londonundergroundstatus.ui.main.SelectionType.NOW
import uk.co.nelsonwright.londonundergroundstatus.ui.main.SelectionType.WEEKEND
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.xml.datatype.DatatypeConstants.JANUARY


private const val FORMATTED_NOW_DATE = "formatted now date"

@ExperimentalCoroutinesApi
class TflRepositoryTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val mockApi = mockk<TflApiInterface>()
    private val mockTimeHelper = mockk<TimeHelper>()
    private lateinit var repo: TflRepository
    private val calendarUtils = mockk<CalendarUtils>()
    private val weekendPair = Pair("Saturday", "Sunday")
    private val theCurrentTime = ZonedDateTime.of(2021, JANUARY, 13, 10, 2, 0, 0, ZoneId.of("Z"))

    @Before
    fun setup() {
        coEvery { mockApi.getLinesStatusNow(any()) } returns stubbedTubeLines()
        coEvery {
            mockApi.getLinesStatusForWeekend(
                    any(),
                    any(),
                    any(),
                    any()
            )
        } returns stubbedTubeLines()
        every { calendarUtils.getFormattedLocateDateTime() } returns FORMATTED_NOW_DATE
        every { calendarUtils.getWeekendDates() } returns weekendPair
        every { mockTimeHelper.getCurrentDateTime() } returns theCurrentTime
        repo = TflRepositoryImpl(mockApi, calendarUtils, mockTimeHelper)
    }

    @Test
    fun shouldRequestTubeLinesForNow() = runBlockingTest {
        repo.loadTubeLines(selectionType = NOW, useCacheRequest = false)
        coVerify { mockApi.getLinesStatusNow(APPLICATION_KEY) }
    }

    @Test
    fun shouldUseCachedTubeLinesForNow() = runBlockingTest {
        repo.loadTubeLines(selectionType = NOW, useCacheRequest = false)
        repo.loadTubeLines(selectionType = NOW, useCacheRequest = true)
        coVerify(exactly = 1) { mockApi.getLinesStatusNow(APPLICATION_KEY) }
    }

    @Test
    fun shouldNotUseExpiredCacheForNow() = runBlockingTest {
        repo.loadTubeLines(selectionType = NOW, useCacheRequest = false)
        every { mockTimeHelper.getCurrentDateTime() } returns theCurrentTime.plusMinutes(NOW_CACHE_TIME_MINUTES + 1)

        repo.loadTubeLines(selectionType = NOW, useCacheRequest = true)

        coVerify(exactly = 2) { mockApi.getLinesStatusNow(APPLICATION_KEY) }
    }

    @Test
    fun shouldRequestTubeLinesForWeekend() = runBlockingTest {
        repo.loadTubeLines(selectionType = WEEKEND, useCacheRequest = false)

        coVerify {
            mockApi.getLinesStatusForWeekend(
                    APPLICATION_KEY,
                    weekendPair.first,
                    weekendPair.second
            )
        }
    }

    @Test
    fun shouldUseCachedTubeLinesForWeekend() = runBlockingTest {
        repo.loadTubeLines(selectionType = WEEKEND, useCacheRequest = false)
        repo.loadTubeLines(selectionType = WEEKEND, useCacheRequest = true)

        coVerify(exactly = 1) {
            mockApi.getLinesStatusForWeekend(
                    APPLICATION_KEY,
                    weekendPair.first,
                    weekendPair.second
            )
        }
    }

    @Test
    fun shouldNotUseExpiredCacheForWeekend() = runBlockingTest {
        repo.loadTubeLines(selectionType = WEEKEND, useCacheRequest = false)
        every { mockTimeHelper.getCurrentDateTime() } returns theCurrentTime.plusMinutes(WEEKEND_CACHE_TIME_MINUTES + 1)

        repo.loadTubeLines(selectionType = WEEKEND, useCacheRequest = true)

        coVerify(exactly = 2) {
            mockApi.getLinesStatusForWeekend(
                    APPLICATION_KEY,
                    weekendPair.first,
                    weekendPair.second
            )
        }
    }

    private fun stubbedTubeLines(): List<ApiTubeLine> {
        return listOf(ApiTubeLine())
    }
}