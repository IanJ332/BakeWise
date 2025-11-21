package com.example.bakewise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bakewise.databinding.FragmentFeedbackBinding

class FeedbackFragment : Fragment() {

    private var _binding: FragmentFeedbackBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedbackBinding.inflate(inflater, container, false)
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
                findNavController().navigate(R.id.action_feedbackFragment_to_scheduleFragment, bundle)
            }
        }

        binding.bakingNavBar.navBackButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("recipeId", recipeId)
                putInt("stepIndex", stepIndex)
            }
            findNavController().navigate(R.id.action_feedbackFragment_to_stepWaitingFragment, bundle)
        }

        when (stepIndex) {
            0 -> {
                binding.notReadyDescription.text = "Dough still looks dense, hasn't risen much, and feels tight."
                binding.readyDescription.text = "Dough has risen, feels soft, and is full of air. You should see some bubbles on the surface."
            }
            1 -> {
                binding.notReadyTitle.text = "Under-proofed"
                binding.readyTitle.text = "Ready"
                binding.notReadyDescription.text = "If you poke the dough, the hole springs back very quickly."
                binding.readyDescription.text = "If you poke the dough, the indentation slowly springs back, but not completely."
            }
        }

        binding.completeStepButton.setOnClickListener {
            val nextStepIndex = stepIndex + 1
            if (nextStepIndex < (recipe?.schedule?.size ?: 0)) {
                val bundle = Bundle().apply {
                    putInt("recipeId", recipeId)
                    putInt("stepIndex", nextStepIndex)
                }
                findNavController().navigate(R.id.action_feedbackFragment_to_recipeStepFragment, bundle)
            } else {
                val bundle = Bundle().apply {
                    putInt("recipeId", recipeId)
                }
                findNavController().navigate(R.id.action_feedbackFragment_to_bakeCompleteFragment, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}