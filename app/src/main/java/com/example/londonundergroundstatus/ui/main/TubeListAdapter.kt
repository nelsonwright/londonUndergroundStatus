package com.example.londonundergroundstatus.ui.main

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.londonundergroundstatus.api.TubeLine
import com.londonundergroundstatus.R

enum class RowType {
    HEADER,
    BODY,
    FOOTER
}

interface TubeListClickListener {
    fun onTubeLineClicked(tubeLine: TubeLine)
}

class TubeListAdapter(
    private val context: Context, private var tubeDetailsList: List<TubeLine>,
    private val listener: TubeListClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {

    fun update(tubeDetails: List<TubeLine>) {
        tubeDetailsList = tubeDetails
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            RowType.HEADER.ordinal -> HeaderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_view_header, parent, false)
            )

            RowType.FOOTER.ordinal -> FooterViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_view_footer, parent, false)
            )

            else -> RowViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_view_row, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            RowType.HEADER.ordinal -> {
                holder as HeaderViewHolder
                holder.bindView(listener = listener)
            }
            RowType.FOOTER.ordinal -> {
                holder as FooterViewHolder
                holder.bindView(listener = listener)
            }
            else -> {
                holder as RowViewHolder
                holder.bindView(
                    context = context,
                    tubeLine = tubeDetailsList[position - 1],
                    listener = listener
                )
            }
        }
    }

    // add 1 to the size in item count, because we are going to add header and footer in list
    override fun getItemCount(): Int = tubeDetailsList.size + 2

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> RowType.HEADER.ordinal
            tubeDetailsList.size + 1 -> RowType.FOOTER.ordinal
            else -> RowType.BODY.ordinal
        }
    }

    override fun onClick(v: View?) {
        Log.d("TubeListAdapter", "Clicked a view: ${v.toString()}")
    }
}