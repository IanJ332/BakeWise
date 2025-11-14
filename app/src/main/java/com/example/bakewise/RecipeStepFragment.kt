package com.example.bakewise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bakewise.databinding.FragmentRecipeStepBinding

class RecipeStepFragment : Fragment() {

    private var _binding: FragmentRecipeStepBinding? = null
    private val binding get() = _binding!!

    private var stepIndex: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stepIndex = arguments?.getInt("stepIndex") ?: -1

        binding.bakingNavBar.navViewStepsButton.setOnClickListener {
            findNavController().navigate(R.id.action_global_stepListDialogFragment)
        }

        binding.bakingNavBar.navBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        when (stepIndex) {
            0 -> {
                binding.stepTitleTextView.text = "Step 1: Mix & Develop Dough"
                binding.instructionsTextView.text = "Welcome! Let\'s begin by mixing our ingredients.\n\nIn a large bowl, combine your active sourdough starter, water, and flour. Mix with your hands or a spatula until no dry flour remains. Cover the bowl and let the dough rest for 30 minutes. This is called the \'autolyse\' and helps the flour absorb the water.\n\nAfter the rest, add the salt and a little more water. Use your hands to pinch and squeeze the salt into the dough until it feels fully combined. Cover the bowl."
            }
            1 -> {
                binding.stepTitleTextView.text = "Step 2: Shape & Proof"
                binding.instructionsTextView.text = "Your dough is now airy and active. Gently scrape the dough onto a lightly floured surface. Be careful not to push all the air out.\n\nFold the edges of the dough into the center to form a round shape (a \'boule\'). Use a bench scraper to flip the dough over and gently drag it a few inches to create surface tension. You want a tight, smooth ball.\n\nPlace the shaped dough into a banneton (or a bowl lined with a floured towel), seam-side up."
            }
            2 -> {
                binding.stepTitleTextView.text = "Step 3: Bake!"
                binding.instructionsTextView.text = "This is the final step! Place your Dutch oven (with its lid) inside your oven and preheat both to 450°F (230°C) for at least 45 minutes.\n\nCarefully take your hot Dutch oven out. Gently flip your cold dough from the banneton onto a piece of parchment paper, and score the top with a sharp blade.\n\nLift the parchment paper and lower your dough into the hot Dutch oven. Put the lid on, place it in the oven, and bake for 20 minutes.\n\nAfter 20 minutes, remove the lid. The bread will be pale. Continue baking with the lid *off* for another 20-25 minutes until deep golden brown."
            }
        }

        binding.doneButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("stepIndex", stepIndex)
            }
            findNavController().navigate(R.id.action_recipeStepFragment_to_stepWaitingFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}