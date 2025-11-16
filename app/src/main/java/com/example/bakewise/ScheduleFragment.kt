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
        val scheduleData = arguments?.getParcelableArray("scheduleData")?.map { it as BakeStep } ?: emptyList()
        val scheduleName = arguments?.getString("scheduleName")
        val scheduleTimes = arguments?.getStringArray("scheduleTimes")

        binding.recipeNameTextView.text = recipeName

        scheduleData.forEachIndexed { index, step ->
            val textView = TextView(requireContext()).apply {
                val recipe = MOCK_RECIPES.find { it.name == recipeName }
                // Display the step name and the calculated time
                val timeString = scheduleTimes?.get(index) ?: ""
                text = "${step.stepName} - $timeString"
                textSize = 16f
                setOnClickListener {
                    recipe?.let {
                        val stepIndex = it.schedule.indexOf(step)
                        if (stepIndex != -1) {
                            val bundle = Bundle().apply {
                                putInt("recipeId", it.id)
                                putInt("stepIndex", stepIndex)
                            }
                            findNavController().navigate(R.id.action_scheduleFragment_to_recipeStepFragment, bundle)
                        }
                    }
                }
            }
            binding.scheduleStepsLayout.addView(textView)
        }

        if (scheduleName != null) {
            // Viewing a saved schedule
            binding.scheduleNameEditText.setText(scheduleName)
            binding.scheduleNameEditText.isEnabled = false
            binding.saveScheduleButton.isVisible = false
        } else {
            // Creating a new schedule
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val defaultScheduleName = "$recipeName - ${sdf.format(Date())}"
            binding.scheduleNameEditText.setText(defaultScheduleName)

            binding.saveScheduleButton.setOnClickListener {
                val newScheduleName = binding.scheduleNameEditText.text.toString()
                if (newScheduleName.isNotBlank()) {
                    val newSchedule = SavedSchedule(newScheduleName, recipeName, scheduleData)
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