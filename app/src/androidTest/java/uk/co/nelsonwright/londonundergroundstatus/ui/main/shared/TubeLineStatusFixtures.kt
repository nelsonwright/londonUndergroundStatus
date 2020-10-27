package uk.co.nelsonwright.londonundergroundstatus.ui.main.shared

import uk.co.nelsonwright.londonundergroundstatus.api.TubeLineStatus

fun getPlannedClosureStatus(): TubeLineStatus {
    return TubeLineStatus(
        statusSeverity = 4,
        severityDescription = "Planned Closure",
        reason = "No service until further notice."
    )
}
