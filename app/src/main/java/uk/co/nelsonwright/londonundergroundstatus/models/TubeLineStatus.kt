package uk.co.nelsonwright.londonundergroundstatus.models

import java.io.Serializable

data class TubeLineStatus(
    val statusSeverity: Int,
    val severityDescription: String? = "",
    val reason: String? = ""
) : Serializable