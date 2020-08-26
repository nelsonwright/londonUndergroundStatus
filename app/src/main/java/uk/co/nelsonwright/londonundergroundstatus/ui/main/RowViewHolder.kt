package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_view_row.view.*
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.models.TubeLineColours

class RowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindView(tubeLine: TubeLine, listener: TubeListClickListener, context: Context) {
        itemView.tube_name.text = tubeLine.name
        val tube = TubeLineColours.values().firstOrNull {
            it.id == tubeLine.id
        }

        tube?.let {
            itemView.tube_name.setBackgroundColor(Color.parseColor(it.backgroundColour))
            itemView.tube_name.setTextColor(
                (if (it.whiteForegroundColour) {
                    context.getColor(R.color.colorWhite)
                } else
                    context.getColor(R.color.colorBlack))
            )
        }

        itemView.tube_status_severity.text = getLineSeverityText(tubeLine)


        itemView.tube_status_severity.setTextColor(
            if (tubeLine.notGoodService()) {
                context.getColor(R.color.darkRed)
            } else {
                context.getColor(R.color.colorBlack)
            }
        )

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