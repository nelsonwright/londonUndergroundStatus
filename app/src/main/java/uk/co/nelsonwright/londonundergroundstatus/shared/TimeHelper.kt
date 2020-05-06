package uk.co.nelsonwright.londonundergroundstatus.shared

import java.util.*
import javax.inject.Singleton

@Singleton
class TimeHelper {

    fun getCurrentDateTime(locale: Locale = Locale.UK): Calendar {
        return Calendar.getInstance(locale)
    }
}