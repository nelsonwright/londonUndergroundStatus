package com.example.tubestatus.ui.main

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.tubestatus.api.TubeStatus
import kotlinx.android.synthetic.main.recycler_view_row.view.*

class RowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindView(context: Context, details: TubeStatus, listener: TubeListClickListener) {
        itemView.tube_name.text = details.name

//        itemView.recycler_language_button.setOnClickListener {
//            listener.onLanguageButtonClick(details, itemView.recycler_language_button)
//        }
    }
}