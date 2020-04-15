package uk.co.nelsonwright.londonundergroundstatus.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class TflRepository {
    private var tubeLines: MutableLiveData<List<TubeLine>> = MutableLiveData()
    private var loadingError: MutableLiveData<Boolean> = MutableLiveData()

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

    fun loadTubeLinesNow(appId: String, appKey: String): Disposable? {
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
}