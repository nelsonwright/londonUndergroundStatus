package com.example.londonundergroundstatus.ui.main


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_view_header.view.*
import uk.co.nelsonwright.londonundergroundstatus.R
import java.text.DateFormat
import java.util.*

class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindView(listener: TubeListClickListener) {
        val simpleDateFormat = DateFormat.getDateTimeInstance()
        val refreshDate: String = simpleDateFormat.format(Date())
        itemView.refresh_date.text = itemView.context.getString(R.string.refresh_date, refreshDate)
    }
}