package uk.co.nelsonwright.londonundergroundstatus.shared


import java.text.SimpleDateFormat
import java.util.*

class CalendarUtils(private val timeHelper: TimeHelper) {

    fun getWeekendDates(): Pair<String, String> {
        val yearMonthDayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.UK)

        val uTCDateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.UK)
        val rollingDate = calculateThisSaturday()

        val saturdayDateString = yearMonthDayFormat.format(rollingDate.time)

        // treat the end of the weekend as just before midnight on Sunday...
        rollingDate.add(Calendar.DATE, 1)
        rollingDate.set(Calendar.HOUR_OF_DAY, 23)
        rollingDate.set(Calendar.MINUTE, 59)

        val earlyMondayMorningDateTimeString = uTCDateTimeFormat.format(rollingDate.time)
        return Pair(saturdayDateString, earlyMondayMorningDateTimeString)
    }

    fun getFormattedSaturdayDate(): String {
        // e.g. 2 May or 29 Apr . . .
        val dateMonthFormat = SimpleDateFormat("d MMM", Locale.getDefault())
        return dateMonthFormat.format(calculateThisSaturday().time)
    }

    fun getFormattedNowDate(): String {
        val date = timeHelper.getCurrentDateTime().time
        val formatter = SimpleDateFormat("EEE, MMM d HH:mm:ss", Locale.UK)
        return formatter.format(date)
    }

    private fun calculateThisSaturday(): Calendar {
        val dateExamined = timeHelper.getCurrentDateTime(Locale.UK) // start with today

        // move the date along until you reach Saturday . . .
        while (dateExamined[Calendar.DAY_OF_WEEK] != Calendar.SATURDAY) {
            dateExamined.add(Calendar.DATE, 1)
        }
        return dateExamined
    }
}