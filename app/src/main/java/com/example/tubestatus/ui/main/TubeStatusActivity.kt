package com.example.tubestatus.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tubestatus.R
import com.example.tubestatus.api.TubeLine
import com.example.tubestatus.models.TubeLineColours
import kotlinx.android.synthetic.main.tube_status_overview_activity.*

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

    private fun setListeners() {
        refresh_button.setOnClickListener {
            viewModel.onRefreshClicked()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onTubeLineClicked(tubeLine: TubeLine) {
        val tube = TubeLineColours.values().firstOrNull() { it.id == tubeLine.id }

        val intent = Intent(this, TubeStatusDetailsActivity::class.java).apply {
            putExtra(EXTRA_LINES, tubeLine)
            tube?.let {
                putExtra(EXTRA_LINE_COLOUR, it.backgroundColour)
            }
        }

        startActivity(intent)
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
        viewModel.getTubeLines().observe(this, Observer { lines ->
            updateView(lines)
        })
        viewModel.getLoadError().observe(this, Observer {
            showLoadError(it)
        })
        viewModel.getLoading().observe(this, Observer {
            showLoadingState(it)
        })
    }

    private fun updateView(lines: List<TubeLine>) {
        (viewAdapter as TubeListAdapter).update(lines)
    }

    private fun showLoadingState(showLoading: Boolean?) {
        progress_bar.visibility = if (showLoading == true) View.VISIBLE else View.GONE
    }

    private fun showLoadError(showError: Boolean) {
        loading_error_group.visibility = if (showError == true) View.VISIBLE else View.GONE
    }
}
