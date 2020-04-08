package com.example.tubestatus.ui.main

import android.os.Bundle
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
        viewModel.getTubeLines().observe(this, Observer { lines ->
            updateView(lines)
        })
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun setupRecyclerView() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = TubeListAdapter(this, arrayListOf(), this)

        lines_recycler_view.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun updateView(lines: List<TubeStatus>) {
        lines.map {
            Toast.makeText(this, it.name, LENGTH_SHORT).show()
        }
        (viewAdapter as TubeListAdapter).update(lines)
    }

    override fun onTubeLineClicked() {
        Toast.makeText(this, "Clicked a tube line", LENGTH_SHORT).show()
    }
}
