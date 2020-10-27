package uk.co.nelsonwright.londonundergroundstatus.ui.main.testmocks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import uk.co.nelsonwright.londonundergroundstatus.api.TflRepository
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLineStatus
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLinesStatusResult
import uk.co.nelsonwright.londonundergroundstatus.ui.main.shared.getPlannedClosureStatus

class TflRepositoryMock : TflRepository {
    private var tubeLinesStatusResult: MutableLiveData<TubeLinesStatusResult> = MutableLiveData(
        TubeLinesStatusResult()
    )
    private val statusSuspended = TubeLineStatus(4, "Part Suspended", "A 6 minute service is operating")
    private val statusClosure = TubeLineStatus(4, "Planned Closure", "No service due to  operational restrictions")

    private val tubeLinesList = listOf(
        TubeLine("bakerloo", "Bakerloo", listOf(statusSuspended)),
        TubeLine("victoria", "Victoria", listOf(statusSuspended, statusClosure)),
        TubeLine("waterloo-city", "Waterloo & City", listOf(getPlannedClosureStatus()))
    )

    override fun getTubeLines(): LiveData<TubeLinesStatusResult> {
        return getStubbedTubeLinesResult()
    }

    override fun loadTubeLinesForNow(): Disposable? {
        return Disposables.empty()
    }

    override fun loadTubeLinesForWeekend(): Disposable? {
        return Disposables.empty()
    }

    private fun getStubbedTubeLinesResult(): LiveData<TubeLinesStatusResult> {
        return MutableLiveData(getStubbedTubeLineStatusResult())
    }

    private fun getStubbedTubeLinesError(): LiveData<TubeLinesStatusResult> {
        return MutableLiveData(getStubbedTubeLineStatusError())
    }

    private fun getStubbedTubeLineStatusResult(): TubeLinesStatusResult {
        return TubeLinesStatusResult(tubeLines = tubeLinesList, loadingError = false)
    }

    private fun getStubbedTubeLineStatusError(): TubeLinesStatusResult {
        return TubeLinesStatusResult(loadingError = true)
    }
}
