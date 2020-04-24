package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uk.co.nelsonwright.londonundergroundstatus.api.TflRepository

class TubeStatusViewModelFactory(val app: Application, val repo: TflRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Application::class.java, TflRepository::class.java)
            .newInstance(app, repo)
    }
}