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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPickRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipeAdapter = RecipeAdapter(
            MOCK_RECIPES,
            onItemClick = {
                val bundle = Bundle().apply {
                    putInt("recipeId", it.id)
                    putInt("stepIndex", 0)
                }
                findNavController().navigate(R.id.action_newPickRecipeFragment_to_recipeStepFragment, bundle)
            },
            onDetailsClick = { recipe ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Time Details (${recipe.name})")
                    .setMessage("Total: ${recipe.totalTime}\nActive Time: 1h\nWaiting: 29h")
                    .setPositiveButton("OK", null)
                    .show()
            }
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