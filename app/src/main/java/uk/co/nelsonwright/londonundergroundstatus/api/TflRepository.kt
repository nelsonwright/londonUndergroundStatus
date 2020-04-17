package uk.co.nelsonwright.londonundergroundstatus.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.itkacher.okhttpprofiler.OkHttpProfilerInterceptor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import uk.co.nelsonwright.londonundergroundstatus.BuildConfig
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

    private fun getWeekendDates(): Pair<String, String> {
        val yearMonthDayFormat = SimpleDateFormat("yyyy-MM-dd")
        val thisSaturday = calculateThisSaturday()

        val saturdayDateString = yearMonthDayFormat.format(thisSaturday.time)
        thisSaturday.add(Calendar.DATE, 1)
        val sundayDateString = yearMonthDayFormat.format(thisSaturday.time)
        return Pair(saturdayDateString, sundayDateString)
    }

    private fun calculateThisSaturday(): Calendar {
        val dateExamined = Calendar.getInstance(Locale.UK) // start with today

        // move the date along until you reach Saturday . . .
        while (dateExamined[Calendar.DAY_OF_WEEK] != Calendar.SATURDAY) {
            dateExamined.add(Calendar.DATE, 1)
        }
        return dateExamined
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

    companion object {
        fun create(): TflApiService {
            val builder = OkHttpClient.Builder()

            if (BuildConfig.DEBUG) {
                builder.addInterceptor(OkHttpProfilerInterceptor())
            }
            val client = builder.build()

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.tfl.gov.uk/")
                .client(client)
                .build()

            return retrofit.create(TflApiService::class.java)
        }
    }
}