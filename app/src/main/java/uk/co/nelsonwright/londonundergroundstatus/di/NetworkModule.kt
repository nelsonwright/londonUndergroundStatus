package uk.co.nelsonwright.londonundergroundstatus.di

import dagger.Module
import dagger.Provides
import uk.co.nelsonwright.londonundergroundstatus.api.TflApiInterface
import uk.co.nelsonwright.londonundergroundstatus.api.TflService

@Module
class NetworkModule {

    @Provides
    fun provideTflApiInterface(): TflApiInterface {
        return TflService.create()
    }
}