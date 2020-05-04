package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.status_detail_row.view.*
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLineStatus

class TubeListDetailsAdapter(
    private val context: Context,
    private val dataSource: List<TubeLineStatus>
) : RecyclerView.Adapter<TubeListDetailsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.status_detail_row, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val status = dataSource[position]
        holder.statusHeader.text = status.severityDescription
        holder.statusDetail.text = status.reason
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var statusHeader: TextView = itemView.status_header
        var statusDetail: TextView = itemView.status_detail
    }
}