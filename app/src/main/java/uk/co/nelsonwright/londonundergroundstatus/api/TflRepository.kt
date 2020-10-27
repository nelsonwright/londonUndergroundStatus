package uk.co.nelsonwright.londonundergroundstatus.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import javax.inject.Singleton

interface TflRepository {
    fun getTubeLines(): LiveData<TubeLinesStatusResult>
    fun loadTubeLinesForNow(): Disposable?
    fun loadTubeLinesForWeekend(): Disposable?
}

@Singleton
class TflRepositoryImpl(val api: TflApiInterface, private val calendarUtils: CalendarUtils) : TflRepository {
    private var tubeLinesStatusResult: MutableLiveData<TubeLinesStatusResult> = MutableLiveData(TubeLinesStatusResult())

    override fun getTubeLines(): LiveData<TubeLinesStatusResult> {
        return tubeLinesStatusResult
    }

    override fun loadTubeLinesForNow(): Disposable? {
        return api.getLinesStatusNow(APPLICATION_ID, APPLICATION_KEY)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                success(),
                error()
            )
    }

    override fun loadTubeLinesForWeekend(): Disposable? {
        val (saturdayDateString, sundayDateString) = calendarUtils.getWeekendDates()

        return api.getLinesStatusForWeekend(APPLICATION_ID, APPLICATION_KEY, saturdayDateString, sundayDateString)
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
                timestamp = calendarUtils.getFormattedLocateDateTime()
            )
        }
    }

    private fun error(): (t: Throwable) -> Unit {
        return { _ ->
            tubeLinesStatusResult.value = TubeLinesStatusResult(
                loadingError = true,
                timestamp = calendarUtils.getFormattedLocateDateTime()
            )
        }
    }
}
