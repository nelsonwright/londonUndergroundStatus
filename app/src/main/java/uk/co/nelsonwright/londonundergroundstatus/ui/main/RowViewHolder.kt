package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.databinding.RecyclerViewRowBinding
import uk.co.nelsonwright.londonundergroundstatus.models.TubeLine
import uk.co.nelsonwright.londonundergroundstatus.models.TubeLineColours

class RowViewHolder(private val itemBinding: RecyclerViewRowBinding) : RecyclerView.ViewHolder(itemBinding.root) {
    fun bindView(tubeLine: TubeLine, listener: TubeListClickListener) {
        itemBinding.tubeName.text = tubeLine.name
        val tube = TubeLineColours.values().firstOrNull {
            it.id == tubeLine.id
        }

        tube?.let {
            itemBinding.tubeName.apply {
                setBackgroundColor(Color.parseColor(it.backgroundColour))
                setTextColor(
                    when {
                        it.whiteForegroundColour -> context.getColor(R.color.colorWhite)
                        else -> context.getColor(R.color.colorBlack)
                    }
                )
                setOnClickListener {
                    listener.onTubeLineClicked(tubeLine)
                }
            }
        }

        itemBinding.tubeStatusSeverity.apply {
            text = getLineSeverityText(tubeLine)
            setTextColor(
                when {
                    tubeLine.notGoodService() -> context.getColor(R.color.darkRed)
                    else -> context.getColor(R.color.colorBlack)
                }
            )
            setOnClickListener {
                listener.onTubeLineClicked(tubeLine)
            }
        }
    }

    private fun getLineSeverityText(tubeLine: TubeLine): String? {
        val distinctStatuses = tubeLine.lineStatuses?.map {
            it.severityDescription
        }?.distinct()

        return distinctStatuses?.joinToString(" / ")
    }
}