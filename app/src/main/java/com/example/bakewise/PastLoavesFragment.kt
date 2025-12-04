package com.example.bakewise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bakewise.databinding.FragmentPastLoavesBinding
import com.example.bakewise.databinding.DialogLoafDetailsBinding

class PastLoavesFragment : Fragment() {

    private var _binding: FragmentPastLoavesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPastLoavesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loaves = PastLoavesRepository.loaves

        if (loaves.isEmpty()) {
            binding.emptyView.isVisible = true
            binding.pastLoavesRecyclerView.isVisible = false
        } else {
            binding.emptyView.isVisible = false
            binding.pastLoavesRecyclerView.isVisible = true
            
            binding.pastLoavesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.pastLoavesRecyclerView.adapter = PastLoavesAdapter(loaves) { loaf ->
                showLoafDetails(loaf)
            }
        }
    }

    private fun showLoafDetails(loaf: PastLoaf) {
        val dialogBinding = DialogLoafDetailsBinding.inflate(layoutInflater)
        
        dialogBinding.detailTitleTextView.text = "${loaf.recipeName} Notes"
        
        if (loaf.finalPhotoUri != null) {
            dialogBinding.detailLoafImage.isVisible = true
            dialogBinding.detailLoafImage.setImageURI(android.net.Uri.parse(loaf.finalPhotoUri))
        } else {
            dialogBinding.detailLoafImage.isVisible = false
        }
        
        if (loaf.feedback != null) {
            dialogBinding.detailFeedbackText.isVisible = true
            dialogBinding.detailFeedbackText.text = loaf.feedback
        } else {
            dialogBinding.detailFeedbackText.isVisible = false
        }

        dialogBinding.detailsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        dialogBinding.detailsRecyclerView.adapter = LoafDetailsAdapter(loaf.notes)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}