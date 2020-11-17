package uk.co.nelsonwright.londonundergroundstatus.di

import dagger.Component
import uk.co.nelsonwright.londonundergroundstatus.ui.main.TubeOverviewFragment
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class, TimeHelperModule::class, CalendarUtilsModule::class, TflRepositoryModule::class]
)

interface AppComponent {
    fun inject(target: TubeOverviewFragment)
}