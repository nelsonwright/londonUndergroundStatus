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
import uk.co.nelsonwright.londonundergroundstatus.databinding.FragmentDetailsBinding
import uk.co.nelsonwright.londonundergroundstatus.models.TubeLineStatus


class TubeDetailsFragment : Fragment() {
    private val args: TubeDetailsFragmentArgs by navArgs()
    private var _binding: FragmentDetailsBinding? = null

    // This property is only valid between onCreateView and onDestroyView...
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionBar()
        setupDetailsList()

        binding.tubeTrainImage.visibility = if (args.tubeLine.notGoodService()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

            binding.detailsRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = TubeListDetailsAdapter(compactedList)
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