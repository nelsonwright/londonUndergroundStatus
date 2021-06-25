package uk.co.nelsonwright.londonundergroundstatus.api

import uk.co.nelsonwright.londonundergroundstatus.models.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.models.TubeLinesWithRefreshTime
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import uk.co.nelsonwright.londonundergroundstatus.shared.TimeHelper
import uk.co.nelsonwright.londonundergroundstatus.ui.main.SelectionType
import uk.co.nelsonwright.londonundergroundstatus.ui.main.SelectionType.*
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

    private var linesCache: Map<SelectionType, TubeLinesWithRefreshTime> = mapOf(
        NOW to TubeLinesWithRefreshTime(),
        TOMORROW to TubeLinesWithRefreshTime(),
        WEEKEND to TubeLinesWithRefreshTime()
    )

    override suspend fun loadTubeLines(
        selectionType: SelectionType,
        useCacheRequest: Boolean
    ): TubeLinesWithRefreshTime {
        var refreshedTubeLines = emptyList<TubeLine>()

        return if (canUseCache(useCacheRequest, selectionType)) {
            TubeLinesWithRefreshTime(
                tubeLines = linesCache[selectionType]?.tubeLines ?: emptyList(),
                refreshTime = linesCache[selectionType]?.refreshTime
                    ?: timeHelper.getCurrentLocalDateTime()
            )
        } else {
            when (selectionType) {
                NOW -> refreshedTubeLines =
                    api.getLinesStatusNow(APPLICATION_KEY).map { it.toModel() }

                TOMORROW -> {
                    val (startOfTomorrowDateString, endOfTomorrowDateString) = calendarUtils.getTomorrowDates()

                    refreshedTubeLines = api.getLinesStatusForDateRange(
                        APPLICATION_KEY,
                        startOfTomorrowDateString,
                        endOfTomorrowDateString
                    )
                        .map { api ->
                            api.toModel()
                        }
                }
                WEEKEND -> {
                    val (saturdayDateString, sundayDateString) = calendarUtils.getWeekendDates()
                    refreshedTubeLines = api.getLinesStatusForDateRange(
                        APPLICATION_KEY,
                        saturdayDateString,
                        sundayDateString
                    )
                        .map { api ->
                            api.toModel()
                        }
                }
            }

            linesCache[selectionType]?.refreshTime = timeHelper.getCurrentLocalDateTime()
            linesCache[selectionType]?.tubeLines = refreshedTubeLines
            TubeLinesWithRefreshTime(
                tubeLines = refreshedTubeLines,
                refreshTime = timeHelper.getCurrentLocalDateTime()
            )
        }
    }

    private fun canUseCache(useCacheRequest: Boolean, selectionType: SelectionType): Boolean {
        linesCache[selectionType]?.let {
            if (it.tubeLines.isEmpty()) return false
        }

        val expiryTime = linesCache[selectionType]?.refreshTime?.plusMinutes(
            when (selectionType) {
                NOW -> NOW_CACHE_TIME_MINUTES
                TOMORROW -> TOMORROW_CACHE_TIME_MINUTES
                WEEKEND -> WEEKEND_CACHE_TIME_MINUTES
            }
        )
        val timeNow = timeHelper.getCurrentLocalDateTime()
        return useCacheRequest && timeNow.isBefore(expiryTime)
    }
}
