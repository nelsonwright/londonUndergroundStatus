package uk.co.nelsonwright.londonundergroundstatus.api

import uk.co.nelsonwright.londonundergroundstatus.models.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import javax.inject.Inject
import javax.inject.Singleton

interface TflRepository {
    suspend fun loadTubeLines(isWeekendSelected: Boolean = false): List<TubeLine>
}

@Singleton
class TflRepositoryImpl @Inject constructor(val api: TflApiInterface, private val calendarUtils: CalendarUtils) :
    TflRepository {

    override suspend fun loadTubeLines(isWeekendSelected: Boolean): List<TubeLine> {
        return if (isWeekendSelected) {
            loadTubeLinesForWeekend()
        } else {
            loadTubeLinesForNow()
        }
    }

    private suspend fun loadTubeLinesForNow(): List<TubeLine> {
        return api.getLinesStatusNow(APPLICATION_KEY).map { api ->
            api.toModel()
        }
    }

    private suspend fun loadTubeLinesForWeekend(): List<TubeLine> {
        val (saturdayDateString, sundayDateString) = calendarUtils.getWeekendDates()
        return api.getLinesStatusForWeekend(APPLICATION_KEY, saturdayDateString, sundayDateString)
            .map {
                it.toModel()
            }
    }
}
