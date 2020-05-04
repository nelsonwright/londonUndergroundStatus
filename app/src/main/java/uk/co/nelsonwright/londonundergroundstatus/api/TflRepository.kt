package uk.co.nelsonwright.londonundergroundstatus.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import uk.co.nelsonwright.londonundergroundstatus.ui.main.CalendarUtils.Companion.getWeekendDates

object TflRepository {
    private var tubeLinesStatusResult: MutableLiveData<TubeLinesStatusResult> = MutableLiveData(TubeLinesStatusResult())

    fun getTubeLines(): LiveData<TubeLinesStatusResult> {
        return tubeLinesStatusResult
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
            tubeLinesStatusResult.value = TubeLinesStatusResult(tubeLines = lines)
        }
    }

    private fun error(): (t: Throwable) -> Unit {
        return { _ ->
            tubeLinesStatusResult.value = TubeLinesStatusResult(loadingError = true)
        }
    }
}
