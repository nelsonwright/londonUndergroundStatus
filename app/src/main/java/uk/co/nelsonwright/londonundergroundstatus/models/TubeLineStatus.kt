package uk.co.nelsonwright.londonundergroundstatus.models

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class TubeLineStatus(
    val statusSeverity: Int,
    val severityDescription: String? = "",
    val reason: String? = ""
) : Serializable