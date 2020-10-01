package uk.co.nelsonwright.londonundergroundstatus.di

import dagger.Component
import uk.co.nelsonwright.londonundergroundstatus.ui.main.TubeStatusOverviewActivity
import uk.co.nelsonwright.londonundergroundstatus.ui.main.TubeStatusViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, TimeHelperModule::class, CalendarUtilsModule::class, TflRepositoryModule::class])
interface AppComponent {
    fun inject(target: TubeStatusOverviewActivity)
    fun inject(target: TubeStatusViewModel)
}