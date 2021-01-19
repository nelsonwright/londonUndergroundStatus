package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.databinding.RecyclerViewRowBinding
import uk.co.nelsonwright.londonundergroundstatus.models.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.ui.main.RowType.BODY
import uk.co.nelsonwright.londonundergroundstatus.ui.main.RowType.FOOTER

enum class RowType {
    BODY,
    FOOTER
}

interface TubeListClickListener {
    fun onTubeLineClicked(tubeLine: TubeLine)
}

class TubeListAdapter(
    private var tubeDetailsList: List<TubeLine>,
    private val listener: TubeListClickListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {

    var itemBinding: RecyclerViewRowBinding? = null
    fun update(tubeDetails: List<TubeLine>) {
        tubeDetailsList = tubeDetails
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        itemBinding = RecyclerViewRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return when (viewType) {
            FOOTER.ordinal -> FooterViewHolder(
                inflater.inflate(R.layout.recycler_view_footer, parent, false)
            )

            else -> {
                itemBinding?.let { RowViewHolder(it) }
                    ?: throw IllegalStateException("Something went wrong with the recycler view overview")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            FOOTER.ordinal -> {
                holder as FooterViewHolder
                holder.bindView()
            }
            else -> {
                holder as RowViewHolder
                if (tubeDetailsList.isNotEmpty()) {
                    itemBinding?.let {
                        holder.bindView(
                            tubeLine = tubeDetailsList[position],
                            listener = listener
                        )
                    }

                }
            }
        }
    }

    // add 1 to the size in item count, because we are going to add the footer in list
    override fun getItemCount(): Int = tubeDetailsList.size + 1

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            tubeDetailsList.size -> FOOTER.ordinal
            else -> BODY.ordinal
        }
    }

    override fun onClick(v: View?) {
        // do nothing
    }
}
