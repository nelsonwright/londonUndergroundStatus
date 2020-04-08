package com.example.tubestatus.ui.main

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.tubestatus.api.TubeStatus
import com.example.tubestatus.models.TubeLineColours
import kotlinx.android.synthetic.main.recycler_view_row.view.*

class RowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindView(context: Context, tubeStatus: TubeStatus, listener: TubeListClickListener) {
        itemView.tube_name.text = tubeStatus.name
        val tube = TubeLineColours.values().firstOrNull() {
            it.id == tubeStatus.id
        }

        itemView.tube_name.setBackgroundColor(Color.parseColor(tube?.backgroundColour))
        itemView.tube_name.setTextColor(Color.parseColor(if (tube?.whiteForegroundColour == true) "#ffffff" else "#000000"))

        itemView.tube_status_severity.text = getLineSeverityText(tubeStatus)

        itemView.tube_name.setOnClickListener {
            listener.onTubeLineClicked(itemView)
        }

        itemView.tube_status_severity.setOnClickListener {
            listener.onTubeLineClicked(itemView)
        }
    }

    private fun getLineSeverityText(tubeStatus: TubeStatus): String? {
        val distinctStatuses = tubeStatus.lineStatuses?.map {
            it.severityDescription
        }?.distinct()

        return distinctStatuses?.joinToString(" / ")
    }
}