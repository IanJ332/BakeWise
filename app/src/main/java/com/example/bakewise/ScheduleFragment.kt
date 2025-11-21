package com.example.bakewise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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
        
        // Try to get the schedule items (calculated times) from Plan a Loaf flow
        // We use getParcelableArray because that's what we sent
        val scheduleItemsArray = arguments?.getParcelableArray("scheduleItems")
        val scheduleItems = scheduleItemsArray?.map { it as ScheduleItem }

        // Determine the list of steps to show. 
        // If we have scheduleItems (from Plan), map them to BakeSteps.
        // If not, look for the "scheduleData" argument (from Explore/Saved).
        val scheduleData: List<BakeStep> = if (scheduleItems != null) {
            scheduleItems.map { it.bakeStep }
        } else {
            arguments?.getParcelableArray("scheduleData")?.map { it as BakeStep } ?: emptyList()
        }

        binding.recipeNameTextView.text = recipeName

        scheduleData.forEachIndexed { index, step ->
            val textView = TextView(requireContext()).apply {
                val recipe = MOCK_RECIPES.find { it.name == recipeName }
                
                // Display the step name. If we have a calculated time, append it.
                // We only have times if scheduleItems is not null.
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
        // Wait, viewing a saved schedule DOES have times (scheduleItems is not null).
        // So isExploring is really just "scheduleItems is null".
        val isExploring = scheduleItems == null

        if (isViewingSchedule) {
             // Viewing a saved schedule
            binding.scheduleNameEditText.isVisible = false
            binding.saveScheduleButton.isVisible = false
            binding.recipeNameTextView.text = scheduleName
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
                    // Use scheduleItems!! here because we are in the "Creating" block, so it must exist
                    val newSchedule = SavedSchedule(newScheduleName, recipeName, scheduleItems!!)
                    ScheduleRepository.savedSchedules.add(newSchedule)
                    Toast.makeText(requireContext(), "Schedule saved!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_global_homeFragment)
                } else {
                    Toast.makeText(requireContext(), "Please enter a name for the schedule", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}