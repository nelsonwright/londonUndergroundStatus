package uk.co.nelsonwright.londonundergroundstatus.api

import com.google.gson.annotations.SerializedName
import uk.co.nelsonwright.londonundergroundstatus.shared.GOOD_SERVICE
import java.io.Serializable

data class TubeLine(
    @SerializedName("id") val id: String = "",
    @SerializedName("name") val name: String? = "",
    @SerializedName("lineStatuses") val lineStatuses: List<TubeLineStatus>? = emptyList()
) : Serializable {

    fun notGoodService(): Boolean {
        return lineStatuses?.any {
            it.statusSeverity != GOOD_SERVICE
        } ?: false
    }
}

data class TubeLineStatus(
    @SerializedName("statusSeverity") val statusSeverity: Int,
    @SerializedName("statusSeverityDescription") val severityDescription: String? = "",
    @SerializedName("reason") val reason: String? = ""
) : Serializable
