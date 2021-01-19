package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.databinding.StatusDetailRowBinding
import uk.co.nelsonwright.londonundergroundstatus.models.TubeLineStatus
import uk.co.nelsonwright.londonundergroundstatus.shared.GOOD_SERVICE

class TubeListDetailsAdapter(private val dataSource: List<TubeLineStatus>) :
    RecyclerView.Adapter<TubeListDetailsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = StatusDetailRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tubeLineStatus = dataSource[position]
        holder.bind(tubeLineStatus)
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    class ViewHolder(private val itemBinding: StatusDetailRowBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(tubeLineStatus: TubeLineStatus) {
            itemBinding.statusHeader.text = tubeLineStatus.severityDescription
            itemBinding.statusDetail.text = tubeLineStatus.reason

            if (tubeLineStatus.statusSeverity != GOOD_SERVICE) {
                itemBinding.statusHeader.setTextColor(itemBinding.root.context.getColor(R.color.darkRed))
            }
        }
    }
}