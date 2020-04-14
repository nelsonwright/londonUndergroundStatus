package com.example.londonundergroundstatus.models

import com.example.londonundergroundstatus.api.TubeLine

data class TubeStatusViewState(
    val loading: Boolean = false,
    val loadingError: Boolean = false,
    val tubeLines: List<TubeLine> = emptyList()
)