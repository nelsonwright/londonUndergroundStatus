package uk.co.nelsonwright.londonundergroundstatus.shared

import java.text.SimpleDateFormat
import java.util.*

class CalendarUtils(private val timeHelper: TimeHelper) {

    fun getWeekendDates(): Pair<String, String> {
        val yearMonthDayFormat = SimpleDateFormat("yyyy-MM-dd")
        val thisSaturday = calculateThisSaturday()

        val saturdayDateString = yearMonthDayFormat.format(thisSaturday.time)
        thisSaturday.add(Calendar.DATE, 1)
        val sundayDateString = yearMonthDayFormat.format(thisSaturday.time)
        return Pair(saturdayDateString, sundayDateString)
    }

    fun getFormattedSaturdayDate(): String {
        // e.g. 2 May or 29 Apr . . .
        val dateMonthFormat = SimpleDateFormat("d MMM")
        return dateMonthFormat.format(calculateThisSaturday().time)
    }

    fun getFormattedNowDate(): String {
        val date = timeHelper.getCurrentDateTime().time
        val formatter = SimpleDateFormat("EEE, MMM d HH:mm:ss")
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