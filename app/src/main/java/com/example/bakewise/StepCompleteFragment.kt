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
            if (stepIndex == 2) {
                // It's the FINAL step (Bake). Skip feedback and go to the end.
                val bundle = Bundle().apply {
                    putInt("recipeId", recipeId)
                }
                findNavController().navigate(R.id.action_stepCompleteFragment_to_bakeCompleteFragment, bundle)
            } else {
                // It's Step 0 or 1. Proceed to feedback.
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