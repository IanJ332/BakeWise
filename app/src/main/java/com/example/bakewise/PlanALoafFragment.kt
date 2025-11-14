package com.example.bakewise

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bakewise.databinding.FragmentPlanALoafBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlanALoafFragment : Fragment() {

    private var _binding: FragmentPlanALoafBinding? = null
    private val binding get() = _binding!!

    private var selectedRecipe: Recipe? = null
    private val selectedDateTime = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanALoafBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.selectRecipeEditText.setOnClickListener {
            showRecipeSelectionDialog()
        }

        binding.requiredReadyTimeEditText.setOnClickListener {
            showDateTimePicker()
        }

        binding.clearButton.setOnClickListener {
            clearSelections()
        }

        binding.calculateScheduleButton.setOnClickListener {
            calculateSchedule()
        }
    }

    private fun showRecipeSelectionDialog() {
        val recipeNames = MOCK_RECIPES.map { it.name }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle("Select a Recipe")
            .setItems(recipeNames) { _, which ->
                selectedRecipe = MOCK_RECIPES[which]
                binding.selectRecipeEditText.setText(selectedRecipe?.name)
            }
            .show()
    }

    private fun showDateTimePicker() {
        val currentDateTime = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedDateTime.set(Calendar.YEAR, year)
                selectedDateTime.set(Calendar.MONTH, month)
                selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        selectedDateTime.set(Calendar.MINUTE, minute)
                        updateDateTimeEditText()
                    },
                    currentDateTime.get(Calendar.HOUR_OF_DAY),
                    currentDateTime.get(Calendar.MINUTE),
                    false
                ).show()
            },
            currentDateTime.get(Calendar.YEAR),
            currentDateTime.get(Calendar.MONTH),
            currentDateTime.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateTimeEditText() {
        val sdf = SimpleDateFormat("MMMM d, yyyy h:mm a", Locale.getDefault())
        binding.requiredReadyTimeEditText.setText(sdf.format(selectedDateTime.time))
    }

    private fun clearSelections() {
        selectedRecipe = null
        binding.selectRecipeEditText.text = null
        binding.requiredReadyTimeEditText.text = null
    }

    private fun calculateSchedule() {
        if (selectedRecipe == null) {
            Toast.makeText(requireContext(), "Please select a recipe first.", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.requiredReadyTimeEditText.text.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please select a ready time.", Toast.LENGTH_SHORT).show()
            return
        }

        val scheduleSteps = mutableListOf<String>()
        val sdf = SimpleDateFormat("MMMM d, yyyy h:mm a", Locale.getDefault())

        selectedRecipe!!.schedule.forEach { step ->
            val stepTime = selectedDateTime.clone() as Calendar
            stepTime.add(Calendar.HOUR_OF_DAY, -step.hoursBeforeReady)
            scheduleSteps.add("${step.stepName}: ${sdf.format(stepTime.time)}")
        }

        val bundle = Bundle().apply {
            putString("recipeName", selectedRecipe!!.name)
            putStringArray("scheduleData", scheduleSteps.toTypedArray())
        }
        findNavController().navigate(R.id.action_planALoafFragment_to_scheduleFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}