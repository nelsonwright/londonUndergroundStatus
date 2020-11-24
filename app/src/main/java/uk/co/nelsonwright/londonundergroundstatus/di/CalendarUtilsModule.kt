package uk.co.nelsonwright.londonundergroundstatus.di

import dagger.Binds
import dagger.Module
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtilsImpl
import javax.inject.Singleton

@Module
abstract class CalendarUtilsModule {

    @Singleton
    @Binds
    abstract fun bindCalendarUtils(calendarUtils: CalendarUtilsImpl): CalendarUtils
}