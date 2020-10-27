package uk.co.nelsonwright.londonundergroundstatus.api

import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import uk.co.nelsonwright.londonundergroundstatus.shared.TimeHelper

interface ServiceLocator {
    fun getTflRepository(): TflRepository
}

class ServiceLocatorImpl : ServiceLocator {
    private val api by lazy {
        TflService.create()
    }

    private val calendarUtils = CalendarUtils(TimeHelper())

    override fun getTflRepository(): TflRepository {
        return TflRepositoryImpl(api, calendarUtils)
    }
}