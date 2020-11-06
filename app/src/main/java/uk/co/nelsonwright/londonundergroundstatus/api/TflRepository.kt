package uk.co.nelsonwright.londonundergroundstatus.api

import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import javax.inject.Singleton

interface TflRepository {
    suspend fun loadTubeLinesForNow(): List<TubeLine>
    suspend fun loadTubeLinesForWeekend(): List<TubeLine>
}

@Singleton
class TflRepositoryImpl(val api: TflApiInterface, private val calendarUtils: CalendarUtils) : TflRepository {
    override suspend fun loadTubeLinesForNow(): List<TubeLine> {
        return api.getLinesStatusNow(APPLICATION_ID, APPLICATION_KEY)
    }

    override suspend fun loadTubeLinesForWeekend(): List<TubeLine> {
        val (saturdayDateString, sundayDateString) = calendarUtils.getWeekendDates()
        return api.getLinesStatusForWeekend(APPLICATION_ID, APPLICATION_KEY, saturdayDateString, sundayDateString)
    }
}
