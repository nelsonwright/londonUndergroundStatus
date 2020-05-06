package uk.co.nelsonwright.londonundergroundstatus.di

import dagger.Module
import dagger.Provides
import uk.co.nelsonwright.londonundergroundstatus.shared.TimeHelper
import javax.inject.Singleton

@Module
class TimeHelperModule {

    @Provides
    @Singleton
    fun provideTimeHelper(): TimeHelper = TimeHelper()
}