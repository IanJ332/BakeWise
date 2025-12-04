package com.example.bakewise

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TimePicker
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
                ScheduleItem(stepTime.timeInMillis, it)
            }.sortedBy { it.whenMillis }

            lastSchedule = calculatedSchedule

            if (lastSchedule!!.isNotEmpty()) {
                val first = lastSchedule!!.first()
                binding.whenToStartTextView.text = "Start: ${fmt.format(Date(first.whenMillis))} — ${first.bakeStep.stepName}"
                binding.letsScheduleButton.isVisible = true
            } else {
                binding.whenToStartTextView.text = ""
                binding.letsScheduleButton.isVisible = false
            }
        }

        binding.letsScheduleButton.setOnClickListener {
            if (selectedRecipe == null || lastSchedule == null) return@setOnClickListener

            val bundle = Bundle().apply {
                putString("recipeName", selectedRecipe!!.name)
                putParcelableArray("scheduleItems", lastSchedule!!.toTypedArray())
            }
            findNavController().navigate(R.id.action_planALoafFragment_to_scheduleFragment, bundle)
        }

        binding.readyTimeEditText.setText("")
    }

    private fun showDateThenTime() {
        val totalHours = selectedRecipe!!.schedule.maxOfOrNull { it.hoursBeforeReady } ?: 0
        val minReadyCal = Calendar.getInstance()
        minReadyCal.add(Calendar.HOUR_OF_DAY, totalHours)

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                readyCal.set(Calendar.YEAR, year)
                readyCal.set(Calendar.MONTH, month)
                readyCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                showTimePicker(minReadyCal)
            },
            minReadyCal.get(Calendar.YEAR),
            minReadyCal.get(Calendar.MONTH),
            minReadyCal.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = minReadyCal.timeInMillis
            show()
        }
    }

    private fun showTimePicker(minReadyCal: Calendar) {
        val hour = minReadyCal.get(Calendar.HOUR_OF_DAY)
        val min = minReadyCal.get(Calendar.MINUTE)
        
        readyCal.set(Calendar.HOUR_OF_DAY, hour)
        readyCal.set(Calendar.MINUTE, min)
        readyCal.set(Calendar.SECOND, 0)
        readyCal.set(Calendar.MILLISECOND, 0)

        if (readyCal.timeInMillis < minReadyCal.timeInMillis) {
            readyCal.timeInMillis = minReadyCal.timeInMillis
        }
        
        // We add a "Subtitle" or Title to the dialog to show the Start Time dynamically.
        // Since TimePickerDialog doesn't natively support a dynamic subtitle easily without custom view,
        // we will update the Title with the projected Start Time as the user scrolls.
        
        val tpd = object : TimePickerDialog(requireContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, { _, hourOfDay, minute ->
            readyCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            readyCal.set(Calendar.MINUTE, minute)
            readyCal.set(Calendar.SECOND, 0)
            readyCal.set(Calendar.MILLISECOND, 0)

            if (readyCal.timeInMillis < minReadyCal.timeInMillis) {
                readyCal.timeInMillis = minReadyCal.timeInMillis
                Toast.makeText(requireContext(), "Time adjusted to the earliest feasible time.", Toast.LENGTH_SHORT).show()
            } 
            
            binding.readyTimeEditText.setText(fmt.format(readyCal.time))
        }, hour, min, false) {
            
            override fun onTimeChanged(view: TimePicker, hourOfDay: Int, minute: Int) {
                super.onTimeChanged(view, hourOfDay, minute)
                
                val tempCal = Calendar.getInstance()
                tempCal.timeInMillis = readyCal.timeInMillis 
                tempCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                tempCal.set(Calendar.MINUTE, minute)
                tempCal.set(Calendar.SECOND, 0)
                tempCal.set(Calendar.MILLISECOND, 0)
                
                if (tempCal.timeInMillis < minReadyCal.timeInMillis) {
                    updateTime(minReadyCal.get(Calendar.HOUR_OF_DAY), minReadyCal.get(Calendar.MINUTE))
                    // If clamped, we update the title based on the clamped time
                    updateStartTimeTitle(this, minReadyCal)
                } else {
                    // If valid, update title based on user selection
                    updateStartTimeTitle(this, tempCal)
                }
            }
        }
        
        tpd.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        // Set initial title
        updateStartTimeTitle(tpd, readyCal)
        
        tpd.show()
    }
    
    private fun updateStartTimeTitle(dialog: TimePickerDialog, readyTime: Calendar) {
        // Calculate start time: Ready Time - Total Hours
        val totalHours = selectedRecipe!!.schedule.maxOfOrNull { it.hoursBeforeReady } ?: 0
        val startTime = Calendar.getInstance()
        startTime.timeInMillis = readyTime.timeInMillis
        startTime.add(Calendar.HOUR_OF_DAY, -totalHours)
        
        val startFmt = SimpleDateFormat("EEE HH:mm", Locale.US)
        dialog.setTitle("Start Baking at: ${startFmt.format(startTime.time)}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}