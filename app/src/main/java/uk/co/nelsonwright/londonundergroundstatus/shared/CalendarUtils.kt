package uk.co.nelsonwright.londonundergroundstatus.shared

import java.time.DayOfWeek.SATURDAY
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*

private const val LONDON_TIME_ZONE = "Europe/London"

class CalendarUtils(private val timeHelper: TimeHelper) {

    fun getWeekendDates(): Pair<String, String> {
        val yearMonthDayFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.UK)

        val uTCDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ", Locale.UK)
        val saturdayDate = calculateThisSaturday()

        val saturdayDateString = yearMonthDayFormat.format(saturdayDate)

        // treat the end of the weekend as just before midnight on Sunday...
        val sundayDate = calculateThisSaturday()
            .plusDays(1)
            .withHour(23)
            .withMinute(59)

        val sundayDateString = uTCDateTimeFormat.format(sundayDate)
        return Pair(saturdayDateString, sundayDateString)
    }

    fun getFormattedSaturdayDate(): String {
        // e.g. 2 May or 29 Apr . . .
        val dateMonthFormat = DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())
        return dateMonthFormat.format(calculateThisSaturday())
    }

    fun getFormattedLocateDateTime(): String {
        val date = timeHelper.getCurrentLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("EEE, MMM d HH:mm:ss", Locale.getDefault())
        return formatter.format(date)
    }

    private fun calculateThisSaturday(): ZonedDateTime {
        return timeHelper.getCurrentDateTime(LONDON_TIME_ZONE)
            .with(TemporalAdjusters.next(SATURDAY))
    }
}