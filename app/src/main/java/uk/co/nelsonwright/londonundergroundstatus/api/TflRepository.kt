package uk.co.nelsonwright.londonundergroundstatus.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import uk.co.nelsonwright.londonundergroundstatus.ui.main.CalendarUtils.Companion.getWeekendDates

object TflRepository {
    private var tubeLines: MutableLiveData<List<TubeLine>> = MutableLiveData(emptyList())
    private var loadingError: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getTubeLines(): LiveData<List<TubeLine>> {
        return tubeLines
    }

    fun getLoadingError(): LiveData<Boolean> {
        return loadingError
    }

    private val api by lazy {
        TflService.create()
    }

    fun loadTubeLinesForNow(appId: String, appKey: String): Disposable? {
        return api.getLinesStatusNow(appId, appKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                success(),
                error()
            )
    }

    fun loadTubeLinesForWeekend(appId: String, appKey: String): Disposable? {
        val (saturdayDateString, sundayDateString) = getWeekendDates()

        return api.getLinesStatusForWeekend(appId, appKey, saturdayDateString, sundayDateString)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                success(),
                error()
            )
    }

    private fun success(): (tubeLinesList: List<TubeLine>) -> Unit {
        return { lines ->
            tubeLines.value = lines
            loadingError.value = false
        }
    }

    private fun error(): (t: Throwable) -> Unit {
        return { _ ->
            loadingError.value = true
            tubeLines.value = emptyList()
        }
    }
}
