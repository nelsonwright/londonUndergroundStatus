package uk.co.nelsonwright.londonundergroundstatus.shared

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.util.*

class CalendarUtilsTest {
    private val mockTimeHelper = mockk<TimeHelper>()
    private val currentTime = Calendar.getInstance()
    private lateinit var calendarUtils: CalendarUtils

    @Before
    fun setup() {
        setupMockTimeHelper()
        calendarUtils = CalendarUtils(mockTimeHelper)
    }

    @Test
    fun shouldGetFormattedNowDate() {
        val expectedDateString = "Tue, Sep 1 17:32:58"
        val formattedNowDate = calendarUtils.getFormattedNowDate()
        assertThat(formattedNowDate).isEqualTo(expectedDateString)
    }

    @Test
    fun shouldGetFormattedSaturdayDate() {
        val expectedDateString = "5 Sep"
        val formattedSaturdayDate = calendarUtils.getFormattedSaturdayDate()
        assertThat(formattedSaturdayDate).isEqualTo(expectedDateString)
    }

    @Test
    fun shouldGetWeekendDates() {
        val expectedSaturday = "2020-09-05"
        val expectedSunday = "2020-09-06T23:59:58+0100"

        val (saturday, sunday) = calendarUtils.getWeekendDates()

        assertThat(saturday).isEqualTo(expectedSaturday)
        assertThat(sunday).isEqualTo(expectedSunday)
    }

    private fun setupMockTimeHelper() {
        currentTime.set(Calendar.YEAR, 2020)
        currentTime.set(Calendar.MONTH, 8)
        currentTime.set(Calendar.DAY_OF_MONTH, 1)
        currentTime.set(Calendar.HOUR_OF_DAY, 17)
        currentTime.set(Calendar.MINUTE, 32)
        currentTime.set(Calendar.SECOND, 58)
        every { mockTimeHelper.getCurrentDateTime() } returns currentTime
    }
}