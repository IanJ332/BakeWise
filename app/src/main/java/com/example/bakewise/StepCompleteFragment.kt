package com.example.bakewise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bakewise.databinding.FragmentStepCompleteBinding

class StepCompleteFragment : Fragment() {

    private var _binding: FragmentStepCompleteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepCompleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipeId = arguments?.getInt("recipeId") ?: -1
        val stepIndex = arguments?.getInt("stepIndex") ?: -1

        val recipe = MOCK_RECIPES.find { it.id == recipeId }

        binding.bakingNavBar.navViewStepsButton.setOnClickListener {
            if (recipe != null) {
                val bundle = Bundle().apply {
                    putString("recipeName", recipe.name)
                    putParcelableArray("scheduleData", recipe.schedule.toTypedArray())
                }
                findNavController().navigate(R.id.action_stepCompleteFragment_to_scheduleFragment, bundle)
            }
        }

        binding.bakingNavBar.navBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.startEvaluationButton.setOnClickListener {
            val totalSteps = recipe?.schedule?.size ?: 0
            // Check if this is the last step. Indices are 0-based, so last index is size-1.
            if (stepIndex >= totalSteps - 1) {
                // It's the FINAL step. Skip feedback and go to the end.
                val bundle = Bundle().apply {
                    putInt("recipeId", recipeId)
                }
                findNavController().navigate(R.id.action_stepCompleteFragment_to_bakeCompleteFragment, bundle)
            } else {
                // Proceed to feedback/evaluation for the current step.
                val bundle = Bundle().apply {
                    putInt("recipeId", recipeId)
                    putInt("stepIndex", stepIndex)
                }
                findNavController().navigate(R.id.action_stepCompleteFragment_to_feedbackFragment, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}