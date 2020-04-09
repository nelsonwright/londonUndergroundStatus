package com.example.tubestatus.models

import com.example.tubestatus.api.TubeLine

data class TubeStatusViewState(
    val loading: Boolean = true,
    val loadingError: Boolean = false,
    val tubeLines: List<TubeLine>
)