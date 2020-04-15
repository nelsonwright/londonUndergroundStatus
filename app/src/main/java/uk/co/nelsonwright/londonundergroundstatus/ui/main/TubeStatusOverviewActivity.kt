package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.tube_status_overview_activity.*
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.models.TubeLineColours
import uk.co.nelsonwright.londonundergroundstatus.models.TubeStatusViewState

class TubeStatusOverviewActivity : AppCompatActivity(), TubeListClickListener, AdapterView.OnItemSelectedListener {
    private val viewModel: TubeStatusViewModel by viewModels()

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tube_status_overview_activity)
        setupRecyclerView()
        observeViewModel()
        setListeners()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onTubeLineClicked(tubeLine: TubeLine) {
        val tube = TubeLineColours.values().firstOrNull { it.id == tubeLine.id }

        val intent = Intent(this, TubeStatusDetailsActivity::class.java).apply {
            putExtra(EXTRA_LINES, tubeLine)
            tube?.let {
                putExtra(EXTRA_LINE_COLOUR, it.backgroundColour)
            }
        }

        startActivity(intent)
    }

    private fun onStatusDateChanged(position: Int) {
        when (position) {
            1 -> viewModel.loadTubeLinesForWeekend()
            else -> viewModel.loadTubeLines()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.tube_status_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_refresh -> {
                swipe_refresh.isRefreshing = true
                viewModel.loadTubeLines()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = TubeListAdapter(arrayListOf(), this)

        lines_recycler_view.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.getViewState().observe(this, Observer { state ->
            updateView(state)
        })
    }

    private fun setListeners() {
        refresh_button.setOnClickListener {
            viewModel.onRefreshClicked()
        }
        swipe_refresh.setOnRefreshListener {
            viewModel.loadTubeLines()
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.now_or_weekend_array,
            R.layout.status_spinner_item
        ).also { adapter ->
            // the layout for when the list of choices appears, i.e when the down arrow is tapped
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // assign the adapter to the spinner
            status_date_spinner.adapter = adapter
        }
        status_date_spinner.onItemSelectedListener = this
    }

    private fun updateView(viewState: TubeStatusViewState) {
        with(viewState) {
            swipe_refresh.isRefreshing = false
            progress_bar.visibility = if (loading) View.VISIBLE else View.GONE

            (viewAdapter as TubeListAdapter).update(tubeLines)

            refresh_date.text = getString(R.string.refresh_date, refreshDate)

            refresh_date.visibility = if (loadingError) View.GONE else View.VISIBLE
            lines_recycler_view.visibility = if (loadingError) View.GONE else View.VISIBLE
            loading_error_group.visibility = if (loadingError) View.VISIBLE else View.GONE
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // do nothing
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        onStatusDateChanged(position)
    }
}
