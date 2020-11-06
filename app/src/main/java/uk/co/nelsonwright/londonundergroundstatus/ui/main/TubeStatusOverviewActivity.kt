package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.tube_status_overview_activity.*
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.TubeStatusApplication
import uk.co.nelsonwright.londonundergroundstatus.api.ServiceLocator
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.models.TubeLineColours
import uk.co.nelsonwright.londonundergroundstatus.models.TubeStatusViewState
import uk.co.nelsonwright.londonundergroundstatus.shared.CalendarUtils
import javax.inject.Inject

private const val WEEKEND_SELECTED = 1

class TubeStatusOverviewActivity : AppCompatActivity(), TubeListClickListener, AdapterView.OnItemSelectedListener {

    @Inject
    lateinit var calendarUtils: CalendarUtils

    @Inject
    lateinit var serviceLocator: ServiceLocator

    private lateinit var viewModelFactory: TubeStatusViewModelFactory
    private val viewModel: TubeStatusViewModel by viewModels { viewModelFactory }

    private lateinit var viewAdapter: TubeListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val isWeekendSelected: Boolean
        get() {
            return status_date_spinner.selectedItemPosition == WEEKEND_SELECTED
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tube_status_overview_activity)
        (application as TubeStatusApplication).tubeStatusComponent.inject(this)

        viewModelFactory = TubeStatusViewModelFactory(serviceLocator = serviceLocator)
        setupRecyclerView()
        observeViewModel()
        setListeners()
    }

    override fun onTubeLineClicked(tubeLine: TubeLine) {
        val tube = TubeLineColours.values()
            .firstOrNull { it.id == tubeLine.id }

        val intent = Intent(this, TubeStatusDetailsActivity::class.java)
            .apply {
                putExtra(EXTRA_TUBE_LINE, tubeLine)
                tube?.let {
                    putExtra(EXTRA_LINE_COLOUR, it.backgroundColour)
                }
            }

        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.tube_status_menu, menu)
        return true
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

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // do nothing
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.loadTubeLines(isWeekendSelected)
    }

    private fun setupRecyclerView() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = TubeListAdapter(arrayListOf(), this, this)

        lines_recycler_view.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.viewState.observe(this, { state ->
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
            viewModel.onRefreshClicked(isWeekendSelected)
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

        ArrayAdapter(applicationContext, R.layout.status_spinner_item, dropdownList)
            .also { adapter ->
                // the layout for when the list of choices appears, i.e when the down arrow is tapped
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // assign the adapter to the spinner
                status_date_spinner.adapter = adapter
            }
        status_date_spinner.onItemSelectedListener = this
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
