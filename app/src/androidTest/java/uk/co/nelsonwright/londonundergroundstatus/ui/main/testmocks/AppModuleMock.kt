package uk.co.nelsonwright.londonundergroundstatus.ui.main.testmocks

import android.app.Application
import dagger.Module
import dagger.Provides
import uk.co.nelsonwright.londonundergroundstatus.api.ServiceLocator
import uk.co.nelsonwright.londonundergroundstatus.di.AppModule
import javax.inject.Singleton

@Module
class AppModuleMock(app: Application) : AppModule(app) {

    @Provides
    @Singleton
    override fun provideServiceLocator(): ServiceLocator = ServiceLocatorMock()
}