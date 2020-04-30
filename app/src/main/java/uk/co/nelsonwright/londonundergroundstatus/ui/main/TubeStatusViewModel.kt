package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.api.TflRepository
import uk.co.nelsonwright.londonundergroundstatus.models.TubeStatusViewState
import uk.co.nelsonwright.londonundergroundstatus.ui.main.CalendarUtils.Companion.getFormattedNowDate

class TubeStatusViewModel(
    private val context: Context,
    private val repo: TflRepository
) : ViewModel() {

    val viewState: LiveData<TubeStatusViewState>
        get() {
            return Transformations.map(tubeLinesResult) {
                TubeStatusViewState(
                    tubeLines = it.tubeLines,
                    loadingError = it.loadingError,
                    refreshDate = getFormattedNowDate(),
                    loading = false
                )
            }
        }

    private var tubeLinesResult = repo.getTubeLines()
    private var loading = MutableLiveData<Boolean>(false)
    private var disposable: Disposable? = null

    init {
        loadTubeLines()
    }

    fun getLoading(): LiveData<Boolean> {
        return loading
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

    private fun getAppKeyAndId(): Pair<String, String> {
        val appId = context.applicationContext.getString(R.string.applicationId)
        val appKey = context.applicationContext.getString(R.string.applicationKey)
        return Pair(appId, appKey)
    }

    private fun showLoading() {
        loading.value = true
    }
}

