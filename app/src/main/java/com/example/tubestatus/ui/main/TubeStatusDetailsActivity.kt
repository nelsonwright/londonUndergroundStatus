package com.example.tubestatus.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tubestatus.R
import com.example.tubestatus.api.TubeLine
import kotlinx.android.synthetic.main.tube_status_details_activity.*

class TubeStatusDetailsActivity : AppCompatActivity() {
    private lateinit var tubeLine: TubeLine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tube_status_details_activity)

        intent.extras?.let {
            tubeLine = it.getSerializable("LINES") as TubeLine
            setTitle(tubeLine.name)
        }

        tubeLine.lineStatuses?.let {
            val adapter = TubeListDetailsAdapter(this, it)
            statusList.adapter = adapter
        }
    }
}
