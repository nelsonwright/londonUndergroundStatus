package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.api.TflRepository
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLinesStatusResult
import uk.co.nelsonwright.londonundergroundstatus.models.TubeStatusViewState

class TubeStatusViewModel(
    private val context: Context,
    private val repo: TflRepository
) : ViewModel() {

    private var tubeLinesResult = repo.getTubeLines()

    val viewState: LiveData<TubeStatusViewState>
        get() = mediatorLiveData

    var mediatorLiveData = MediatorLiveData<TubeStatusViewState>()

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
        val (appId, appKey) = getAppKeyAndId()

        disposable = if (weekend) {
            repo.loadTubeLinesForWeekend(appId, appKey)
        } else {
            repo.loadTubeLinesForNow(appId, appKey)
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

    private fun getAppKeyAndId(): Pair<String, String> {
        val appId = context.getString(R.string.applicationId)
        val appKey = context.getString(R.string.applicationKey)
        return Pair(appId, appKey)
    }

    private fun showLoading() {
        loading.value = true
    }

    private fun hideLoading() {
        loading.value = false
    }
}

