package uk.co.nelsonwright.londonundergroundstatus.api

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import uk.co.nelsonwright.londonundergroundstatus.models.TubeLineStatus

@Keep
data class ApiTubeLineStatus(
    @SerializedName("statusSeverity") val statusSeverity: Int,
    @SerializedName("statusSeverityDescription") val severityDescription: String? = "",
    @SerializedName("reason") val reason: String? = ""
) {
    fun toModel(): TubeLineStatus {
        return TubeLineStatus(
            statusSeverity = statusSeverity,
            severityDescription = severityDescription,
            reason = reason
        )
    }
}