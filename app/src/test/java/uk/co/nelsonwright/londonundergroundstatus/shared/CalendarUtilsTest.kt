package uk.co.nelsonwright.londonundergroundstatus.shared

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

private const val LONDON_TIME_ZONE = "Europe/London"

class CalendarUtilsTest {
    private val mockTimeHelper = mockk<TimeHelper>()
    private val currentTime = LocalDateTime.of(2020, 9, 1, 17, 32, 58)
    private lateinit var calendarUtils: CalendarUtils

    @Before
    fun setup() {
        setupMockTimeHelper()
        calendarUtils = CalendarUtilsImpl(mockTimeHelper)
    }

    @Test
    fun shouldGetFormattedNowDate() {
        val expectedDateString = "Tue, Sep 1 17:32:58"
        val formattedNowDate = calendarUtils.getFormattedLocateDateTime()
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
        every { mockTimeHelper.getCurrentLocalDateTime() } returns currentTime
        every { mockTimeHelper.getCurrentDateTime(LONDON_TIME_ZONE) } returns currentTime.atZone(
            ZoneId.of(
                LONDON_TIME_ZONE
            )
        )
    }
}