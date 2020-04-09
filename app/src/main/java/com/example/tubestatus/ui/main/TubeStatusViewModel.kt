package com.example.tubestatus.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tubestatus.R
import com.example.tubestatus.api.TflApiService
import com.example.tubestatus.api.TubeStatus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class TubeStatusViewModel(application: Application) : AndroidViewModel(application) {

    private val repo by lazy {
        TflApiService.create()
    }

    private var loadError = MutableLiveData<Boolean>()
    private var loading = MutableLiveData<Boolean>()

    private var disposable: Disposable? = null
    private val tubeLines: MutableLiveData<List<TubeStatus>> = MutableLiveData()

    init {
        loadError.value = false
        loading.value = true
        loadTubeLines()
    }

    fun getTubeLines(): LiveData<List<TubeStatus>> {
        return tubeLines
    }

    fun getLoadError(): LiveData<Boolean> {
        return loadError
    }

    fun getLoading(): LiveData<Boolean> {
        return loading
    }

    fun onPause() {
        disposable?.dispose()
    }

    fun onRefreshClicked() {
        loadError.value = false
        loading.value = true
        loadTubeLines()
    }

    private fun loadTubeLines() {
        val appId =
            getApplication<Application>().applicationContext.getString(R.string.applicationId)
        val appKey =
            getApplication<Application>().applicationContext.getString(R.string.applicationKey)

        disposable = repo.getLinesStatus(appId, appKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { lines ->
                    tubeLines.value = lines
                    loading.value = false
                    loadError.value = false
                }
            ) { error ->
                loadError.value = true
                loading.value = false
            }
    }
}

