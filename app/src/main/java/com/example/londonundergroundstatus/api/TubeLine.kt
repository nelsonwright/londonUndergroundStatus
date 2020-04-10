package com.example.londonundergroundstatus.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TubeLine(
    @SerializedName("id") val id: String = "",
    @SerializedName("name") val name: String? = "",
    @SerializedName("lineStatuses") val lineStatuses: List<TubeLineStatus>? = emptyList()
) : Serializable

data class TubeLineStatus(
    @SerializedName("statusSeverityDescription") val severityDescription: String? = "",
    @SerializedName("reason") val reason: String? = ""
) : Serializable
