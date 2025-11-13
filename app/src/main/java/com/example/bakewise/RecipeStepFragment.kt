package com.example.bakewise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bakewise.databinding.FragmentRecipeStepBinding

class RecipeStepFragment : Fragment() {

    private var _binding: FragmentRecipeStepBinding? = null
    private val binding get() = _binding!!

    private var recipeId: Int = -1
    private var stepIndex: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeId = arguments?.getInt("recipeId") ?: -1
        stepIndex = arguments?.getInt("stepIndex") ?: -1

        val recipe = MOCK_RECIPES.find { it.id == recipeId }
        val step = recipe?.schedule?.getOrNull(stepIndex)

        if (recipe != null && step != null) {
            binding.stepTitleTextView.text = "Step ${stepIndex + 1}: ${step.stepName}"
        }

        binding.doneButton.setOnClickListener {
            findNavController().navigate(R.id.action_recipeStepFragment_to_stepWaitingFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}