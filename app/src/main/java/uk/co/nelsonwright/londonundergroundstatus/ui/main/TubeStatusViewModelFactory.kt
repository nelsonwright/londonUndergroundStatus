package uk.co.nelsonwright.londonundergroundstatus.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import uk.co.nelsonwright.londonundergroundstatus.api.ServiceLocatorImpl

class TubeStatusViewModelFactory(
    private val mainDispatcher: CoroutineDispatcher,
    private val ioDispatcher: CoroutineDispatcher
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val serviceLocator = ServiceLocatorImpl()
        return if (modelClass.isAssignableFrom(TubeStatusViewModel::class.java)) {
            TubeStatusViewModel(serviceLocator, mainDispatcher, ioDispatcher) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}