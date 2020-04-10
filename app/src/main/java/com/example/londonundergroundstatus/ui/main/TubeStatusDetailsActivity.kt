package com.example.londonundergroundstatus.ui.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.londonundergroundstatus.api.TubeLine
import com.londonundergroundstatus.R
import kotlinx.android.synthetic.main.tube_status_details_activity.*

const val EXTRA_LINES = "EXTRA_LINES"
const val EXTRA_LINE_COLOUR = "EXTRA_LINE_COLOUR"

class TubeStatusDetailsActivity : AppCompatActivity() {
    private lateinit var tubeLine: TubeLine
    private lateinit var lineColour: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tube_status_details_activity)

        intent.extras?.let { bundle ->
            tubeLine = bundle.getSerializable(EXTRA_LINES) as TubeLine
            lineColour = bundle.getSerializable(EXTRA_LINE_COLOUR) as String
            title = tubeLine.name
        }

        tubeLine.lineStatuses?.let {
            val adapter = TubeListDetailsAdapter(this, it)
            statusList.adapter = adapter
        }

        val colorDrawable = ColorDrawable(Color.parseColor(lineColour))
        supportActionBar?.setBackgroundDrawable(colorDrawable)
    }
}
