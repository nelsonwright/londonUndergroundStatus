package uk.co.nelsonwright.londonundergroundstatus.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uk.co.nelsonwright.londonundergroundstatus.api.ServiceLocator
import uk.co.nelsonwright.londonundergroundstatus.api.TflRepository
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLineStatus
import uk.co.nelsonwright.londonundergroundstatus.shared.GOOD_SERVICE
import uk.co.nelsonwright.londonundergroundstatus.testutils.observeOnce


@ExperimentalCoroutinesApi
class TubeStatusViewModelTest {
    // swaps the background executor used by the Architecture Components with a
    // different one which executes each task synchronously
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing, i.e. uses the TestCoroutineDispatcher
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @MockK
    lateinit var mockRepo: TflRepository

    @MockK
    lateinit var mockServiceLocator: ServiceLocator

    private lateinit var viewModel: TubeStatusViewModel

    private val tubeLinesNow = stubbedTubeLinesNow()
    private val tubeLinesWeekend = stubbedTubeLinesWeekend()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        stubRepoResponses()
        viewModel =
            TubeStatusViewModel(mockServiceLocator, mainCoroutineRule.testDispatcher, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun shouldInitiallyShowLoading() = mainCoroutineRule.runBlockingTest {
        mainCoroutineRule.testDispatcher.pauseDispatcher()

        val viewModel =
            TubeStatusViewModel(
                serviceLocator = mockServiceLocator,
                mainDispatcher = Dispatchers.Unconfined,
                ioDispatcher = mainCoroutineRule.testDispatcher
            )

        viewModel.viewState.value?.let {
            assertThat(it.loading).isTrue
        }
    }

    @Test
    fun shouldInitiallyShowLines() {
        viewModel.viewState.observeOnce {
            assertThat(it.tubeLines).isEqualTo(tubeLinesNow)
            assertThat(it.loadingError).isFalse()
        }
    }

    @Test
    fun shouldInitiallyShowError() {
        coEvery { mockRepo.loadTubeLinesForNow() } throws Exception("error")

        viewModel =
            TubeStatusViewModel(mockServiceLocator, mainCoroutineRule.testDispatcher, mainCoroutineRule.testDispatcher)

        viewModel.viewState.observeOnce {
            assertThat(it.loadingError).isEqualTo(true)
            assertThat(it.tubeLines).isEqualTo(emptyList<TubeLine>())
        }
    }

    @Test
    fun shouldInitiallyRequestLineStatusesForNow() {
        coVerify { mockRepo.loadTubeLinesForNow() }
    }

    @Test
    fun shouldRequestLineStatusesForNow() {
        viewModel.loadTubeLines(isWeekend = false)

        coVerify { mockRepo.loadTubeLinesForNow() }
        viewModel.viewState.observeOnce {
            assertThat(it.tubeLines).isEqualTo(tubeLinesNow)
        }
    }

    @Test
    fun shouldRequestLineStatusesForWeekend() {
        viewModel.loadTubeLines(isWeekend = true)

        coVerify { mockRepo.loadTubeLinesForWeekend() }
        viewModel.viewState.observeOnce {
            assertThat(it.tubeLines).isEqualTo(tubeLinesWeekend)
        }
    }

    private fun stubRepoResponses() {
        coEvery { mockRepo.loadTubeLinesForNow() } returns stubbedTubeLinesNow()
        coEvery { mockRepo.loadTubeLinesForWeekend() } returns stubbedTubeLinesWeekend()
        every { mockServiceLocator.getTflRepository() } returns mockRepo
    }

    private fun stubbedTubeLinesNow(): List<TubeLine> {
        return listOf(
            TubeLine("bakerloo", "Bakerloo", listOf(statusPartSuspended())),
            TubeLine("london-overground", "London Overground", listOf(statusGoodService())),
            TubeLine("wycombe", "Wycombe", listOf(statusPlannedClosure())),
            TubeLine("victoria", "Victoria", listOf(statusPartSuspended(), statusPlannedClosure()))
        )
    }

    private fun stubbedTubeLinesWeekend(): List<TubeLine> {
        return listOf(
            TubeLine("bakerloo", "Bakerloo", listOf(statusPartSuspended())),
            TubeLine("central", "Central", listOf(statusGoodService())),
            TubeLine("victoria", "Victoria", listOf(statusPartSuspended(), statusPlannedClosure())),
            TubeLine("wycombe", "Wycombe", listOf(statusPlannedClosure())),
            TubeLine("zanzibar", "Zanzibar", listOf(statusPlannedClosure()))
        )
    }

    private fun statusPlannedClosure(): TubeLineStatus {
        return TubeLineStatus(
            statusSeverity = 4,
            severityDescription = "Planned Closure",
            reason = "No service until further notice."
        )
    }

    private fun statusPartSuspended(): TubeLineStatus {
        return TubeLineStatus(
            statusSeverity = 4,
            severityDescription = "Part Suspended",
            reason = "A 6 minute service is operating"
        )
    }

    private fun statusGoodService(): TubeLineStatus {
        return TubeLineStatus(
            statusSeverity = GOOD_SERVICE,
            severityDescription = "Good Service",
            reason = "A good service is operating"
        )
    }

}