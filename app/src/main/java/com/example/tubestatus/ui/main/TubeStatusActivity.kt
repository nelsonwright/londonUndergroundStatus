package com.example.tubestatus.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tubestatus.R
import com.example.tubestatus.api.TubeStatus
import kotlinx.android.synthetic.main.tube_status_activity.*

class TubeStatusActivity : AppCompatActivity(), TubeListClickListener {
    private val viewModel: TubeStatusViewModel by viewModels()

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tube_status_activity)
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

    override fun onTubeLineClicked(view: View) {
        Toast.makeText(this, "Clicked a tube line", LENGTH_SHORT).show()
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

    private fun updateView(lines: List<TubeStatus>) {
        (viewAdapter as TubeListAdapter).update(lines)
    }

    private fun showLoadingState(showLoading: Boolean?) {
        progress_bar.visibility = if (showLoading == true) View.VISIBLE else View.GONE
    }

    private fun showLoadError(showError: Boolean) {
        loading_error_group.visibility = if (showError == true) View.VISIBLE else View.GONE
    }
}
