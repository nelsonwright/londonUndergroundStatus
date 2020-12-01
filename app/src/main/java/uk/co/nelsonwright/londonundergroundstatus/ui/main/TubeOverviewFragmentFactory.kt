package uk.co.nelsonwright.londonundergroundstatus.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import uk.co.nelsonwright.londonundergroundstatus.api.TflRepositoryImpl
import uk.co.nelsonwright.londonundergroundstatus.api.TflService
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtilsImpl
import uk.co.nelsonwright.londonundergroundstatus.shared.TimeHelperImpl
import javax.inject.Inject

class TubeOverviewFragmentFactory @Inject constructor(
    private val calendarUtils: CalendarUtils,
    private val viewModelFactory: TubeOverviewViewModelFactory
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        if (className == TubeOverviewFragment::class.java.name) {
            return TubeOverviewFragment(calendarUtils, viewModelFactory)
        }
        return super.instantiate(classLoader, className)
    }
}

fun getFragmentFactory(): TubeOverviewFragmentFactory {
    val calendarUtils = CalendarUtilsImpl(TimeHelperImpl())

    return TubeOverviewFragmentFactory(
        calendarUtils,
        TubeOverviewViewModelFactory(
            TflRepositoryImpl(TflService.create(), calendarUtils),
            calendarUtils
        )
    )
}