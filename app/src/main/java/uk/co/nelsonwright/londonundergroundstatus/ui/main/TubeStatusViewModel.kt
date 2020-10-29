package uk.co.nelsonwright.londonundergroundstatus.ui.main

import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import uk.co.nelsonwright.londonundergroundstatus.api.ServiceLocator
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLinesStatusResult
import uk.co.nelsonwright.londonundergroundstatus.models.TubeStatusViewState


@Keep
class TubeStatusViewModel(serviceLocator: ServiceLocator) : ViewModel() {
    private val repo = serviceLocator.getTflRepository()

    val viewState: LiveData<TubeStatusViewState>
        get() = mediatorLiveData

    var mediatorLiveData = MediatorLiveData<TubeStatusViewState>()

    private var tubeLinesResult = repo.getTubeLines()
    private var loading = MutableLiveData(false)
    private var disposable: Disposable? = null

    init {
        mediatorLiveData.addSource(tubeLinesResult) {
            mediatorLiveData.value = combineLatestData(tubeLinesResult, loading)
            hideLoading()
        }
        mediatorLiveData.addSource(loading) {
            mediatorLiveData.value = combineLatestData(tubeLinesResult, loading)
        }

        loadTubeLines()
    }

    fun onPause() {
        disposable?.dispose()
    }

    fun onRefreshClicked(isWeekendSelected: Boolean) {
        loadTubeLines(isWeekendSelected)
    }

    fun loadTubeLines(weekend: Boolean = false) {
        showLoading()

        disposable = if (weekend) {
            repo.loadTubeLinesForWeekend()
        } else {
            repo.loadTubeLinesForNow()
        }
    }

    private fun combineLatestData(
        tubeLinesResult: LiveData<TubeLinesStatusResult>,
        loadingResult: MutableLiveData<Boolean>
    ): TubeStatusViewState? {
        val tubeLinesStatusResult = tubeLinesResult.value
        val loading = loadingResult.value

        if (tubeLinesStatusResult == null || loading == null) {
            return TubeStatusViewState(loading = true)
        }

        return TubeStatusViewState(
            loadingError = tubeLinesStatusResult.loadingError,
            tubeLines = tubeLinesStatusResult.tubeLines,
            refreshDate = tubeLinesStatusResult.timestamp,
            loading = loading
        )
    }

    private fun showLoading() {
        loading.value = true
    }

    private fun hideLoading() {
        loading.value = false
    }
}

