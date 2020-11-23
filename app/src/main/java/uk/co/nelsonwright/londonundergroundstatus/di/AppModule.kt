package uk.co.nelsonwright.londonundergroundstatus.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import uk.co.nelsonwright.londonundergroundstatus.OpenForTesting
import uk.co.nelsonwright.londonundergroundstatus.api.ServiceLocator
import uk.co.nelsonwright.londonundergroundstatus.api.ServiceLocatorImpl
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtilsImpl
import uk.co.nelsonwright.londonundergroundstatus.shared.TimeHelperImpl
import javax.inject.Singleton

@Module
@OpenForTesting
class AppModule(private val app: Application) {
    @Provides
    @Singleton
    fun provideContext(): Context = app

    @Provides
    @Singleton
    fun provideServiceLocator(): ServiceLocator = ServiceLocatorImpl(CalendarUtilsImpl(TimeHelperImpl()))
}