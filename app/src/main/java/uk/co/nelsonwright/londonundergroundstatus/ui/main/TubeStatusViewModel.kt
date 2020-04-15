package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.api.TflApiService
import uk.co.nelsonwright.londonundergroundstatus.models.TubeStatusViewState
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.DAY_OF_WEEK
import java.util.Calendar.SATURDAY

class TubeStatusViewModel(application: Application) : AndroidViewModel(application) {

    private val repo by lazy {
        TflApiService.create()
    }

    private var disposable: Disposable? = null
    private var viewState: MutableLiveData<TubeStatusViewState> = MutableLiveData()

    init {
        viewState.value = TubeStatusViewState(loading = true)
        loadTubeLines()
    }

    fun getViewState(): LiveData<TubeStatusViewState> {
        return viewState
    }

    fun onPause() {
        disposable?.dispose()
    }

    fun onRefreshClicked() {
        viewState.value = TubeStatusViewState(loading = true)

        loadTubeLines()
    }

    fun loadTubeLines() {
        val appId =
            getApplication<Application>().applicationContext.getString(R.string.applicationId)
        val appKey =
            getApplication<Application>().applicationContext.getString(R.string.applicationKey)

        disposable = repo.getLinesStatus(appId, appKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { lines ->
                    viewState.value = TubeStatusViewState(
                        tubeLines = lines,
                        refreshDate = getRefreshDate()
                    )
                },
                { error ->
                    viewState.value = TubeStatusViewState(loadingError = true)
                })
    }

    fun loadTubeLinesForWeekend() {
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

        disposable = repo.getLinesStatusForWeekend(appId, appKey, startDateString, endDateString)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { lines ->
                    viewState.value = TubeStatusViewState(
                        tubeLines = lines,
                        refreshDate = getRefreshDate()
                    )
                },
                { error ->
                    viewState.value = TubeStatusViewState(loadingError = true)
                })
    }


    private fun getRefreshDate(): String {
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateTimeInstance()
        return formatter.format(date)
    }
}

