package uk.co.nelsonwright.londonundergroundstatus.ui.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_details.*
import uk.co.nelsonwright.londonundergroundstatus.R
import uk.co.nelsonwright.londonundergroundstatus.api.TubeLineStatus


class TubeDetailsFragment : Fragment() {
    private val args: TubeDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionBar()
        setupDetailsList()

        tube_train_image.visibility = if (args.tubeLine.notGoodService()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun setupActionBar() {
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.title = args.tubeLine.name

        val colorDrawable = ColorDrawable(Color.parseColor(args.lineColour))
        supportActionBar?.setBackgroundDrawable(colorDrawable)
    }

    private fun setupDetailsList() {
        val compactedList: MutableList<TubeLineStatus> = mutableListOf()

        args.tubeLine.lineStatuses?.let { tubeLineStatusList ->
            compactSameReasonsUnderOneAmalgamatedStatus(tubeLineStatusList, compactedList)

            details_recycler_view.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = TubeListDetailsAdapter(requireContext(), compactedList)
            }
        }
    }

    private fun compactSameReasonsUnderOneAmalgamatedStatus(
        tubeLineStatusList: List<TubeLineStatus>,
        compactedList: MutableList<TubeLineStatus>
    ) {
        var severityDescriptionCompacted = ""

        tubeLineStatusList.forEach { tls ->
            if (compactedList.any { cl -> cl.reason == tls.reason }) {
                // we've already got the reason in the list i.e. the body text, so just check if we need to add the
                // severity description to the existing header...

                if (severityDescriptionCompacted != tls.severityDescription) {
                    severityDescriptionCompacted = "$severityDescriptionCompacted / ${tls.severityDescription}"
                }
            } else {
                compactedList.add(
                    TubeLineStatus(
                        statusSeverity = tls.statusSeverity,
                        severityDescription = tls.severityDescription,
                        reason = tls.reason
                    )
                )
                severityDescriptionCompacted = tls.severityDescription ?: ""
            }
        }
    }
}