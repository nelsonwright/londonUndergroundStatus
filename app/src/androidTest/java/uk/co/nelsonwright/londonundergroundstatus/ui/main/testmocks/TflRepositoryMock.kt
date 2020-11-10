package uk.co.nelsonwright.londonundergroundstatus.ui.main.testmocks

import uk.co.nelsonwright.londonundergroundstatus.api.TflRepository
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.ui.main.shared.stubbedTubeLinesNow
import uk.co.nelsonwright.londonundergroundstatus.ui.main.shared.stubbedTubeLinesWeekend

class TflRepositoryMock : TflRepository {
    override suspend fun loadTubeLinesForNow(): List<TubeLine> {
        return stubbedTubeLinesNow()
    }

    override suspend fun loadTubeLinesForWeekend(): List<TubeLine> {
        return stubbedTubeLinesWeekend()
    }
}
