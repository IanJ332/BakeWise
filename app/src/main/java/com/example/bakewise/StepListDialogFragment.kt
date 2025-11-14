package com.example.bakewise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.bakewise.databinding.DialogStepListBinding

class StepListDialogFragment : DialogFragment() {

    private var _binding: DialogStepListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogStepListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.jumpToStep1Button.setOnClickListener {
            findNavController().navigate(R.id.recipeStepFragment, Bundle().apply { putInt("stepIndex", 0) })
            dismiss()
        }

        binding.jumpToStep2Button.setOnClickListener {
            findNavController().navigate(R.id.recipeStepFragment, Bundle().apply { putInt("stepIndex", 1) })
            dismiss()
        }

        binding.jumpToStep3Button.setOnClickListener {
            findNavController().navigate(R.id.recipeStepFragment, Bundle().apply { putInt("stepIndex", 2) })
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}