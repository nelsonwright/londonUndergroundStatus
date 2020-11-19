package uk.co.nelsonwright.londonundergroundstatus.models

import uk.co.nelsonwright.londonundergroundstatus.shared.GOOD_SERVICE
import java.io.Serializable

data class TubeLine(
    val id: String = "",
    val name: String? = "",
    val lineStatuses: List<TubeLineStatus>? = emptyList()
) : Serializable {

    fun notGoodService(): Boolean {
        return lineStatuses?.any {
            it.statusSeverity != GOOD_SERVICE
        } ?: false
    }
}

