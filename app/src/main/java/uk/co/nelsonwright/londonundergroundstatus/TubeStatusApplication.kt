package uk.co.nelsonwright.londonundergroundstatus

import android.app.Application
import uk.co.nelsonwright.londonundergroundstatus.di.AppComponent
import uk.co.nelsonwright.londonundergroundstatus.di.AppModule
import uk.co.nelsonwright.londonundergroundstatus.di.DaggerAppComponent

class TubeStatusApplication : Application() {
    lateinit var tubeStatusComponent: AppComponent

    private fun initDagger(app: TubeStatusApplication): AppComponent =
        DaggerAppComponent.builder()
            .appModule(AppModule(app))
            .build()

    override fun onCreate() {
        super.onCreate()
        tubeStatusComponent = initDagger(this)
    }

    fun setAppComponent(appComponent: AppComponent) {
        tubeStatusComponent = appComponent
    }
}