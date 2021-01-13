package uk.co.nelsonwright.londonundergroundstatus.api

import uk.co.nelsonwright.londonundergroundstatus.models.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import uk.co.nelsonwright.londonundergroundstatus.shared.TimeHelper
import java.time.Duration.ofMinutes
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

const val NOW_CACHE_TIME_MINUTES = 2L
const val WEEKEND_CACHE_TIME_MINUTES = 5L

interface TflRepository {
    suspend fun loadTubeLines(isNowSelected: Boolean = true, useCacheRequest: Boolean = true): List<TubeLine>
}

@Singleton
class TflRepositoryImpl @Inject constructor(private val api: TflApiInterface, private val calendarUtils:
CalendarUtils, private val timeHelper: TimeHelper) :
        TflRepository {

    private var cachedTubeLinesNow: List<TubeLine>? = null
    private var cachedTubeLinesWeekend: List<TubeLine>? = null
    private var lastCachedTimeLinesNow: ZonedDateTime? = null
    private var lastCachedTimeLinesWeekend: ZonedDateTime? = null

    override suspend fun loadTubeLines(isNowSelected: Boolean, useCacheRequest: Boolean): List<TubeLine> {
        return if (isNowSelected) {
            loadTubeLinesForNow(useCacheRequest)
        } else {
            loadTubeLinesForWeekend(useCacheRequest)
        }
    }

    private suspend fun loadTubeLinesForNow(useCacheRequest: Boolean): List<TubeLine> {
        return if (canUseCacheForNowLines(useCacheRequest)) {
            cachedTubeLinesNow ?: throw IllegalArgumentException("Something has gone wrong with the cache")
        } else {
            cachedTubeLinesNow = api.getLinesStatusNow(APPLICATION_KEY).map { api ->
                api.toModel()
            }
            lastCachedTimeLinesNow = timeHelper.getCurrentDateTime()
            return cachedTubeLinesNow as List<TubeLine>
        }
    }

    private fun canUseCacheForNowLines(useCacheRequest: Boolean): Boolean {
        if (cachedTubeLinesNow == null) return false

        val expiryTime = lastCachedTimeLinesNow?.plus(ofMinutes(NOW_CACHE_TIME_MINUTES))
        val timeNow = timeHelper.getCurrentDateTime()
        return useCacheRequest && timeNow.isBefore(expiryTime)
    }

    private suspend fun loadTubeLinesForWeekend(useCacheRequest: Boolean): List<TubeLine> {
        return if (canUseCacheForWeekendLines(useCacheRequest)) {
            cachedTubeLinesWeekend ?: throw IllegalArgumentException("Something has gone wrong with the cache")
        } else {
            val (saturdayDateString, sundayDateString) = calendarUtils.getWeekendDates()
            cachedTubeLinesWeekend = api.getLinesStatusForWeekend(APPLICATION_KEY, saturdayDateString, sundayDateString)
                    .map {
                        it.toModel()
                    }
            lastCachedTimeLinesWeekend = timeHelper.getCurrentDateTime()
            cachedTubeLinesWeekend as List<TubeLine>
        }
    }

    private fun canUseCacheForWeekendLines(useCacheRequest: Boolean): Boolean {
        if (cachedTubeLinesWeekend == null) return false

        val expiryTime = lastCachedTimeLinesWeekend?.plus(ofMinutes(WEEKEND_CACHE_TIME_MINUTES))
        val timeNow = timeHelper.getCurrentDateTime()
        return useCacheRequest && timeNow.isBefore(expiryTime)
    }
}
