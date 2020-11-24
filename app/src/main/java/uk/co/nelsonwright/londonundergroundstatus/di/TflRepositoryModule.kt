package uk.co.nelsonwright.londonundergroundstatus.di

import dagger.Module
import dagger.Provides
import uk.co.nelsonwright.londonundergroundstatus.api.TflRepository
import uk.co.nelsonwright.londonundergroundstatus.api.TflRepositoryImpl
import uk.co.nelsonwright.londonundergroundstatus.api.TflService
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtilsImpl
import uk.co.nelsonwright.londonundergroundstatus.shared.TimeHelperImpl
import javax.inject.Singleton

@Module
class TflRepositoryModule {

    @Singleton
    @Provides
    fun providesTflRepository(): TflRepository {
        return TflRepositoryImpl(TflService.create(), CalendarUtilsImpl(TimeHelperImpl()))
    }
}