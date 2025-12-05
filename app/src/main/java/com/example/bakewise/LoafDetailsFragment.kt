package com.example.bakewise

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bakewise.databinding.FragmentLoafDetailsBinding
import java.text.SimpleDateFormat
import java.util.Locale

class LoafDetailsFragment : Fragment() {

    private var _binding: FragmentLoafDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoafDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loaf = arguments?.getParcelable<PastLoaf>("loaf")

        if (loaf != null) {
            binding.detailLoafName.text = loaf.recipeName
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            binding.detailDateBaked.text = "Baked on ${dateFormat.format(loaf.dateBaked)}"

            // Overall Conclusion
            if (!loaf.overallConclusion.isNullOrEmpty()) {
                binding.detailConclusionText.text = loaf.overallConclusion
                binding.detailConclusionText.isVisible = true
            } else {
                binding.detailConclusionText.text = "No overall conclusion available."
            }

            // Notes List
            binding.detailNotesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.detailNotesRecyclerView.adapter = LoafDetailsAdapter(loaf.notes) // Read-only

            // Final Photo
            if (loaf.finalPhotoUri != null) {
                binding.detailLoafImage.isVisible = true
                try {
                    binding.detailLoafImage.setImageURI(Uri.parse(loaf.finalPhotoUri))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                binding.detailLoafImage.isVisible = false
            }

            // Final Feedback
            if (!loaf.feedback.isNullOrEmpty()) {
                binding.detailFeedbackText.text = loaf.feedback
                binding.detailFeedbackText.isVisible = true
            } else {
                binding.detailFeedbackText.isVisible = false
            }
        }

        binding.closeDetailsButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
