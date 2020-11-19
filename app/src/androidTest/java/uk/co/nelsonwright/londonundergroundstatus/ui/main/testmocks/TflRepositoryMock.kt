package uk.co.nelsonwright.londonundergroundstatus.ui.main.testmocks

import uk.co.nelsonwright.londonundergroundstatus.api.TflRepository
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.ui.main.shared.stubbedTubeLinesNow
import uk.co.nelsonwright.londonundergroundstatus.ui.main.shared.stubbedTubeLinesWeekend

class TflRepositoryMock : TflRepository {
    override suspend fun loadTubeLines(isWeekendSelected: Boolean): List<TubeLine> {
        if (isWeekendSelected) {
            return stubbedTubeLinesWeekend()
        } else {
            return stubbedTubeLinesNow()
        }
    }
}
