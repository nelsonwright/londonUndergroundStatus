package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.tube_status_details_activity.*
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLineStatus

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

        val context = this
        val compactedList: MutableList<TubeLineStatus> = mutableListOf()

        tubeLine.lineStatuses?.let { tubeLineStatusList ->
            compactSameReasonsUnderOneAmalgamatedStatus(tubeLineStatusList, compactedList)

            details_recycler_view.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = TubeListDetailsAdapter(context, compactedList)
            }
        }

        val colorDrawable = ColorDrawable(Color.parseColor(lineColour))
        supportActionBar?.setBackgroundDrawable(colorDrawable)

        tube_train_image.visibility = if (tubeLine.notGoodService()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun compactSameReasonsUnderOneAmalgamatedStatus(
        tubeLineStatusList: List<TubeLineStatus>,
        compactedList: MutableList<TubeLineStatus>
    ) {
        var severityDescriptionCompacted = ""

        tubeLineStatusList.forEach { tls ->
            if (compactedList.any { cl -> cl.reason == tls.reason }) {
                // we've already got the reason in the list i.e. the body text, so just check if we need to add the
                // severity description to the existing header...

                if (severityDescriptionCompacted != tls.severityDescription) {
                    severityDescriptionCompacted = "$severityDescriptionCompacted / ${tls.severityDescription}"
                }
            } else {
                compactedList.add(
                    TubeLineStatus(
                        statusSeverity = tls.statusSeverity,
                        severityDescription = tls.severityDescription,
                        reason = tls.reason
                    )
                )
                severityDescriptionCompacted = tls.severityDescription ?: ""
            }
        }
    }
}
