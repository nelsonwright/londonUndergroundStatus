package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.content.Context
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
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.TubeStatusApplication
import uk.co.nelsonwright.londonundergroundstatus.databinding.FragmentOverviewBinding
import uk.co.nelsonwright.londonundergroundstatus.models.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.models.TubeLineColours
import uk.co.nelsonwright.londonundergroundstatus.models.TubeStatusViewState
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import uk.co.nelsonwright.londonundergroundstatus.ui.main.SelectionType.*
import javax.inject.Inject

private const val NOW_SELECTED = 0
private const val TOMORROW_SELECTED = 1
private const val WEEKEND_SELECTED = 2

class TubeOverviewFragment @Inject constructor(
    private val calendarUtils: CalendarUtils,
    private val viewModelFactory: TubeOverviewViewModelFactory
) : Fragment(), TubeListClickListener {

    private val viewModel: TubeOverviewViewModel by viewModels { viewModelFactory }

    private lateinit var viewAdapter: TubeListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var lastSelectedSpinnerPosition = NOW_SELECTED
    private var _binding: FragmentOverviewBinding? = null

    private val selectionType: SelectionType
        get() {
            return when (binding.statusDateSpinner.selectedItemPosition) {
                WEEKEND_SELECTED -> WEEKEND
                TOMORROW_SELECTED -> TOMORROW
                else -> NOW
            }
        }

    // This property is only valid between onCreateView and onDestroyView...
    private val binding
        get() = _binding!!

    override fun onAttach(context: Context) {
        (context.applicationContext as TubeStatusApplication).appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBarColour()
        setHasOptionsMenu(true)
        setupRecyclerView()
        observeViewModel()
        setListeners()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        lastSelectedSpinnerPosition = viewModel.spinnerPosition
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                binding.swipeRefresh.isRefreshing = true
                viewModel.refreshTubeLines(selectionType)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setActionBarColour() {
        val activity = requireActivity()
        val colorDrawable = ColorDrawable(requireContext().getColor(R.color.colorPrimary))
        if (activity is AppCompatActivity) {
            activity.supportActionBar?.setBackgroundDrawable(colorDrawable)
        }
    }

    private fun setupRecyclerView() {
        viewManager = LinearLayoutManager(requireContext())
        viewAdapter = TubeListAdapter(arrayListOf(), this)

        binding.linesRecyclerView.apply {
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
            binding.refreshDate.text = getString(R.string.refresh_date, refreshDate)
            viewAdapter.update(tubeLines)
        }
    }

    private fun setListeners() {
        binding.refreshButton.setOnClickListener {
            viewModel.refreshTubeLines(selectionType)
        }
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshTubeLines(selectionType)
        }

        setupDateDropdown()
    }

    private fun setupDateDropdown() {
        val dropdownList = listOf(
            getString(R.string.now),
            getString(R.string.tomorrow),
            getString(R.string.weekend_of, calendarUtils.getFormattedSaturdayDate())
        )

        ArrayAdapter(requireContext(), R.layout.status_spinner_item, dropdownList)
            .also { adapter ->
                // the layout for when the list of choices appears, i.e when the down arrow is tapped
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // assign the adapter to the spinner
                binding.statusDateSpinner.adapter = adapter
            }

        binding.statusDateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != lastSelectedSpinnerPosition) {
                    viewModel.loadTubeLines(selectionType)
                    lastSelectedSpinnerPosition = position
                    viewModel.spinnerPosition = lastSelectedSpinnerPosition
                }
            }
        }
    }

    private fun updateViewVisibilities(error: Boolean) {
        if (error) {
            binding.refreshDate.visibility = GONE
            binding.linesRecyclerView.visibility = GONE
            binding.loadingErrorGroup.visibility = VISIBLE
        } else {
            binding.refreshDate.visibility = VISIBLE
            binding.linesRecyclerView.visibility = VISIBLE
            binding.loadingErrorGroup.visibility = GONE
        }
    }

    private fun updateLoadingIndicator(loading: Boolean) {
        binding.swipeRefresh.isRefreshing = loading
    }
}