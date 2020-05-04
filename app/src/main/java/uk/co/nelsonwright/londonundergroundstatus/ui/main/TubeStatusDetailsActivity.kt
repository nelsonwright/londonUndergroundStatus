package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.tube_status_details_activity.*
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLine

const val EXTRA_LINES = "EXTRA_LINES"
const val EXTRA_LINE_COLOUR = "EXTRA_LINE_COLOUR"

class TubeStatusDetailsActivity : AppCompatActivity() {
    private lateinit var tubeLine: TubeLine
    private lateinit var lineColour: String
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tube_status_details_activity)

        intent.extras?.let { bundle ->
            tubeLine = bundle.getSerializable(EXTRA_LINES) as TubeLine
            lineColour = bundle.getSerializable(EXTRA_LINE_COLOUR) as String
            title = tubeLine.name
        }

        val context = this

        tubeLine.lineStatuses?.let {
            details_recycler_view.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = TubeListDetailsAdapter(context, it)
            }
        }

        val colorDrawable = ColorDrawable(Color.parseColor(lineColour))
        supportActionBar?.setBackgroundDrawable(colorDrawable)
    }
}
