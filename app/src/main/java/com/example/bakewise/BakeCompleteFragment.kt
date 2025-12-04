package com.example.bakewise

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bakewise.databinding.FragmentBakeCompleteBinding
import java.util.Date
import kotlin.random.Random

class BakeCompleteFragment : Fragment() {

    private var _binding: FragmentBakeCompleteBinding? = null
    private val binding get() = _binding!!

    private var currentPhotoUri: String? = null
    private var currentFeedback: String? = null

    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            currentPhotoUri = uri.toString()
            binding.loafPhotoImageView.setImageURI(uri)
            binding.loafPhotoImageView.isVisible = true
            binding.takePhotoButton.isVisible = false // Hide button after taking photo
            
            startAnalysisSimulation()
        }
    }

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

        binding.takePhotoButton.setOnClickListener {
            takePhotoLauncher.launch("image/*")
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
                    notes = CurrentBakeSession.stepNotes.toList(),
                    feedback = currentFeedback,
                    finalPhotoUri = currentPhotoUri
                )
                PastLoavesRepository.addLoaf(loaf)
                Toast.makeText(requireContext(), "Loaf saved to Diary!", Toast.LENGTH_SHORT).show()
                
                removeActiveSchedule()
                CurrentBakeSession.clear()
                findNavController().navigate(R.id.action_bakeCompleteFragment_to_homeFragment)
            }
        }
    }
    
    private fun startAnalysisSimulation() {
        binding.analysisProgressBar.isVisible = true
        binding.analysisStatusText.isVisible = true
        binding.analysisResultText.isVisible = false
        
        // Simulate random wait time between 2 to 5 seconds
        val waitTime = Random.nextLong(2000, 5000)
        
        Handler(Looper.getMainLooper()).postDelayed({
            if (_binding != null) {
                binding.analysisProgressBar.isVisible = false
                binding.analysisStatusText.isVisible = false
                binding.analysisResultText.isVisible = true
                
                val feedback = generateMockFeedback()
                currentFeedback = feedback
                binding.analysisResultText.text = feedback
            }
        }, waitTime)
    }
    
    private fun generateMockFeedback(): String {
        val positive = listOf(
            "Great oven spring! The crust looks perfectly caramelized.",
            "The crumb structure looks open and airy. Excellent fermentation.",
            "Beautiful ear! You nailed the scoring angle.",
            "The color is a rich, deep golden brown. Looks delicious!"
        )
        
        val constructive = listOf(
            "The crumb is a bit tight. Maybe extend the bulk fermentation next time.",
            "Looks a bit pale. Try increasing the oven temperature slightly.",
            "The shape is a bit flat. Work on building more tension during shaping.",
            "Slightly under-proofed. Give it another 30 minutes in the final proof."
        )
        
        // Randomly pick one positive and one constructive
        val p = positive.random()
        val c = constructive.random()
        
        return "AI Analysis:\n\n$p\n\n$c"
    }
    
    private fun removeActiveSchedule() {
        val scheduleName = CurrentBakeSession.scheduleName
        if (!scheduleName.isNullOrEmpty()) {
            val scheduleToRemove = ScheduleRepository.savedSchedules.find { it.name == scheduleName }
            if (scheduleToRemove != null) {
                ScheduleRepository.savedSchedules.remove(scheduleToRemove)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}