package uk.co.nelsonwright.londonundergroundstatus.di

import dagger.Binds
import dagger.Module
import uk.co.nelsonwright.londonundergroundstatus.api.TflRepository
import uk.co.nelsonwright.londonundergroundstatus.api.TflRepositoryImpl
import javax.inject.Singleton

@Module
abstract class TflRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindsTflRepository(tflRepository: TflRepositoryImpl): TflRepository
}