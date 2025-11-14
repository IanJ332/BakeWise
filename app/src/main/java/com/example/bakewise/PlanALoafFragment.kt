package com.example.bakewise

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bakewise.databinding.FragmentPlanALoafBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PlanALoafFragment : Fragment() {

    private var _binding: FragmentPlanALoafBinding? = null
    private val binding get() = _binding!!

    private var selectedRecipe: Recipe? = null
    private val readyCal: Calendar = Calendar.getInstance()
    private val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
    private var lastSchedule: List<ScheduleItem>? = null

    data class ScheduleItem(val when_: Date, val label: String)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanALoafBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Int>("selectedRecipeId")?.observe(viewLifecycleOwner) { recipeId ->
            selectedRecipe = MOCK_RECIPES.find { it.id == recipeId }
            binding.pickRecipeButton.text = "Recipe: ${selectedRecipe?.name}"
            binding.selectedRecipeTextView.visibility = View.GONE
            binding.whenToStartTextView.text = ""
            binding.letsScheduleButton.isVisible = false
            lastSchedule = null
        }

        binding.pickRecipeButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("source", "PlanALoaf")
            }
            findNavController().navigate(R.id.action_planALoafFragment_to_newPickRecipeFragment, bundle)
        }

        binding.pickReadyTimeButton.setOnClickListener {
            if (selectedRecipe == null) {
                Toast.makeText(requireContext(), "Please select a recipe first", Toast.LENGTH_SHORT).show()
            } else {
                showDateThenTime()
            }
        }

        binding.clearButton.setOnClickListener {
            selectedRecipe = null
            binding.pickRecipeButton.text = "Pick recipe"
            binding.selectedRecipeTextView.visibility = View.VISIBLE
            readyCal.timeInMillis = System.currentTimeMillis()
            binding.readyTimeEditText.setText("")
            binding.whenToStartTextView.text = ""
            binding.letsScheduleButton.isVisible = false
            lastSchedule = null
            Toast.makeText(requireContext(), "Cleared", Toast.LENGTH_SHORT).show()
        }

        binding.calculateScheduleButton.setOnClickListener {
            if (selectedRecipe == null) {
                Toast.makeText(requireContext(), "Pick a recipe first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (binding.readyTimeEditText.text.toString().trim().isEmpty()) {
                Toast.makeText(requireContext(), "Pick a ready time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val readyDate = Date(readyCal.timeInMillis)
            val calculatedSchedule = selectedRecipe!!.schedule.map {
                val stepTime = Calendar.getInstance()
                stepTime.time = readyDate
                stepTime.add(Calendar.HOUR, -it.hoursBeforeReady)
                ScheduleItem(stepTime.time, it.stepName)
            }.sortedBy { it.when_ }

            lastSchedule = calculatedSchedule

            if (lastSchedule!!.isNotEmpty()) {
                val first = lastSchedule!!.first()
                binding.whenToStartTextView.text = "Start: ${fmt.format(first.when_)} — ${first.label}"
                binding.letsScheduleButton.isVisible = true
            } else {
                binding.whenToStartTextView.text = ""
                binding.letsScheduleButton.isVisible = false
            }
        }

        binding.letsScheduleButton.setOnClickListener {
            if (selectedRecipe == null || lastSchedule == null) return@setOnClickListener

            val scheduleSteps = lastSchedule!!.map { "${it.label}: ${fmt.format(it.when_)}" }.toTypedArray()

            val bundle = Bundle().apply {
                putString("recipeName", selectedRecipe!!.name)
                putStringArray("scheduleData", scheduleSteps)
            }
            findNavController().navigate(R.id.action_planALoafFragment_to_scheduleFragment, bundle)
        }

        binding.readyTimeEditText.setText("")
    }

    private fun showDateThenTime() {
        val totalHours = selectedRecipe!!.schedule.maxOfOrNull { it.hoursBeforeReady } ?: 0
        val minReadyCal = Calendar.getInstance()
        minReadyCal.add(Calendar.HOUR_OF_DAY, totalHours)

        val currentCalendar = Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                readyCal.set(Calendar.YEAR, year)
                readyCal.set(Calendar.MONTH, month)
                readyCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                showTimePicker(minReadyCal)
            },
            currentCalendar.get(Calendar.YEAR),
            currentCalendar.get(Calendar.MONTH),
            currentCalendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = minReadyCal.timeInMillis
            show()
        }
    }

    private fun showTimePicker(minReadyCal: Calendar) {
        val hour = readyCal.get(Calendar.HOUR_OF_DAY)
        val min = readyCal.get(Calendar.MINUTE)
        TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
            readyCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            readyCal.set(Calendar.MINUTE, minute)
            readyCal.set(Calendar.SECOND, 0)
            readyCal.set(Calendar.MILLISECOND, 0)

            if (readyCal.timeInMillis < minReadyCal.timeInMillis) {
                Toast.makeText(requireContext(), "Selected time is not feasible for this recipe. Please pick a later time.", Toast.LENGTH_LONG).show()
                binding.readyTimeEditText.setText("")
            } else {
                binding.readyTimeEditText.setText(fmt.format(readyCal.time))
            }
        }, hour, min, false).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}