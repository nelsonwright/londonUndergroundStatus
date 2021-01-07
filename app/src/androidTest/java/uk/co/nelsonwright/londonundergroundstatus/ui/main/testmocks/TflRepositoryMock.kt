package uk.co.nelsonwright.londonundergroundstatus.ui.main.testmocks

import uk.co.nelsonwright.londonundergroundstatus.api.TflRepository
import uk.co.nelsonwright.londonundergroundstatus.models.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.ui.main.shared.stubbedTubeLinesNow
import uk.co.nelsonwright.londonundergroundstatus.ui.main.shared.stubbedTubeLinesWeekend

class TflRepositoryMock : TflRepository {
    override suspend fun loadTubeLines(isNowSelected: Boolean): List<TubeLine> {
        if (isNowSelected) {
            return stubbedTubeLinesNow()
        } else {
            return stubbedTubeLinesWeekend()
        }
    }
}
