package com.example.tubestatus.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tubestatus.R
import com.example.tubestatus.TflApiService
import com.example.tubestatus.api.TubeStatus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class TubeStatusViewModel(application: Application) : AndroidViewModel(application) {

    val repo by lazy {
        TflApiService.create()
    }

    var disposable: Disposable? = null

    val tubeLines: MutableLiveData<List<TubeStatus>> = MutableLiveData()

    init {
        loadTubeLines()
    }

    fun getTubeLines(): LiveData<List<TubeStatus>> {
        return tubeLines
    }

    fun onPause() {
        disposable?.dispose()
    }

    private fun loadTubeLines() {
        val appId =
            getApplication<Application>().applicationContext.getString(R.string.applicationId)
        val appKey =
            getApplication<Application>().applicationContext.getString(R.string.applicationKey)

        disposable = repo.getLinesStatus(appId, appKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                tubeLines.value = it
            }
    }

}
