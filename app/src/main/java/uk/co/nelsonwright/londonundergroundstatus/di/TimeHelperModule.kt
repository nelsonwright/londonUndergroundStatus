package uk.co.nelsonwright.londonundergroundstatus.di

import dagger.Binds
import dagger.Module
import uk.co.nelsonwright.londonundergroundstatus.shared.TimeHelper
import uk.co.nelsonwright.londonundergroundstatus.shared.TimeHelperImpl
import javax.inject.Singleton

@Module
abstract class TimeHelperModule {

    @Singleton
    @Binds
    abstract fun bindTimeHelper(timeHelper: TimeHelperImpl): TimeHelper
}