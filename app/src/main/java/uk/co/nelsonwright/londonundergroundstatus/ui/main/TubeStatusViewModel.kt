package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.Disposable
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.api.TflRepository

class TubeStatusViewModel(
    application: Application,
    private val repo: TflRepository
) : AndroidViewModel(application) {

    val tubeLines = repo.getTubeLines()
    val loadingError = repo.getLoadingError()

    private var loading: MutableLiveData<Boolean> = MutableLiveData()
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
        val appId =
            getApplication<Application>().applicationContext.getString(R.string.applicationId)
        val appKey =
            getApplication<Application>().applicationContext.getString(R.string.applicationKey)
        return Pair(appId, appKey)
    }

    private fun showLoading() {
        loading.value = true
    }
}

