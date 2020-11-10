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
class TubeStatusViewModel(
    serviceLocator: ServiceLocator,
    private val mainDispatcher: CoroutineDispatcher,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    val viewState: LiveData<TubeStatusViewState>
        get() = mutableLiveData

    private val repo = serviceLocator.getTflRepository()
    private val calendarUtils = CalendarUtils(TimeHelper())
    private var mutableLiveData = MutableLiveData<TubeStatusViewState>()

    init {
        getTubeLines(isWeekend = false)
    }

    fun loadTubeLines(isWeekend: Boolean = false) {
        getTubeLines(isWeekend)
    }

    private fun getTubeLines(isWeekend: Boolean = false) {
        viewModelScope.launch(mainDispatcher) {
            mutableLiveData.value = TubeStatusViewState(loading = true)

            try {
                val tubeLineList = if (isWeekend) {
                    withContext(ioDispatcher) {
                        repo.loadTubeLinesForWeekend()
                    }
                } else {
                    withContext(ioDispatcher) {
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

