package com.example.bakewise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bakewise.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.planALoafButton.setOnClickListener {
            // Corrected: This should go to the PlanALoafFragment, not the recipe list.
            findNavController().navigate(R.id.action_homeFragment_to_planALoafFragment)
        }

        binding.bakeNowButton.setOnClickListener {
             val bundle = Bundle().apply {
                putString("source", "BakeNow")
            }
            findNavController().navigate(R.id.action_homeFragment_to_newPickRecipeFragment, bundle)
        }

        binding.currentSchedulesButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_currentSchedulesFragment)
        }

        binding.exploreRecipesButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("source", "ExploreRecipes")
            }
            findNavController().navigate(R.id.action_homeFragment_to_newPickRecipeFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}