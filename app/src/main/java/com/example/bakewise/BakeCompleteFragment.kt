package com.example.bakewise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bakewise.databinding.FragmentBakeCompleteBinding
import java.util.Date

class BakeCompleteFragment : Fragment() {

    private var _binding: FragmentBakeCompleteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBakeCompleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipeId = arguments?.getInt("recipeId") ?: -1
        val recipe = MOCK_RECIPES.find { it.id == recipeId }

        if (recipe != null) {
            binding.summaryText.text = "You have successfully baked your ${recipe.name}!"
        }

        binding.returnHomeButton.setOnClickListener {
            removeActiveSchedule()
            CurrentBakeSession.clear()
            findNavController().navigate(R.id.action_bakeCompleteFragment_to_homeFragment)
        }

        binding.saveLoafButton.setOnClickListener {
            if (recipe != null) {
                val loaf = PastLoaf(
                    recipeName = recipe.name,
                    dateBaked = Date(),
                    notes = CurrentBakeSession.stepNotes.toList()
                )
                PastLoavesRepository.addLoaf(loaf)
                Toast.makeText(requireContext(), "Loaf saved to Past Loaves!", Toast.LENGTH_SHORT).show()
                
                removeActiveSchedule()
                
                // Clear session after saving
                CurrentBakeSession.clear()
                
                // Navigate home or maybe to a "Past Loaves" screen if it existed, but Home is safe
                findNavController().navigate(R.id.action_bakeCompleteFragment_to_homeFragment)
            }
        }
    }
    
    private fun removeActiveSchedule() {
        // If we have a tracked schedule name in the session, remove it.
        val scheduleName = CurrentBakeSession.scheduleName
        if (!scheduleName.isNullOrEmpty()) {
            val scheduleToRemove = ScheduleRepository.savedSchedules.find { it.name == scheduleName }
            if (scheduleToRemove != null) {
                ScheduleRepository.savedSchedules.remove(scheduleToRemove)
                // Also cancel notifications for this schedule? 
                // ideally yes, but they are one-shot alarms.
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}