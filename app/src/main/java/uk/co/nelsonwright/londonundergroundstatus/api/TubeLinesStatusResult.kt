package uk.co.nelsonwright.londonundergroundstatus.api

data class TubeLinesStatusResult(
    val tubeLines: List<TubeLine> = emptyList(),
    val loadingError: Boolean = false,
    val timestamp: String = ""
)