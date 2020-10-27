package uk.co.nelsonwright.londonundergroundstatus.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import uk.co.nelsonwright.londonundergroundstatus.api.ServiceLocator
import uk.co.nelsonwright.londonundergroundstatus.api.ServiceLocatorImpl
import javax.inject.Singleton

@Module
open class AppModule(private val app: Application) {
    @Provides
    @Singleton
    open fun provideContext(): Context = app

    @Provides
    @Singleton
    open fun provideServiceLocator(): ServiceLocator = ServiceLocatorImpl()
}