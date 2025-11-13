package com.example.bakewise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bakewise.databinding.FragmentRecipeDetailBinding

class RecipeDetailFragment : Fragment() {

    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!

    private var recipeId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeId = arguments?.getInt("recipeId") ?: -1
        val recipe = MOCK_RECIPES.find { it.id == recipeId }

        recipe?.let {
            binding.recipeNameTextView.text = it.name
            val steps = it.schedule.mapIndexed { index, bakeStep -> "${index + 1}. ${bakeStep.stepName}" }.joinToString("\n")
            binding.recipeStepsTextView.text = steps
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}