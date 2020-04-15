package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_view_row.view.*
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.models.DARK_BLUE
import uk.co.nelsonwright.londonundergroundstatus.models.TubeLineColours
import uk.co.nelsonwright.londonundergroundstatus.models.WHITE

class RowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindView(tubeLine: TubeLine, listener: TubeListClickListener) {
        itemView.tube_name.text = tubeLine.name
        val tube = TubeLineColours.values().firstOrNull {
            it.id == tubeLine.id
        }

        tube?.let {
            itemView.tube_name.setBackgroundColor(Color.parseColor(it.backgroundColour))
            itemView.tube_name.setTextColor(Color.parseColor(if (it.whiteForegroundColour) WHITE else DARK_BLUE))
        }

        itemView.tube_status_severity.text = getLineSeverityText(tubeLine)

        itemView.tube_name.setOnClickListener {
            listener.onTubeLineClicked(tubeLine)
        }

        itemView.tube_status_severity.setOnClickListener {
            listener.onTubeLineClicked(tubeLine)
        }
    }

    private fun getLineSeverityText(tubeLine: TubeLine): String? {
        val distinctStatuses = tubeLine.lineStatuses?.map {
            it.severityDescription
        }?.distinct()

        return distinctStatuses?.joinToString(" / ")
    }
}