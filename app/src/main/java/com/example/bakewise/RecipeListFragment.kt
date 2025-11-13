package com.example.bakewise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bakewise.databinding.FragmentRecipeListBinding

class RecipeListFragment : Fragment() {

    private var _binding: FragmentRecipeListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MOCK_RECIPES.forEach { recipe ->
            val button = Button(requireContext()).apply {
                text = recipe.name
                setOnClickListener {
                    val bundle = Bundle().apply {
                        putInt("recipeId", recipe.id)
                    }
                    findNavController().navigate(R.id.action_recipeListFragment_to_recipeDetailFragment, bundle)
                }
            }
            binding.recipeListContainer.addView(button)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}