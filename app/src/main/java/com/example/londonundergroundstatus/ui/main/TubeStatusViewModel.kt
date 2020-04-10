package com.example.londonundergroundstatus.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.londonundergroundstatus.api.TflApiService
import com.example.londonundergroundstatus.models.TubeStatusViewState
import com.londonundergroundstatus.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


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
                    viewState.value = TubeStatusViewState(tubeLines = lines)
                }
            ) { error ->
                viewState.value = TubeStatusViewState(loadingError = true)
            }
    }
}

