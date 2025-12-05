package com.example.bakewise

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bakewise.databinding.FragmentStepWaitingBinding

class StepWaitingFragment : Fragment() {

    private var _binding: FragmentStepWaitingBinding? = null
    private val binding get() = _binding!!

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(requireContext(), "Reminders set!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepWaitingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipeId = arguments?.getInt("recipeId") ?: -1
        val stepIndex = arguments?.getInt("stepIndex") ?: -1

        val recipe = MOCK_RECIPES.find { it.id == recipeId }
        val step = recipe?.schedule?.getOrNull(stepIndex)

        if (step != null) {
            binding.messageTextView.text = "Now resting after: ${step.stepName}.\n\nCheck back when the timer is up to evaluate the results and move to the next step."
        } else {
            binding.messageTextView.text = "Resting..."
        }

        binding.bakingNavBar.navViewStepsButton.setOnClickListener {
            if (recipe != null) {
                val bundle = Bundle().apply {
                    putString("recipeName", recipe.name)
                    putParcelableArray("scheduleData", recipe.schedule.toTypedArray())
                }
                findNavController().navigate(R.id.action_stepWaitingFragment_to_scheduleFragment, bundle)
            }
        }

        binding.bakingNavBar.navBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.setRemindersButton.setOnClickListener {
            askNotificationPermission()
        }

        binding.continueButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("recipeId", recipeId)
                putInt("stepIndex", stepIndex)
            }
            findNavController().navigate(R.id.action_stepWaitingFragment_to_stepCompleteFragment, bundle)
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(requireContext(), "Reminders set!", Toast.LENGTH_SHORT).show()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}