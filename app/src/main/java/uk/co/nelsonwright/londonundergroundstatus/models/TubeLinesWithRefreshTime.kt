package uk.co.nelsonwright.londonundergroundstatus.models

import java.time.LocalDateTime

data class TubeLinesWithRefreshTime(
    var tubeLines: List<TubeLine> = emptyList(),
    var refreshTime: LocalDateTime = LocalDateTime.now()
)