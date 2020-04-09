package com.example.tubestatus.ui.main

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.tubestatus.api.TubeLine
import com.example.tubestatus.models.TubeLineColours
import kotlinx.android.synthetic.main.recycler_view_row.view.*

class RowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindView(context: Context, tubeLine: TubeLine, listener: TubeListClickListener) {
        itemView.tube_name.text = tubeLine.name
        val tube = TubeLineColours.values().firstOrNull() {
            it.id == tubeLine.id
        }

        tube?.let {
            itemView.tube_name.setBackgroundColor(Color.parseColor(it.backgroundColour))
            itemView.tube_name.setTextColor(Color.parseColor(if (it.whiteForegroundColour == true) "#ffffff" else "#000000"))
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