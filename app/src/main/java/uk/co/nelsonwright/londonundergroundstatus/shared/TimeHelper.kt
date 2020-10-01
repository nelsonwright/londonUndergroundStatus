package uk.co.nelsonwright.londonundergroundstatus.shared

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Singleton

@Singleton
class TimeHelper {

    fun getCurrentDateTime(zoneId: String = "Z"): ZonedDateTime {
        return ZonedDateTime.now(ZoneId.of(zoneId))
    }

    fun getCurrentLocalDateTime(): LocalDateTime {
        return LocalDateTime.now()
    }
}