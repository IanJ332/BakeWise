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

        // Try to find and remove the completed schedule
        // We need to identify which schedule was active. 
        // Since we don't strictly link the "Bake Now" flow to a specific SavedSchedule ID,
        // we can try to match by name or just rely on the user manually clearing it if they started from scratch.
        // However, if we started from a Schedule, we should ideally pass that info along.
        // For now, as a heuristic requested by the user, if we complete a bake, we can check if there's a schedule
        // that matches this recipe and is "due" or "started".
        // But safer is to just let the "Return Home" clear the session (which it does).
        
        // The user specifically asked: "once we go thorugh the baking process a scheudle shouold be removed from an active scehdyekls"
        // This implies we should delete the schedule from ScheduleRepository if it was the one being baked.
        // We can do this if we know the schedule name or ID.
        // Currently `CurrentBakeSession` only stores recipeId and name.
        // We should probably add `scheduleName` to `CurrentBakeSession` if it came from a schedule.
        
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