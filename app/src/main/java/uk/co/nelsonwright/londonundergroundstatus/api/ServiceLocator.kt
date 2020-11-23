package uk.co.nelsonwright.londonundergroundstatus.api

import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils

interface ServiceLocator {
    fun getTflRepository(): TflRepository
    fun getCalendarUtils(): CalendarUtils
}

class ServiceLocatorImpl(private val calendarUtils: CalendarUtils) : ServiceLocator {
    private val api by lazy {
        TflService.create()
    }

    override fun getTflRepository(): TflRepository {
        return TflRepositoryImpl(api, calendarUtils)
    }

    override fun getCalendarUtils(): CalendarUtils {
        return calendarUtils
    }
}