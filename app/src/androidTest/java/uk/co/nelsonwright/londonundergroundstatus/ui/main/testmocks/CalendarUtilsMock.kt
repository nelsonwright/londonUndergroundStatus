package uk.co.nelsonwright.londonundergroundstatus.ui.main.testmocks

import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import java.time.LocalDateTime

private const val LOCAL_DATE_TIME = "a date time that is local"

class CalendarUtilsMock : CalendarUtils {
    override fun getWeekendDates(): Pair<String, String> {
        return Pair("Sat 11th", "Sun 12th")
    }

    override fun getTomorrowDates(): Pair<String, String> {
        return Pair("Wed 2nd", "Wed 2nd 23:59")
    }

    override fun getFormattedSaturdayDate(): String {
        return "Sat 11th"
    }

    override fun getFormattedLocateDateTime(dateToFormat: LocalDateTime): String {
        return LOCAL_DATE_TIME
    }
}