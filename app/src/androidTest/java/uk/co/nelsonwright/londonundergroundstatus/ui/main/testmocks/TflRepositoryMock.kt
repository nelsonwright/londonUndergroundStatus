package uk.co.nelsonwright.londonundergroundstatus.ui.main.testmocks

import uk.co.nelsonwright.londonundergroundstatus.api.TflRepository
import uk.co.nelsonwright.londonundergroundstatus.models.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.ui.main.SelectionType
import uk.co.nelsonwright.londonundergroundstatus.ui.main.SelectionType.NOW
import uk.co.nelsonwright.londonundergroundstatus.ui.main.shared.stubbedTubeLinesNow
import uk.co.nelsonwright.londonundergroundstatus.ui.main.shared.stubbedTubeLinesWeekend

class TflRepositoryMock : TflRepository {
    override suspend fun loadTubeLines(selectionType: SelectionType, useCacheRequest: Boolean): List<TubeLine> {
        if (selectionType == NOW) {
            return stubbedTubeLinesNow()
        } else {
            return stubbedTubeLinesWeekend()
        }
    }
}
