package uk.co.nelsonwright.londonundergroundstatus.api

data class TubeLineResult(
    val tubeLines: List<TubeLine> = emptyList(),
    val loadingError: Boolean = false
)