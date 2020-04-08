package com.example.tubestatus.ui.main

import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.tubestatus.R
import com.example.tubestatus.api.TubeStatus

class TubeStatusActivity : AppCompatActivity() {
    private val viewModel: TubeStatusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tube_status_activity)
        viewModel.getTubeLines().observe(this, Observer { lines ->
            updateView(lines)
        })
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun updateView(lines: List<TubeStatus>) {
        lines.map {
            Toast.makeText(this, it.name, LENGTH_SHORT).show()
        }
    }
}
