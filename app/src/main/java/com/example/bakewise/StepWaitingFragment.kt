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
import androidx.appcompat.app.AlertDialog
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

        val stepIndex = arguments?.getInt("stepIndex") ?: -1

        binding.bakingNavBar.navViewStepsButton.setOnClickListener {
            findNavController().navigate(R.id.action_global_stepListDialogFragment)
        }

        binding.bakingNavBar.navBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        when (stepIndex) {
            0 -> binding.messageTextView.text = "Great! Your dough is now resting. This first rest period is about 4 hours. Set a reminder!"
            1 -> binding.messageTextView.text = "Perfect shape! Cover the dough and place it in the refrigerator for its long cold proof. This will be 8-12 hours. Set a reminder for tomorrow!"
            2 -> binding.messageTextView.text = "Your bread is in the oven! This will take about 45 minutes total. The smell is going to be amazing. Set a final timer!"
        }

        binding.setRemindersButton.setOnClickListener {
            askNotificationPermission()
        }

        binding.simulateTimesUpButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Are you sure?")
                .setMessage("This will skip the waiting period. Are you sure you want to proceed?")
                .setPositiveButton("Yes") { _, _ ->
                    val bundle = Bundle().apply {
                        putInt("stepIndex", stepIndex)
                    }
                    findNavController().navigate(R.id.action_stepWaitingFragment_to_stepCompleteFragment, bundle)
                }
                .setNegativeButton("No", null)
                .show()
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