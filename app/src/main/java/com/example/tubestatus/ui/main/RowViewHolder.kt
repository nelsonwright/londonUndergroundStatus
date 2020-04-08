package com.example.tubestatus.ui.main

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.tubestatus.api.TubeStatus
import kotlinx.android.synthetic.main.recycler_view_row.view.*

class RowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindView(context: Context, tubeStatus: TubeStatus, listener: TubeListClickListener) {
        itemView.tube_name.text = tubeStatus.name
        itemView.tube_status_severity.text = getLineSeverityText(tubeStatus)

        itemView.tube_name.setOnClickListener {
            listener.onTubeLineClicked(it)
        }
    }

    private fun getLineSeverityText(tubeStatus: TubeStatus): String? {
        val distinctStatuses = mutableListOf<String>()
        tubeStatus.lineStatuses?.forEach {
            if (!distinctStatuses.contains(it.severityDescription.toString())) {
                distinctStatuses.add(it.severityDescription.toString())
            }
        }
        return distinctStatuses.joinToString(" / ")
    }
}