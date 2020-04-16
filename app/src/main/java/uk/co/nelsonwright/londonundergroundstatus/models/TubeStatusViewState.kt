package uk.co.nelsonwright.londonundergroundstatus.models

import uk.co.nelsonwright.londonundergroundstatus.api.TubeLine

data class TubeStatusViewState(
    val loading: Boolean = false,
    val loadingError: Boolean = false,
    val tubeLines: List<TubeLine> = emptyList(),
    val refreshDate: String = ""
)