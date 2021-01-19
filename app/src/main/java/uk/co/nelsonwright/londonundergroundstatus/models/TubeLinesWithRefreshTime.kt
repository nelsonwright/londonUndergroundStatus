package uk.co.nelsonwright.londonundergroundstatus.models

import java.time.LocalDateTime

data class TubeLinesWithRefreshTime(
        val tubeLines: List<TubeLine> = emptyList(),
        val refreshTime: LocalDateTime = LocalDateTime.now()
)