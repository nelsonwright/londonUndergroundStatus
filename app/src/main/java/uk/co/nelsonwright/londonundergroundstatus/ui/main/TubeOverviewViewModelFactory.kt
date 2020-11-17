package uk.co.nelsonwright.londonundergroundstatus.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import uk.co.nelsonwright.londonundergroundstatus.api.ServiceLocator
import javax.inject.Inject

class TubeOverviewViewModelFactory @Inject constructor(
    private val serviceLocator: ServiceLocator
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(TubeOverviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            TubeOverviewViewModel(
                serviceLocator = serviceLocator,
                mainDispatcher = Dispatchers.Main,
                ioDispatcher = Dispatchers.IO
            ) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}