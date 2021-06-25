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
import uk.co.nelsonwright.londonundergroundstatus.ui.main.SelectionType.*
import java.time.LocalDateTime
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
    private val tomorrowPair = Pair("tomorrow start", "tomorrow end")
    private val theCurrentTime = ZonedDateTime.of(2021, JANUARY, 13, 10, 2, 0, 0, ZoneId.of("Z"))
    private val theCurrentLocalTime = LocalDateTime.of(2021, JANUARY, 13, 10, 2, 0)

    @Before
    fun setup() {
        coEvery { mockApi.getLinesStatusNow(any()) } returns stubbedTubeLines()
        coEvery {
            mockApi.getLinesStatusForDateRange(
                any(),
                any(),
                any(),
                any()
            )
        } returns stubbedTubeLines()
        every { calendarUtils.getFormattedLocateDateTime(any()) } returns FORMATTED_NOW_DATE
        every { calendarUtils.getWeekendDates() } returns weekendPair
        every { calendarUtils.getTomorrowDates() } returns tomorrowPair
        every { mockTimeHelper.getCurrentDateTime() } returns theCurrentTime
        every { mockTimeHelper.getCurrentLocalDateTime() } returns theCurrentLocalTime
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
        every { mockTimeHelper.getCurrentLocalDateTime() } returns theCurrentLocalTime.plusMinutes(NOW_CACHE_TIME_MINUTES + 1)

        repo.loadTubeLines(selectionType = NOW, useCacheRequest = true)

        coVerify(exactly = 2) { mockApi.getLinesStatusNow(APPLICATION_KEY) }
    }

    @Test
    fun shouldRequestTubeLinesForWeekend() = runBlockingTest {
        repo.loadTubeLines(selectionType = WEEKEND, useCacheRequest = false)

        coVerify {
            mockApi.getLinesStatusForDateRange(
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
            mockApi.getLinesStatusForDateRange(
                    APPLICATION_KEY,
                    weekendPair.first,
                    weekendPair.second
            )
        }
    }

    @Test
    fun shouldNotUseExpiredCacheForWeekend() = runBlockingTest {
        repo.loadTubeLines(selectionType = WEEKEND, useCacheRequest = false)
        every { mockTimeHelper.getCurrentLocalDateTime() } returns theCurrentLocalTime.plusMinutes(WEEKEND_CACHE_TIME_MINUTES + 1)

        repo.loadTubeLines(selectionType = WEEKEND, useCacheRequest = true)

        coVerify(exactly = 2) {
            mockApi.getLinesStatusForDateRange(
                    APPLICATION_KEY,
                    weekendPair.first,
                    weekendPair.second
            )
        }
    }

    @Test
    fun shouldRequestTubeLinesForTomorrow() = runBlockingTest {
        repo.loadTubeLines(selectionType = TOMORROW, useCacheRequest = false)

        coVerify {
            mockApi.getLinesStatusForDateRange(
                APPLICATION_KEY,
                tomorrowPair.first,
                tomorrowPair.second
            )
        }
    }

    @Test
    fun shouldUseCachedTubeLinesForTomorrow() = runBlockingTest {
        repo.loadTubeLines(selectionType = TOMORROW, useCacheRequest = false)
        repo.loadTubeLines(selectionType = TOMORROW, useCacheRequest = true)

        coVerify(exactly = 1) {
            mockApi.getLinesStatusForDateRange(
                APPLICATION_KEY,
                tomorrowPair.first,
                tomorrowPair.second
            )
        }
    }

    @Test
    fun shouldNotUseExpiredCacheForTomorrow() = runBlockingTest {
        repo.loadTubeLines(selectionType = TOMORROW, useCacheRequest = false)
        every { mockTimeHelper.getCurrentLocalDateTime() } returns theCurrentLocalTime.plusMinutes(
            TOMORROW_CACHE_TIME_MINUTES + 1)

        repo.loadTubeLines(selectionType = TOMORROW, useCacheRequest = true)

        coVerify(exactly = 2) {
            mockApi.getLinesStatusForDateRange(
                APPLICATION_KEY,
                tomorrowPair.first,
                tomorrowPair.second
            )
        }
    }

    private fun stubbedTubeLines(): List<ApiTubeLine> {
        return listOf(ApiTubeLine())
    }
}