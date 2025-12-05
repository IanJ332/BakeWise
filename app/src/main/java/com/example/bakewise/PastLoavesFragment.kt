package com.example.bakewise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bakewise.databinding.FragmentPastLoavesBinding

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

        val pastLoaves = PastLoavesRepository.loaves

        if (pastLoaves.isEmpty()) {
            binding.emptyStateText.visibility = View.VISIBLE
            binding.pastLoavesRecyclerView.visibility = View.GONE
        } else {
            binding.emptyStateText.visibility = View.GONE
            binding.pastLoavesRecyclerView.visibility = View.VISIBLE
            binding.pastLoavesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.pastLoavesRecyclerView.adapter = PastLoavesAdapter(pastLoaves) { loaf ->
                val bundle = Bundle().apply {
                    putParcelable("loaf", loaf)
                }
                findNavController().navigate(R.id.action_pastLoavesFragment_to_loafDetailsFragment, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}