package com.example.tubestatus.models

import com.example.tubestatus.api.TubeStatus

data class TubeStatusViewState(
    val loading: Boolean = true,
    val loadingError: Boolean = false,
    val tubeLines: List<TubeStatus>
)