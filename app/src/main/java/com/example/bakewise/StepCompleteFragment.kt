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

        val stepIndex = arguments?.getInt("stepIndex") ?: -1

        binding.startEvaluationButton.setOnClickListener {
            if (stepIndex == 2) {
                // It's the FINAL step (Bake). Skip feedback and go to the end.
                findNavController().navigate(R.id.action_stepCompleteFragment_to_bakeCompleteFragment)
            } else {
                // It's Step 0 or 1. Proceed to feedback.
                val bundle = Bundle().apply {
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