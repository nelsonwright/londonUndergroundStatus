package uk.co.nelsonwright.londonundergroundstatus.api

import android.content.Context
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import uk.co.nelsonwright.londonundergroundstatus.shared.TimeHelper
import javax.inject.Inject

interface ServiceLocator {
    fun getTflRepository(): TflRepository
}

class ServiceLocatorImpl @Inject constructor(private val context: Context) : ServiceLocator {
    private val api by lazy {
        TflService.create()
    }

    private val calendarUtils = CalendarUtils(TimeHelper())

    override fun getTflRepository(): TflRepository {
        return TflRepository(api, calendarUtils, context)
    }
}