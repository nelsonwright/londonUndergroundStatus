package uk.co.nelsonwright.londonundergroundstatus.api

import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import uk.co.nelsonwright.londonundergroundstatus.shared.TimeHelperImpl

interface ServiceLocator {
    fun getTflRepository(): TflRepository
    fun getCalendarUtils(): CalendarUtils
}

class ServiceLocatorImpl : ServiceLocator {
    private val api by lazy {
        TflService.create()
    }

    private val calUtils by lazy {
        CalendarUtils(TimeHelperImpl())
    }

    override fun getTflRepository(): TflRepository {
        return TflRepositoryImpl(api, calUtils)
    }

    override fun getCalendarUtils(): CalendarUtils {
        return calUtils
    }
}