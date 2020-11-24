package uk.co.nelsonwright.londonundergroundstatus.shared

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

interface TimeHelper {
    fun getCurrentDateTime(zoneId: String = "Z"): ZonedDateTime
    fun getCurrentLocalDateTime(): LocalDateTime
}

@Singleton
class TimeHelperImpl @Inject constructor() : TimeHelper {

    override fun getCurrentDateTime(zoneId: String): ZonedDateTime {
        return ZonedDateTime.now(ZoneId.of(zoneId))
    }

    override fun getCurrentLocalDateTime(): LocalDateTime {
        return LocalDateTime.now()
    }
}