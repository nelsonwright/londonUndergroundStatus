package uk.co.nelsonwright.londonundergroundstatus.di

import dagger.Component
import uk.co.nelsonwright.londonundergroundstatus.ui.main.TubeStatusOverviewActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, TflRepositoryModule::class, TimeHelperModule::class, CalendarUtilsModule::class])
interface AppComponent {
    fun inject(target: TubeStatusOverviewActivity)
}