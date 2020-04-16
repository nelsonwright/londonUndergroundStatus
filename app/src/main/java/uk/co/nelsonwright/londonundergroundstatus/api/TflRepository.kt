package uk.co.nelsonwright.londonundergroundstatus.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class TflRepository {
    private var tubeLines: MutableLiveData<List<TubeLine>> = MutableLiveData(emptyList())
    private var loadingError: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getTubeLines(): LiveData<List<TubeLine>> {
        return tubeLines
    }

    fun getLoadingError(): LiveData<Boolean> {
        return loadingError
    }

    private val api by lazy {
        create()
    }

    companion object {
        fun create(): TflApiService {

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.tfl.gov.uk/")
                .build()

            return retrofit.create(TflApiService::class.java)
        }
    }

    fun loadTubeLinesForNow(appId: String, appKey: String): Disposable? {
        return api.getLinesStatusNow(appId, appKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { lines ->
                    tubeLines.value = lines
                    loadingError.value = false
                },
                { error ->
                    loadingError.value = true
                    tubeLines.value = emptyList()
                })
    }

    fun loadTubeLinesForWeekend(appId: String, appKey: String): Disposable? {
        val yearMonthDayFormat = SimpleDateFormat("yyyy-MM-dd")
        val thisSaturday = calculateThisSaturday()

        val saturdayDateString = yearMonthDayFormat.format(thisSaturday.time)
        thisSaturday.add(Calendar.DATE, 1)
        val sundayDateString = yearMonthDayFormat.format(thisSaturday.time)

        return api.getLinesStatusForWeekend(appId, appKey, saturdayDateString, sundayDateString)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { lines ->
                    tubeLines.value = lines
                    loadingError.value = false
                },
                { error ->
                    loadingError.value = true
                    tubeLines.value = emptyList()
                })
    }

    private fun calculateThisSaturday(): Calendar {
        val dateExamined = Calendar.getInstance(Locale.UK) // start with today

        // move the date along until you reach Saturday . . .
        while (dateExamined[Calendar.DAY_OF_WEEK] != Calendar.SATURDAY) {
            dateExamined.add(Calendar.DATE, 1)
        }
        return dateExamined
    }
}