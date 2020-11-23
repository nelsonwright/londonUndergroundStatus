package uk.co.nelsonwright.londonundergroundstatus.ui.main.testmocks

import uk.co.nelsonwright.londonundergroundstatus.api.ServiceLocator
import uk.co.nelsonwright.londonundergroundstatus.api.TflRepository
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils

class ServiceLocatorMock : ServiceLocator {
    override fun getTflRepository(): TflRepository {
        return TflRepositoryMock()
    }

    override fun getCalendarUtils(): CalendarUtils {
        return CalendarUtilsMock()
    }
}