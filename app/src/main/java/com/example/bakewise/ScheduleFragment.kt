package com.example.bakewise

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bakewise.databinding.FragmentScheduleBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
             // Check exact alarm permission next
             checkExactAlarmPermission()
        } else {
            Toast.makeText(requireContext(), "Notification permission denied. Reminders won't work.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipeName = arguments?.getString("recipeName") ?: ""
        val scheduleName = arguments?.getString("scheduleName")
        val isBakeNowPreview = arguments?.getBoolean("isBakeNowPreview") ?: false
        val recipeId = arguments?.getInt("recipeId") ?: -1
        
        val scheduleItemsArray = arguments?.getParcelableArray("scheduleItems")
        val scheduleItems = scheduleItemsArray?.map { it as ScheduleItem }

        // Determine the list of steps to show. 
        // If we have scheduleItems (from Plan), map them to BakeSteps.
        // If not, look for the "scheduleData" argument (from Explore/Saved/BakeNow).
        val scheduleData: List<BakeStep> = if (scheduleItems != null) {
            scheduleItems.map { it.bakeStep }
        } else {
            arguments?.getParcelableArray("scheduleData")?.map { it as BakeStep } ?: emptyList()
        }

        // Find the recipe ID if possible, to pass to notification scheduler
        val recipe = MOCK_RECIPES.find { it.name == recipeName }
        val recipeIdForScheduler = recipe?.id ?: -1

        binding.recipeNameTextView.text = recipeName

        scheduleData.forEachIndexed { index, step ->
            val textView = TextView(requireContext()).apply {
                
                val timeString = scheduleItems?.getOrNull(index)?.let { 
                    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(it.whenMillis)) 
                }
                
                text = if (!timeString.isNullOrEmpty()) "${step.stepName} - $timeString" else step.stepName
                textSize = 16f
                setOnClickListener {
                    recipe?.let {
                        val stepIndex = it.schedule.indexOf(step)
                        if (stepIndex != -1) {
                            val bundle = Bundle().apply {
                                putInt("recipeId", it.id)
                                putInt("stepIndex", stepIndex)
                                putBoolean("isViewingOnly", true)
                            }
                            findNavController().navigate(R.id.action_scheduleFragment_to_recipeStepFragment, bundle)
                        }
                    }
                }
            }
            binding.scheduleStepsLayout.addView(textView)
        }

        val isViewingSchedule = scheduleName != null
        // We are exploring if we don't have specific times calculated AND we aren't viewing a saved schedule
        val isExploring = scheduleItems == null && !isBakeNowPreview

        if (isViewingSchedule) {
            // Viewing a saved schedule
            binding.scheduleNameEditText.isVisible = false
            binding.saveScheduleButton.isVisible = false
            binding.recipeNameTextView.text = scheduleName
        } else if (isBakeNowPreview) {
            // Previewing before Bake Now
            binding.scheduleNameEditText.isVisible = false
            binding.saveScheduleButton.isVisible = false
            binding.startBakingButton.isVisible = true
            
            binding.startBakingButton.setOnClickListener {
                if (recipeId != -1) {
                    val bundle = Bundle().apply {
                        putInt("recipeId", recipeId)
                        putInt("stepIndex", 0)
                    }
                    findNavController().navigate(R.id.action_scheduleFragment_to_recipeStepFragment, bundle)
                } else {
                    Toast.makeText(requireContext(), "Error: Recipe ID not found", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (isExploring) {
            // Exploring recipes (no times)
            binding.scheduleNameEditText.isVisible = false
            binding.saveScheduleButton.isVisible = false
        } else {
            // Creating a new schedule (we have times, but not a saved name yet)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val defaultScheduleName = "$recipeName - ${sdf.format(Date())}"
            binding.scheduleNameEditText.setText(defaultScheduleName)

            binding.saveScheduleButton.setOnClickListener {
                val newScheduleName = binding.scheduleNameEditText.text.toString()
                if (newScheduleName.isNotBlank()) {
                    val newSchedule = SavedSchedule(newScheduleName, recipeName, scheduleItems!!)
                    ScheduleRepository.savedSchedules.add(newSchedule)
                    
                    // Set the name to pending variable so we can use it in permission callback
                    pendingScheduleName = newScheduleName
                    
                    // Initiate permission check and scheduling
                    checkNotificationPermission(scheduleItems)
                } else {
                    Toast.makeText(requireContext(), "Please enter a name for the schedule", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private var pendingScheduleItems: List<ScheduleItem>? = null
    private var pendingScheduleName: String? = null

    private fun checkNotificationPermission(items: List<ScheduleItem>) {
        pendingScheduleItems = items
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                checkExactAlarmPermission()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            checkExactAlarmPermission()
        }
    }

    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(requireContext(), "Please allow setting exact alarms for notifications", Toast.LENGTH_LONG).show()
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                // We can't easily callback from settings here without lifecycle complexity, 
                // so for this prototype we just ask. In a real app, use onResume to check again.
                return
            }
        }
        
        // If we are here, we have permissions
        pendingScheduleItems?.let {
            val recipeName = arguments?.getString("recipeName") ?: ""
            val recipe = MOCK_RECIPES.find { r -> r.name == recipeName }
            val recipeId = recipe?.id ?: -1
            val scheduleName = pendingScheduleName ?: "BakeWise Schedule"

            NotificationScheduler.scheduleNotifications(requireContext(), it, recipeId, scheduleName)
            Toast.makeText(requireContext(), "Schedule saved and reminders set!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_global_homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}