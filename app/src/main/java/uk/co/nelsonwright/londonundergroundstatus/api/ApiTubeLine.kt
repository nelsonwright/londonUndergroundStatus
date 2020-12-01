package uk.co.nelsonwright.londonundergroundstatus.api

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import uk.co.nelsonwright.londonundergroundstatus.models.TubeLine

@Keep
data class ApiTubeLine(
    @SerializedName("id") val id: String = "",
    @SerializedName("name") val name: String? = "",
    @SerializedName("lineStatuses") val lineStatuses: List<ApiTubeLineStatus>? = emptyList()
) {
    fun toModel(): TubeLine {
        return TubeLine(
            id = id,
            name = name,
            lineStatuses = lineStatuses?.map { tubeLineStatus ->
                tubeLineStatus.toModel()
            }
        )
    }
}
