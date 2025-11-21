package com.example.bakewise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bakewise.databinding.FragmentRecipeStepBinding

class RecipeStepFragment : Fragment() {

    private var _binding: FragmentRecipeStepBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipeId = arguments?.getInt("recipeId") ?: -1
        val stepIndex = arguments?.getInt("stepIndex") ?: -1
        val isViewingOnly = arguments?.getBoolean("isViewingOnly") ?: false

        val recipe = MOCK_RECIPES.find { it.id == recipeId }
        val step = recipe?.schedule?.getOrNull(stepIndex)

        if (step != null) {
            binding.stepTitleTextView.text = "Step ${stepIndex + 1}: ${step.stepName}"
            binding.instructionsTextView.text = step.description
        }

        // The back button should always be visible and functional.
        binding.bakingNavBar.navBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // Only show the other buttons if we are in an active bake.
        binding.doneButton.isVisible = !isViewingOnly
        binding.bakingNavBar.navViewStepsButton.isVisible = !isViewingOnly

        if (!isViewingOnly) {
            // This is an active bake, so set up the listeners for the other buttons.
            binding.doneButton.setOnClickListener {
                val bundle = Bundle().apply {
                    putInt("recipeId", recipeId)
                    putInt("stepIndex", stepIndex)
                }
                findNavController().navigate(R.id.action_recipeStepFragment_to_stepWaitingFragment, bundle)
            }

            binding.bakingNavBar.navViewStepsButton.setOnClickListener {
                if (recipe != null) {
                    val bundle = Bundle().apply {
                        putString("recipeName", recipe.name)
                        putParcelableArray("scheduleData", recipe.schedule.toTypedArray())
                    }
                    findNavController().navigate(R.id.action_recipeStepFragment_to_scheduleFragment, bundle)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}