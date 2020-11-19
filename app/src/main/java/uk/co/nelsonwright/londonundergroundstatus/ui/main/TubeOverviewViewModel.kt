package uk.co.nelsonwright.londonundergroundstatus.ui.main

import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.co.nelsonwright.londonundergroundstatus.api.ServiceLocator
import uk.co.nelsonwright.londonundergroundstatus.models.TubeStatusViewState
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import uk.co.nelsonwright.londonundergroundstatus.shared.TimeHelper


@Keep
class TubeOverviewViewModel(
    serviceLocator: ServiceLocator,
    private val mainDispatcher: CoroutineDispatcher,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    var spinnerPosition: Int = 0
    val viewState: LiveData<TubeStatusViewState>
        get() = mutableLiveData

    private val repo = serviceLocator.getTflRepository()
    private val calendarUtils = CalendarUtils(TimeHelper())
    private var mutableLiveData = MutableLiveData<TubeStatusViewState>()

    init {
        getTubeLines(isWeekendSelected = false)
    }

    fun loadTubeLines(isWeekend: Boolean = false) {
        getTubeLines(isWeekend)
    }

    private fun getTubeLines(isWeekendSelected: Boolean = false) {
        viewModelScope.launch(mainDispatcher) {
            mutableLiveData.value = TubeStatusViewState(loading = true)

            try {
                val tubeLineList = withContext(ioDispatcher) {
                    repo.loadTubeLines(isWeekendSelected)
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

