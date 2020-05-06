package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uk.co.nelsonwright.londonundergroundstatus.api.TflRepository

class TubeStatusViewModelFactory(
    private val context: Context,
    private val repo: TflRepository
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Context::class.java, TflRepository::class.java)
            .newInstance(context, repo)
    }
}