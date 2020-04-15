package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLineStatus

class TubeListDetailsAdapter(
    context: Context,
    private val dataSource: List<TubeLineStatus>
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.status_detail_row, parent, false)

        val statusHeader = rowView.findViewById(R.id.status_header) as TextView
        val status = getItem(position) as TubeLineStatus
        statusHeader.text = status.severityDescription

        val statusDetail = rowView.findViewById(R.id.status_detail) as TextView
        statusDetail.text = status.reason

        return rowView
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }
}