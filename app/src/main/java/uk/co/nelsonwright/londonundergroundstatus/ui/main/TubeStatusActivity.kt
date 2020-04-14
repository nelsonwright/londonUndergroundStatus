package com.example.londonundergroundstatus.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.londonundergroundstatus.api.TubeLine
import com.example.londonundergroundstatus.models.TubeLineColours
import com.example.londonundergroundstatus.models.TubeStatusViewState
import kotlinx.android.synthetic.main.tube_status_overview_activity.*
import uk.co.nelsonwright.londonundergroundstatus.R

class TubeStatusActivity : AppCompatActivity(), TubeListClickListener {
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.tube_status_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        return when (item.getItemId()) {
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
        viewAdapter = TubeListAdapter(this, arrayListOf(), this)

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
    }

    private fun updateView(viewState: TubeStatusViewState) {
        with(viewState) {
            swipe_refresh.isRefreshing = false
            (viewAdapter as TubeListAdapter).update(tubeLines)

            progress_bar.visibility = if (loading) View.VISIBLE else View.GONE

            lines_recycler_view.visibility = if (loadingError) View.GONE else View.VISIBLE
            loading_error_group.visibility = if (loadingError) View.VISIBLE else View.GONE
        }

    }
}
