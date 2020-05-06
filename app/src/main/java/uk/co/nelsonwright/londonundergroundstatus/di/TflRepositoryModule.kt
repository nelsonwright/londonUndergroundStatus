package uk.co.nelsonwright.londonundergroundstatus.di

import dagger.Module
import dagger.Provides
import uk.co.nelsonwright.londonundergroundstatus.api.TflRepository
import uk.co.nelsonwright.londonundergroundstatus.api.TflService
import javax.inject.Singleton

@Module
class TflRepositoryModule {
    private val api by lazy {
        TflService.create()
    }

    @Provides
    @Singleton
    fun provideTflRepository(): TflRepository = TflRepository(api)
}