package uk.co.nelsonwright.londonundergroundstatus.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import uk.co.nelsonwright.londonundergroundstatus.api.TflRepository
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import javax.inject.Inject

class TubeOverviewViewModelFactory @Inject constructor(
    private val repository: TflRepository,
    private val calendarUtils: CalendarUtils
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(TubeOverviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            TubeOverviewViewModel(
                repo = repository,
                calendarUtils = calendarUtils,
                mainDispatcher = Dispatchers.Main,
                ioDispatcher = Dispatchers.IO
            ) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}