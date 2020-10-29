package uk.co.nelsonwright.londonundergroundstatus.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uk.co.nelsonwright.londonundergroundstatus.api.ServiceLocator

class TubeStatusViewModelFactory(private val serviceLocator: ServiceLocator) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ServiceLocator::class.java)
            .newInstance(serviceLocator)
    }
}