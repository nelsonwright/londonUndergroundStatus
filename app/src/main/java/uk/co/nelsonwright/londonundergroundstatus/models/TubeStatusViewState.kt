package uk.co.nelsonwright.londonundergroundstatus.models

data class TubeStatusViewState(
    val loadingError: Boolean = false,
    val tubeLines: List<TubeLine> = emptyList(),
    val refreshDate: String = "",
    val loading: Boolean = false
)