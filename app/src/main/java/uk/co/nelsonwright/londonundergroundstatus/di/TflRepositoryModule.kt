package uk.co.nelsonwright.londonundergroundstatus.di

import dagger.Module
import dagger.Provides
import uk.co.nelsonwright.londonundergroundstatus.api.TflRepository
import javax.inject.Singleton

@Module
class TflRepositoryModule {
    @Provides
    @Singleton
    fun provideTflRepository(): TflRepository = TflRepository
}