package uk.co.nelsonwright.londonundergroundstatus.di

import dagger.Module
import dagger.Provides
import uk.co.nelsonwright.londonundergroundstatus.api.TflApiInterface
import uk.co.nelsonwright.londonundergroundstatus.api.TflService
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideTflApiInterface(): TflApiInterface {
        return TflService.create()
    }
}