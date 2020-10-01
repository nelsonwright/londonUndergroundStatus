package uk.co.nelsonwright.londonundergroundstatus.di

import android.content.Context
import dagger.Module
import dagger.Provides
import uk.co.nelsonwright.londonundergroundstatus.api.TflRepository
import uk.co.nelsonwright.londonundergroundstatus.api.TflService
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import uk.co.nelsonwright.londonundergroundstatus.shared.TimeHelper
import javax.inject.Singleton

@Module
class TflRepositoryModule {
    private val api by lazy {
        TflService.create()
    }

    private val calendarUtils = CalendarUtils(TimeHelper())

    @Provides
    @Singleton
    fun provideTflRepository(context: Context): TflRepository = TflRepository(api, calendarUtils, context)
}