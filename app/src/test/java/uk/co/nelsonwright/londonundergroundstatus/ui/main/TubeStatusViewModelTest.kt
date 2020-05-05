package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.api.TflRepository
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLinesStatusResult

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

    @Before
    fun setup() {
        every { mockContext.getString(R.string.applicationId) } returns APP_ID
        every { mockContext.getString(R.string.applicationKey) } returns APP_KEY
        every { mockRepo.getTubeLines() } returns getEmptyTubeLines()
        every { mockRepo.loadTubeLinesForNow(any(), any()) } returns null

        viewModel = TubeStatusViewModel(mockContext, mockRepo)
    }

    @Test
    fun shouldInitiallyShowLoading() {
        assertThat(viewModel.getLoading().observeOnce { loading ->
            assertThat(loading).isTrue()
        })
    }

    private fun getEmptyTubeLines(): LiveData<TubeLinesStatusResult> {
        return MutableLiveData(TubeLinesStatusResult())
    }
}