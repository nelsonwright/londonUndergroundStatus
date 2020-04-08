package com.example.tubestatus.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.Query

data class TubeStatus(
    @SerializedName("id") val id: String = "",
    @SerializedName("name") val name: String? = "",
    @SerializedName("lineStatuses") val lineStatuses: List<TubeLineStatus>? = emptyList()
)

data class TubeLineStatus(
    @SerializedName("statusSeverityDescription") val severityDescription: String? = "",
    @SerializedName("reason") val reason: String? = ""
)

data class TubeStatusResult(val query: Query)
