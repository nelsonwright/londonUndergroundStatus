package uk.co.nelsonwright.londonundergroundstatus.ui.main

import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.co.nelsonwright.londonundergroundstatus.api.TflRepository
import uk.co.nelsonwright.londonundergroundstatus.models.TubeStatusViewState
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import uk.co.nelsonwright.londonundergroundstatus.ui.main.SelectionType.NOW


@Keep
class TubeOverviewViewModel(
    private val repo: TflRepository,
    private val calendarUtils: CalendarUtils,
    private val mainDispatcher: CoroutineDispatcher,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    var spinnerPosition: Int = 0
    val viewState: LiveData<TubeStatusViewState>
        get() = mutableLiveData

    private var mutableLiveData = MutableLiveData<TubeStatusViewState>()

    init {
        getTubeLines()
    }

    fun loadTubeLines(selectionType: SelectionType) {
        getTubeLines(selectionType = selectionType)
    }

    fun refreshTubeLines(selectionType: SelectionType) {
        getTubeLines(selectionType = selectionType, useCacheRequest = false)
    }

    private fun getTubeLines(selectionType: SelectionType = NOW, useCacheRequest: Boolean = true) {
        viewModelScope.launch(mainDispatcher) {
            mutableLiveData.value = TubeStatusViewState(loading = true)

            try {
                val tubeLineList = withContext(ioDispatcher) {
                    repo.loadTubeLines(selectionType = selectionType, useCacheRequest = useCacheRequest)
                }
                mutableLiveData.value = TubeStatusViewState(
                    tubeLines = tubeLineList.tubeLines,
                    refreshDate = calendarUtils.getFormattedLocateDateTime(tubeLineList.refreshTime)
                )
            } catch (exception: Exception) {
                mutableLiveData.value = TubeStatusViewState(loadingError = true)
            }
        }
    }
}

