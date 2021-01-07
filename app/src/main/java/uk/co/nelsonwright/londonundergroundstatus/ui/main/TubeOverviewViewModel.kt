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

    fun loadTubeLines(isNowSelected: Boolean = true) {
        getTubeLines(isNowSelected)
    }

    private fun getTubeLines(isNowSelected: Boolean = true) {
        viewModelScope.launch(mainDispatcher) {
            mutableLiveData.value = TubeStatusViewState(loading = true)

            try {
                val tubeLineList = withContext(ioDispatcher) {
                    repo.loadTubeLines(isNowSelected)
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

