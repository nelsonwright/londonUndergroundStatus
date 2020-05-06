package uk.co.nelsonwright.londonundergroundstatus.di

import dagger.Module
import dagger.Provides
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import uk.co.nelsonwright.londonundergroundstatus.shared.TimeHelper
import javax.inject.Singleton

@Module
class CalendarUtilsModule {
    private val timeHelper = TimeHelper()

    @Provides
    @Singleton
    fun provideCalendarUtils(): CalendarUtils = CalendarUtils(timeHelper)
}