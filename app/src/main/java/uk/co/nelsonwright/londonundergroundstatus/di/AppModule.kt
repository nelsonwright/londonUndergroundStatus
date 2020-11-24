package uk.co.nelsonwright.londonundergroundstatus.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import uk.co.nelsonwright.londonundergroundstatus.OpenForTesting
import javax.inject.Singleton

@Module
@OpenForTesting
class AppModule(private val app: Application) {
    @Provides
    @Singleton
    fun provideContext(): Context = app
}