package uk.co.nelsonwright.londonundergroundstatus.api

import uk.co.nelsonwright.londonundergroundstatus.models.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.models.TubeLinesWithRefreshTime
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import uk.co.nelsonwright.londonundergroundstatus.shared.TimeHelper
import uk.co.nelsonwright.londonundergroundstatus.ui.main.SelectionType
import uk.co.nelsonwright.londonundergroundstatus.ui.main.SelectionType.*
import java.time.Duration.ofMinutes
import java.time.LocalDateTime
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

const val NOW_CACHE_TIME_MINUTES = 2L
const val TOMORROW_CACHE_TIME_MINUTES = 5L
const val WEEKEND_CACHE_TIME_MINUTES = 5L

interface TflRepository {
    suspend fun loadTubeLines(
        selectionType: SelectionType = NOW,
        useCacheRequest: Boolean = true
    ): TubeLinesWithRefreshTime
}

@Singleton
class TflRepositoryImpl @Inject constructor(
    private val api: TflApiInterface,
    private val calendarUtils: CalendarUtils,
    private val timeHelper: TimeHelper
) : TflRepository {

    private var cachedTubeLinesNow: List<TubeLine>? = null
    private var cachedTubeLinesWeekend: List<TubeLine>? = null
    private var cachedTubeLinesTomorrow: List<TubeLine>? = null
    private var lastCachedTimeLinesNow: LocalDateTime? = null
    private var lastCachedTimeLinesWeekend: LocalDateTime? = null
    private var lastCachedTimeLinesTomorrow: LocalDateTime? = null

    override suspend fun loadTubeLines(
        selectionType: SelectionType,
        useCacheRequest: Boolean
    ): TubeLinesWithRefreshTime {
        return when (selectionType) {
            NOW -> loadTubeLinesForNow(useCacheRequest)
            TOMORROW -> loadTubeLinesForTomorrow(useCacheRequest)
            WEEKEND -> loadTubeLinesForWeekend(useCacheRequest)
        }
    }

    private suspend fun loadTubeLinesForNow(useCacheRequest: Boolean): TubeLinesWithRefreshTime {
        return if (canUseCacheForNowLines(useCacheRequest)) {
            TubeLinesWithRefreshTime(
                tubeLines = cachedTubeLinesNow as List<TubeLine>,
                refreshTime = lastCachedTimeLinesNow ?: timeHelper.getCurrentLocalDateTime()
            )
        } else {
            cachedTubeLinesNow = api.getLinesStatusNow(APPLICATION_KEY).map { api ->
                api.toModel()
            }
            lastCachedTimeLinesNow = timeHelper.getCurrentLocalDateTime()
            TubeLinesWithRefreshTime(
                tubeLines = cachedTubeLinesNow as List<TubeLine>,
                refreshTime = timeHelper.getCurrentLocalDateTime()
            )
        }
    }

    private fun canUseCacheForNowLines(useCacheRequest: Boolean): Boolean {
        if (cachedTubeLinesNow == null) return false

        val expiryTime = lastCachedTimeLinesNow?.plusMinutes(NOW_CACHE_TIME_MINUTES)
        val timeNow = timeHelper.getCurrentLocalDateTime()
        return useCacheRequest && timeNow.isBefore(expiryTime)
    }

    private suspend fun loadTubeLinesForWeekend(useCacheRequest: Boolean): TubeLinesWithRefreshTime {
        return if (canUseCacheForWeekendLines(useCacheRequest)) {
            TubeLinesWithRefreshTime(
                tubeLines = cachedTubeLinesWeekend as List<TubeLine>,
                refreshTime = lastCachedTimeLinesWeekend ?: timeHelper.getCurrentLocalDateTime()
            )
        } else {
            val (saturdayDateString, sundayDateString) = calendarUtils.getWeekendDates()
            cachedTubeLinesWeekend = api.getLinesStatusForDateRange(APPLICATION_KEY, saturdayDateString, sundayDateString)
                .map {
                    it.toModel()
                }
            lastCachedTimeLinesWeekend = timeHelper.getCurrentLocalDateTime()
            TubeLinesWithRefreshTime(
                tubeLines = cachedTubeLinesWeekend as List<TubeLine>,
                refreshTime = timeHelper.getCurrentLocalDateTime()
            )
        }
    }

    private fun canUseCacheForWeekendLines(useCacheRequest: Boolean): Boolean {
        if (cachedTubeLinesWeekend == null) return false

        val expiryTime = lastCachedTimeLinesWeekend?.plusMinutes(WEEKEND_CACHE_TIME_MINUTES)
        val timeNow = timeHelper.getCurrentLocalDateTime()
        return useCacheRequest && timeNow.isBefore(expiryTime)
    }

    private suspend fun loadTubeLinesForTomorrow(useCacheRequest: Boolean): TubeLinesWithRefreshTime {
        return if (canUseCacheForTomorrowLines(useCacheRequest)) {
            TubeLinesWithRefreshTime(
                tubeLines = cachedTubeLinesTomorrow as List<TubeLine>,
                refreshTime = lastCachedTimeLinesTomorrow ?: timeHelper.getCurrentLocalDateTime()
            )
        } else {
            val (startOfTomorrowDateString, endOfTomorrowDateString) = calendarUtils.getTomorrowDates()
            cachedTubeLinesTomorrow = api.getLinesStatusForDateRange(APPLICATION_KEY, startOfTomorrowDateString, endOfTomorrowDateString)
                .map {
                    it.toModel()
                }
            lastCachedTimeLinesTomorrow = timeHelper.getCurrentLocalDateTime()
            TubeLinesWithRefreshTime(
                tubeLines = cachedTubeLinesTomorrow as List<TubeLine>,
                refreshTime = timeHelper.getCurrentLocalDateTime()
            )
        }
    }

    private fun canUseCacheForTomorrowLines(useCacheRequest: Boolean): Boolean {
        if (cachedTubeLinesTomorrow == null) return false

        val expiryTime = lastCachedTimeLinesTomorrow?.plusMinutes(TOMORROW_CACHE_TIME_MINUTES)
        val timeNow = timeHelper.getCurrentLocalDateTime()
        return useCacheRequest && timeNow.isBefore(expiryTime)
    }
}
