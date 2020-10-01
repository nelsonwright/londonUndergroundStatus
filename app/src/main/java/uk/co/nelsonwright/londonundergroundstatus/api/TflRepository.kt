package uk.co.nelsonwright.londonundergroundstatus.api

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import javax.inject.Singleton

@Singleton
class TflRepository(val api: TflApiInterface, private val calendarUtils: CalendarUtils, private val context: Context) {
    private var tubeLinesStatusResult: MutableLiveData<TubeLinesStatusResult> = MutableLiveData(TubeLinesStatusResult())

    fun getTubeLines(): LiveData<TubeLinesStatusResult> {
        return tubeLinesStatusResult
    }

    fun loadTubeLinesForNow(): Disposable? {
        val (appId, appKey) = getAppKeyAndId()

        return api.getLinesStatusNow(appId, appKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                success(),
                error()
            )
    }

    fun loadTubeLinesForWeekend(): Disposable? {
        val (appId, appKey) = getAppKeyAndId()

        val (saturdayDateString, sundayDateString) = calendarUtils.getWeekendDates()

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
            tubeLinesStatusResult.value = TubeLinesStatusResult(
                tubeLines = lines,
                timestamp = calendarUtils.getFormattedNowDate()
            )
        }
    }

    private fun error(): (t: Throwable) -> Unit {
        return { _ ->
            tubeLinesStatusResult.value = TubeLinesStatusResult(
                loadingError = true,
                timestamp = calendarUtils.getFormattedNowDate()
            )
        }
    }

    private fun getAppKeyAndId(): Pair<String, String> {
        val appId = context.getString(R.string.applicationId)
        val appKey = context.getString(R.string.applicationKey)
        return Pair(appId, appKey)
    }
}
