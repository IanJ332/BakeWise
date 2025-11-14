package com.example.bakewise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bakewise.databinding.FragmentBakeCompleteBinding

class BakeCompleteFragment : Fragment() {

    private var _binding: FragmentBakeCompleteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBakeCompleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipeId = arguments?.getInt("recipeId") ?: -1
        val recipe = MOCK_RECIPES.find { it.id == recipeId }

        if (recipe != null) {
            binding.summaryText.text = "You have successfully baked your ${recipe.name}!"
        }

        binding.returnHomeButton.setOnClickListener {
            findNavController().navigate(R.id.action_bakeCompleteFragment_to_homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}