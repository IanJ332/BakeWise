package com.example.bakewise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bakewise.databinding.FragmentNewPickRecipeBinding
import java.util.Calendar

class NewPickRecipeFragment : Fragment() {

    private var _binding: FragmentNewPickRecipeBinding? = null
    private val binding get() = _binding!!

    private var source: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        source = arguments?.getString("source")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPickRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the title based on the source
        binding.titleTextView.text = when (source) {
            "ExploreRecipes" -> "Explore Recipes"
            else -> "Pick your recipe"
        }

        val recipeAdapter = RecipeAdapter(
            MOCK_RECIPES,
            onItemClick = { recipe, source ->
                when (source) {
                    "PlanALoaf" -> {
                        findNavController().previousBackStackEntry?.savedStateHandle?.set("selectedRecipeId", recipe.id)
                        findNavController().popBackStack()
                    }
                    "BakeNow" -> {
                        // Start a new baking session
                        CurrentBakeSession.clear()
                        
                        // Calculate times based on current time for "Bake Now"
                        val currentTime = System.currentTimeMillis()
                        val scheduleItems = recipe.schedule.map { step ->
                            // For Bake Now, we assume we start step 1 NOW.
                            // But wait, the schedule data has "hoursBeforeReady".
                            // "Bake Now" usually implies we are doing step 1 now.
                            // The subsequent steps are spaced out based on recipe logic.
                            
                            // Let's assume the "schedule" list in MOCK_RECIPES is ordered.
                            // We need to calculate relative timestamps.
                            // The BakeStep model has 'hoursBeforeReady'.
                            // If step 0 is NOW, then Ready Time is NOW + step[0].hoursBeforeReady ?
                            // No, 'hoursBeforeReady' is relative to the END.
                            // E.g. Step 0 (Feed Starter): 28 hours before ready.
                            // Step 1 (Mix Dough): 22 hours before ready.
                            // So Step 1 is 6 hours AFTER Step 0.
                            
                            // So:
                            // Ready Time = Now + Step[0].hoursBeforeReady
                            // Then for each step: Time = Ready Time - Step.hoursBeforeReady

                            val firstStepHours = recipe.schedule.firstOrNull()?.hoursBeforeReady ?: 0.0
                            // Convert the math to Long explicitly
                            val readyTimeMillis = currentTime + (firstStepHours * 60 * 60 * 1000).toLong()

                            // Do the same for the subtraction
                            val stepTime = readyTimeMillis - (step.hoursBeforeReady * 60 * 60 * 1000).toLong()

                            ScheduleItem(stepTime, step)
                        }
                        
                        val bundle = Bundle().apply {
                            putString("recipeName", recipe.name)
                            // Pass calculated schedule items instead of just raw data
                            putParcelableArray("scheduleItems", scheduleItems.toTypedArray())
                            putBoolean("isBakeNowPreview", true)
                            putInt("recipeId", recipe.id)
                        }
                        findNavController().navigate(R.id.action_newPickRecipeFragment_to_scheduleFragment, bundle)
                    }
                    "ExploreRecipes" -> {
                        val bundle = Bundle().apply {
                            putString("recipeName", recipe.name)
                            putParcelableArray("scheduleData", recipe.schedule.toTypedArray())
                        }
                        findNavController().navigate(R.id.action_newPickRecipeFragment_to_scheduleFragment, bundle)
                    }
                }
            },
            onDetailsClick = { recipe ->
                val stepNames = recipe.schedule.map { it.stepName }.toTypedArray()
                AlertDialog.Builder(requireContext())
                    .setTitle("Steps for ${recipe.name}")
                    .setItems(stepNames) { _, which ->
                        val selectedStep = recipe.schedule[which]
                        StepDetailDialogFragment.newInstance(selectedStep.stepName, selectedStep.description)
                            .show(childFragmentManager, "StepDetailDialog")
                    }
                    .setPositiveButton("Close", null)
                    .show()
            },
            source = source ?: "BakeNow" // Default to BakeNow if source is null
        )

        binding.recipeRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recipeAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}