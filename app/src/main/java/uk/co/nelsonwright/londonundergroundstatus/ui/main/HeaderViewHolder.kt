package uk.co.nelsonwright.londonundergroundstatus.ui.main


import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_view_header.view.*
import uk.co.nelsonwright.londonundergroundstatus.R
import java.text.SimpleDateFormat
import java.util.*

class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), AdapterView.OnItemSelectedListener {
    private lateinit var listener: TubeListClickListener

    fun bindView(
        listener: TubeListClickListener, context: Context
    ) {
        this.listener = listener
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateTimeInstance()
        val formattedDate = formatter.format(date)

        itemView.refresh_date.text = itemView.context.getString(R.string.refresh_date, formattedDate)

        ArrayAdapter.createFromResource(
            context,
            R.array.status_date_array,
            R.layout.status_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            itemView.status_date_spinner.adapter = adapter
        }
        itemView.status_date_spinner.onItemSelectedListener = this
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // do nothing
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        listener.onStatusDateChanged(position)
    }
}