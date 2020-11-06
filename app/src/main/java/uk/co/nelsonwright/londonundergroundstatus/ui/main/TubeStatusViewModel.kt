package uk.co.nelsonwright.londonundergroundstatus.ui.main

import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.co.nelsonwright.londonundergroundstatus.api.ServiceLocator
import uk.co.nelsonwright.londonundergroundstatus.models.TubeStatusViewState
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import uk.co.nelsonwright.londonundergroundstatus.shared.TimeHelper


@Keep
class TubeStatusViewModel(serviceLocator: ServiceLocator) : ViewModel() {
    val viewState: LiveData<TubeStatusViewState>
        get() = mutableLiveData

    private val repo = serviceLocator.getTflRepository()
    private val calendarUtils = CalendarUtils(TimeHelper())
    private var mutableLiveData = MutableLiveData<TubeStatusViewState>()

    fun onRefreshClicked(isWeekendSelected: Boolean) {
        getTubeLines(isWeekendSelected)
    }

    fun loadTubeLines(isWeekend: Boolean = false) {
        getTubeLines(isWeekend)
    }

    private fun getTubeLines(isWeekend: Boolean = false) {
        viewModelScope.launch {
            mutableLiveData.value = TubeStatusViewState(loading = true)

            try {
                val tubeLineList = if (isWeekend) {
                    withContext(Dispatchers.IO) {
                        repo.loadTubeLinesForWeekend()
                    }
                } else {
                    withContext(Dispatchers.IO) {
                        repo.loadTubeLinesForNow()
                    }
                }

                mutableLiveData.value = TubeStatusViewState(
                    tubeLines = tubeLineList,
                    refreshDate = calendarUtils.getFormattedLocateDateTime()
                )
            } catch (exception: Exception) {
                mutableLiveData.value = TubeStatusViewState(loadingError = true)
            }
        }
    }
}

