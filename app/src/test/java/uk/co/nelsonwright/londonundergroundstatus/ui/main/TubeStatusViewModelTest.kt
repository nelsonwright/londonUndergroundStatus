package uk.co.nelsonwright.londonundergroundstatus.ui.main

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
import uk.co.nelsonwright.londonundergroundstatus.api.*
import uk.co.nelsonwright.londonundergroundstatus.testutils.observeOnce


class TubeStatusViewModelTest {
    // swaps the background executor used by the Architecture Components with a
    // different one which executes each task synchronously
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val mockRepo = mockk<TflRepository>()
    private val mockServiceLocator = mockk<ServiceLocator>()
    private lateinit var viewModel: TubeStatusViewModel

    private val statusSuspended = TubeLineStatus(3, "Part Suspended", "A 6 minute service is operating")
    private val statusClosure = TubeLineStatus(4, "Planned Closure", "No service due to  operational restrictions")
    private val tubeLinesList = listOf(
        TubeLine("bakerloo", "Bakerloo", listOf(statusSuspended)),
        TubeLine("bakerloo", "Circle", listOf(statusSuspended, statusClosure))
    )

    @Before
    fun setup() {
        every { mockRepo.getTubeLines() } returns getStubbedTubeLinesResult()
        every { mockRepo.loadTubeLinesForNow() } returns null
        every { mockRepo.loadTubeLinesForWeekend() } returns null
        every { mockServiceLocator.getTflRepository() } returns mockRepo

        viewModel = TubeStatusViewModel(mockServiceLocator)
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
            assertThat(it.loadingError).isFalse()
        }
    }

    @Test
    fun shouldInitiallyShowError() {
        every { mockRepo.getTubeLines() } returns getStubbedTubeLinesError()

        viewModel = TubeStatusViewModel(mockServiceLocator)

        viewModel.mediatorLiveData.observeOnce {
            assertThat(it.loadingError).isEqualTo(true)
            assertThat(it.tubeLines).isEqualTo(emptyList<TubeLine>())
        }
    }

    @Test
    fun shouldInitiallyRequestLineStatusesForNow() {
        verify { mockRepo.loadTubeLinesForNow() }
    }

    @Test
    fun shouldRequestLineStatusesForNow() {
        viewModel.loadTubeLines(weekend = false)

        verify { mockRepo.loadTubeLinesForNow() }
        viewModel.mediatorLiveData.observeOnce {
            assertThat(it.tubeLines).isEqualTo(tubeLinesList)
        }
    }

    @Test
    fun shouldRequestLineStatusesForWeekend() {
        viewModel.loadTubeLines(weekend = true)

        verify { mockRepo.loadTubeLinesForWeekend() }
        viewModel.mediatorLiveData.observeOnce {
            assertThat(it.tubeLines).isEqualTo(tubeLinesList)
        }
    }

    @Test
    fun shouldRefreshLineStatusesForWeekend() {
        viewModel.onRefreshClicked(isWeekendSelected = true)
        verify { mockRepo.loadTubeLinesForWeekend() }
    }

    @Test
    fun shouldRefreshLineStatusesForNow() {
        viewModel.onRefreshClicked(isWeekendSelected = false)
        verify { mockRepo.loadTubeLinesForNow() }
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