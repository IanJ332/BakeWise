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
                        val bundle = Bundle().apply {
                            putInt("recipeId", recipe.id)
                            putInt("stepIndex", 0)
                        }
                        findNavController().navigate(R.id.action_newPickRecipeFragment_to_recipeStepFragment, bundle)
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