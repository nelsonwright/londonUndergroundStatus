package uk.co.nelsonwright.londonundergroundstatus.ui.main.shared

import uk.co.nelsonwright.londonundergroundstatus.models.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.models.TubeLineStatus
import uk.co.nelsonwright.londonundergroundstatus.models.TubeLinesWithRefreshTime
import uk.co.nelsonwright.londonundergroundstatus.shared.GOOD_SERVICE
import java.time.LocalDateTime

fun stubbedTubeLinesNow(): TubeLinesWithRefreshTime {
    return TubeLinesWithRefreshTime(
        tubeLines = listOf(
            TubeLine("bakerloo", "Bakerloo", listOf(statusPartSuspended())),
            TubeLine("london-overground", "London Overground", listOf(statusGoodService())),
            TubeLine("wycombe", "Wycombe", listOf(statusPlannedClosure())),
            TubeLine("victoria", "Victoria", listOf(statusPartSuspended(), statusPlannedClosure()))
        ),
        refreshTime = LocalDateTime.now()
    )
}

fun stubbedTubeLinesWeekend(): TubeLinesWithRefreshTime {
    return TubeLinesWithRefreshTime(
        tubeLines = listOf(
            TubeLine("bakerloo", "Bakerloo", listOf(statusPartSuspended())),
            TubeLine("central", "Central", listOf(statusGoodService())),
            TubeLine("victoria", "Victoria", listOf(statusPartSuspended(), statusPlannedClosure())),
            TubeLine("wycombe", "Wycombe", listOf(statusPlannedClosure())),
            TubeLine("zanzibar", "Zanzibar", listOf(statusPlannedClosure()))
        ),
        refreshTime = LocalDateTime.now()
    )
}

fun statusPlannedClosure(): TubeLineStatus {
    return TubeLineStatus(
        statusSeverity = 4,
        severityDescription = "Planned Closure",
        reason = "No service until further notice."
    )
}

fun statusPartSuspended(): TubeLineStatus {
    return TubeLineStatus(
        statusSeverity = 4,
        severityDescription = "Part Suspended",
        reason = "A 6 minute service is operating"
    )
}

fun statusGoodService(): TubeLineStatus {
    return TubeLineStatus(
        statusSeverity = GOOD_SERVICE,
        severityDescription = "Good Service",
        reason = "A good service is operating"
    )
}
