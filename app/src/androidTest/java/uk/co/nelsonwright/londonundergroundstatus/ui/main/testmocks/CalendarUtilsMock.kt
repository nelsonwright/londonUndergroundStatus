package uk.co.nelsonwright.londonundergroundstatus.ui.main.testmocks

import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import java.time.LocalDateTime

private const val LOCAL_DATE_TIME = "a date time that is local"

class CalendarUtilsMock : CalendarUtils {
    override fun getWeekendDates(): Pair<String, String> {
        return Pair("Sat 11th", "Sun 12th")
    }

    override fun getFormattedSaturdayDate(): String {
        return "Sat 11th"
    }

    override fun getFormattedLocateDateTime(dateToFormat: LocalDateTime): String {
        return LOCAL_DATE_TIME
    }
}