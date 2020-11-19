package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_overview.*
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.TubeStatusApplication
import uk.co.nelsonwright.londonundergroundstatus.models.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.models.TubeLineColours
import uk.co.nelsonwright.londonundergroundstatus.models.TubeStatusViewState
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import javax.inject.Inject


private const val NOW_SELECTED = 0
private const val WEEKEND_SELECTED = 1

class TubeOverviewFragment : Fragment(), TubeListClickListener {
    @Inject
    lateinit var calendarUtils: CalendarUtils

    @Inject
    lateinit var viewModelFactory: TubeOverviewViewModelFactory
    private val viewModel: TubeOverviewViewModel by viewModels { viewModelFactory }

    private lateinit var viewAdapter: TubeListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var lastSelectedSpinnerPosition = NOW_SELECTED

    private val isWeekendSelected: Boolean
        get() {
            return status_date_spinner.selectedItemPosition == WEEKEND_SELECTED
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as TubeStatusApplication).component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val colorDrawable = ColorDrawable(requireContext().getColor(R.color.colorPrimary))
        (requireActivity() as AppCompatActivity).supportActionBar?.setBackgroundDrawable(colorDrawable)
        setHasOptionsMenu(true)
        setupRecyclerView()
        observeViewModel()
        setListeners()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        lastSelectedSpinnerPosition = viewModel.spinnerPosition
    }

    override fun onTubeLineClicked(tubeLine: TubeLine) {
        val tube = TubeLineColours.values()
            .firstOrNull { it.id == tubeLine.id }

        viewModel.spinnerPosition = lastSelectedSpinnerPosition

        val action =
            TubeOverviewFragmentDirections.actionOverviewFragmentToDetailsFragment(tubeLine, tube?.backgroundColour)

        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tube_status_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_refresh -> {
                swipe_refresh.isRefreshing = true
                viewModel.loadTubeLines(isWeekendSelected)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        viewManager = LinearLayoutManager(requireContext())
        viewAdapter = TubeListAdapter(arrayListOf(), this, requireContext())

        lines_recycler_view.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner, { state ->
            updateView(state)
        })
    }

    private fun updateView(state: TubeStatusViewState) {
        with(state) {
            updateLoadingIndicator(loading)
            updateViewVisibilities(error = loadingError)
            refresh_date.text = getString(R.string.refresh_date, refreshDate)
            viewAdapter.update(tubeLines)
        }
    }

    private fun setListeners() {
        refresh_button.setOnClickListener {
            viewModel.loadTubeLines(isWeekendSelected)
        }
        swipe_refresh.setOnRefreshListener {
            viewModel.loadTubeLines(isWeekendSelected)
        }

        setupDateDropdown()
    }

    private fun setupDateDropdown() {
        val dropdownList = listOf(
            getString(R.string.now),
            getString(R.string.weekend_of, calendarUtils.getFormattedSaturdayDate())
        )

        ArrayAdapter(requireContext(), R.layout.status_spinner_item, dropdownList)
            .also { adapter ->
                // the layout for when the list of choices appears, i.e when the down arrow is tapped
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // assign the adapter to the spinner
                status_date_spinner.adapter = adapter
            }

        status_date_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != lastSelectedSpinnerPosition) {
                    viewModel.loadTubeLines(isWeekendSelected)
                    lastSelectedSpinnerPosition = position
                    viewModel.spinnerPosition = lastSelectedSpinnerPosition
                }
            }
        }
    }

    private fun updateViewVisibilities(error: Boolean) {
        if (error) {
            refresh_date.visibility = GONE
            lines_recycler_view.visibility = GONE
            loading_error_group.visibility = VISIBLE
        } else {
            refresh_date.visibility = VISIBLE
            lines_recycler_view.visibility = VISIBLE
            loading_error_group.visibility = GONE
        }
    }

    private fun updateLoadingIndicator(loading: Boolean) {
        swipe_refresh.isRefreshing = loading
    }
}