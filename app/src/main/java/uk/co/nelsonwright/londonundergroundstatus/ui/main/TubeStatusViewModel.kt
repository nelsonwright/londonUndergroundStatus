package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.Disposable
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.api.TflRepository
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.DAY_OF_WEEK
import java.util.Calendar.SATURDAY

class TubeStatusViewModel(application: Application) : AndroidViewModel(application) {

    private var disposable: Disposable? = null

    //    private var viewState: MutableLiveData<TubeStatusViewState> = MutableLiveData()
    private val repo: TflRepository = TflRepository()

    val tubeLines = repo.getTubeLines()
    val loadingError = repo.getLoadingError()
    var loading: MutableLiveData<Boolean> = MutableLiveData()

    init {
        loadTubeLines()
    }

    fun getLoading(): LiveData<Boolean> {
        return loading
    }

    fun onPause() {
        disposable?.dispose()
    }

    fun onRefreshClicked() {
        loadTubeLines()
    }

    fun loadTubeLines() {
        showLoading()
        val appId =
            getApplication<Application>().applicationContext.getString(R.string.applicationId)
        val appKey =
            getApplication<Application>().applicationContext.getString(R.string.applicationKey)

        disposable = repo.loadTubeLinesNow(appId, appKey)
    }

    fun loadTubeLinesForWeekend() {
        showLoading()

        val yearMonthDayFormat = SimpleDateFormat("yyyy-MM-dd")
        val appId =
            getApplication<Application>().applicationContext.getString(R.string.applicationId)
        val appKey =
            getApplication<Application>().applicationContext.getString(R.string.applicationKey)

        val dateExamined = Calendar.getInstance(Locale.UK) // start with today

        while (dateExamined[DAY_OF_WEEK] != SATURDAY) {
            dateExamined.add(Calendar.DATE, 1)
        }

        val startDateString = yearMonthDayFormat.format(dateExamined.time)
        dateExamined.add(Calendar.DATE, 1)
        val endDateString = yearMonthDayFormat.format(dateExamined.time)

//        disposable = repo.getLinesStatusForWeekend(appId, appKey, startDateString, endDateString)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { lines ->
//                    viewState.value = TubeStatusViewState(
//                        tubeLines = lines,
//                        refreshDate = getRefreshDate()
//                    )
//                },
//                { error ->
//                    viewState.value = TubeStatusViewState(loadingError = true)
//                })
    }

    private fun showLoading() {
        loading.value = true
    }
}

