package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.api.TflRepository
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLineStatus
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLinesStatusResult
import uk.co.nelsonwright.londonundergroundstatus.shared.observeOnce

private const val APP_ID = "APP_ID"
private const val APP_KEY = "APP_KEY"

class TubeStatusViewModelTest {
    // swaps the background executor used by the Architecture Components with a
    // different one which executes each task synchronously
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val mockContext = mockk<Context>()
    private val mockRepo = mockk<TflRepository>()
    private lateinit var viewModel: TubeStatusViewModel

    private val statusSuspended = TubeLineStatus("Part Suspended", "A 6 minute service is operating")
    private val statusClosure = TubeLineStatus("Planned Closure", "No service due to  operational restrictions")
    private val tubeLinesList = listOf<TubeLine>(
        TubeLine("bakerloo", "Bakerloo", listOf(statusSuspended)),
        TubeLine("bakerloo", "Circle", listOf(statusSuspended, statusClosure))
    )

    @Before
    fun setup() {
        every { mockContext.getString(R.string.applicationId) } returns APP_ID
        every { mockContext.getString(R.string.applicationKey) } returns APP_KEY
        every { mockRepo.getTubeLines() } returns getStubbedTubeLinesResult()
        every { mockRepo.loadTubeLinesForNow(APP_ID, APP_KEY) } returns null
        every { mockRepo.loadTubeLinesForWeekend(APP_ID, APP_KEY) } returns null

        viewModel = TubeStatusViewModel(mockContext, mockRepo)
    }

    @Test
    fun shouldInitiallyShowLoading() {
        viewModel.viewState.observeOnce {
            assertThat(it.loading).isTrue()
        }
    }

    @Test
    fun shouldInitiallyShowLines() {
        viewModel.mediatorLiveData.observeOnce {
            assertThat(it.tubeLines).isEqualTo(tubeLinesList)
            assertThat(it.loadingError).isEqualTo(false)
        }
    }

    @Test
    fun shouldInitiallyShowError() {
        every { mockRepo.getTubeLines() } returns getStubbedTubeLinesError()

        viewModel = TubeStatusViewModel(mockContext, mockRepo)

        viewModel.mediatorLiveData.observeOnce {
            assertThat(it.loadingError).isEqualTo(true)
            assertThat(it.tubeLines).isEqualTo(emptyList<TubeLine>())
        }
    }

    @Test
    fun shouldInitiallyRequestLineStatusesForNow() {
        verify { mockRepo.loadTubeLinesForNow(APP_ID, APP_KEY) }
    }

    @Test
    fun shouldRequestLineStatusesForNow() {
        viewModel.loadTubeLines(weekend = false)

        verify { mockRepo.loadTubeLinesForNow(APP_ID, APP_KEY) }
        viewModel.mediatorLiveData.observeOnce {
            assertThat(it.tubeLines).isEqualTo(tubeLinesList)
        }
    }

    @Test
    fun shouldRequestLineStatusesForWeekend() {
        viewModel.loadTubeLines(weekend = true)

        verify { mockRepo.loadTubeLinesForWeekend(APP_ID, APP_KEY) }
        viewModel.mediatorLiveData.observeOnce {
            assertThat(it.tubeLines).isEqualTo(tubeLinesList)
        }
    }

    @Test
    fun shouldRefreshLineStatusesForWeekend() {
        viewModel.onRefreshClicked(isWeekendSelected = true)
        verify { mockRepo.loadTubeLinesForWeekend(APP_ID, APP_KEY) }
    }

    @Test
    fun shouldRefreshLineStatusesForNow() {
        viewModel.onRefreshClicked(isWeekendSelected = false)
        verify { mockRepo.loadTubeLinesForNow(APP_ID, APP_KEY) }
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