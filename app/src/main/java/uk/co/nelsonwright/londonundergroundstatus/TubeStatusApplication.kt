package uk.co.nelsonwright.londonundergroundstatus

import android.app.Application
import uk.co.nelsonwright.londonundergroundstatus.di.AppComponent
import uk.co.nelsonwright.londonundergroundstatus.di.DaggerAppComponent

class TubeStatusApplication : Application() {
    // Reference to the application graph that is used across the whole app
    val appComponent: AppComponent = DaggerAppComponent.create()

}