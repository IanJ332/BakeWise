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
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bakewise.databinding.FragmentFeedbackBinding
import java.io.File
import kotlin.random.Random

class FeedbackFragment : Fragment() {

    private var _binding: FragmentFeedbackBinding? = null
    private val binding get() = _binding!!

    private var currentPhotoUri: Uri? = null
    private var currentFeedback: String? = null

    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success && currentPhotoUri != null) {
            displayPhotoAndAnalyze(currentPhotoUri!!)
        }
    }

    private val selectGalleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            currentPhotoUri = uri
            displayPhotoAndAnalyze(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedbackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipeId = arguments?.getInt("recipeId") ?: -1
        val stepIndex = arguments?.getInt("stepIndex") ?: -1

        val recipe = MOCK_RECIPES.find { it.id == recipeId }
        val stepName = recipe?.schedule?.getOrNull(stepIndex)?.stepName ?: "Unknown Step"

        binding.bakingNavBar.navViewStepsButton.setOnClickListener {
            if (recipe != null) {
                val bundle = Bundle().apply {
                    putString("recipeName", recipe.name)
                    putParcelableArray("scheduleData", recipe.schedule.toTypedArray())
                }
                findNavController().navigate(R.id.action_feedbackFragment_to_scheduleFragment, bundle)
            }
        }

        binding.bakingNavBar.navBackButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("recipeId", recipeId)
                putInt("stepIndex", stepIndex)
            }
            findNavController().navigate(R.id.action_feedbackFragment_to_stepWaitingFragment, bundle)
        }

        // Setup Reference Content
        when (stepIndex) {
            0 -> {
                binding.notReadyDescription.text = "Dough still looks dense, hasn't risen much, and feels tight."
                binding.readyDescription.text = "Dough has risen, feels soft, and is full of air. You should see some bubbles on the surface."
            }
            1 -> {
                binding.notReadyTitle.text = "Under-proofed"
                binding.readyTitle.text = "Ready"
                binding.notReadyDescription.text = "If you poke the dough, the hole springs back very quickly."
                binding.readyDescription.text = "If you poke the dough, the indentation slowly springs back, but not completely."
            }
        }

        // Setup Evaluation Logic
        binding.takePhotoButton.setOnClickListener {
            val photoFile = File(requireContext().getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES), "step_${stepIndex}_${System.currentTimeMillis()}.jpg")
            currentPhotoUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", photoFile)
            takePhotoLauncher.launch(currentPhotoUri)
        }

        binding.selectGalleryButton.setOnClickListener {
            selectGalleryLauncher.launch("image/*")
        }

        binding.addToNotesButton.setOnClickListener {
            if (currentFeedback != null) {
                // Save to session notes
                val existingNote = CurrentBakeSession.stepNotes.find { it.stepIndex == stepIndex }
                val noteText = if (existingNote != null) {
                    "${existingNote.note}\n\n[AI Feedback]: $currentFeedback"
                } else {
                    "[AI Feedback]: $currentFeedback"
                }
                
                val photoToSave = currentPhotoUri?.toString() ?: existingNote?.imageUri
                
                CurrentBakeSession.addNote(stepIndex, stepName, noteText, photoToSave)
                
                Toast.makeText(requireContext(), "Added to Diary!", Toast.LENGTH_SHORT).show()
                binding.addToNotesButton.isEnabled = false
                binding.addToNotesButton.text = "Added"
            }
        }

        binding.completeStepButton.setOnClickListener {
            val nextStepIndex = stepIndex + 1
            if (nextStepIndex < (recipe?.schedule?.size ?: 0)) {
                val bundle = Bundle().apply {
                    putInt("recipeId", recipeId)
                    putInt("stepIndex", nextStepIndex)
                }
                findNavController().navigate(R.id.action_feedbackFragment_to_recipeStepFragment, bundle)
            } else {
                val bundle = Bundle().apply {
                    putInt("recipeId", recipeId)
                }
                findNavController().navigate(R.id.action_feedbackFragment_to_bakeCompleteFragment, bundle)
            }
        }
    }

    private fun displayPhotoAndAnalyze(uri: Uri) {
        binding.userStepPhoto.setImageURI(uri)
        binding.userStepPhoto.isVisible = true
        binding.takePhotoButton.text = "Retake Photo"
        startAnalysisSimulation()
    }

    private fun startAnalysisSimulation() {
        binding.analysisProgressBar.isVisible = true
        binding.analysisStatusText.isVisible = true
        binding.aiFeedbackText.isVisible = false
        binding.addToNotesButton.isVisible = false
        
        // Simulate random wait time between 2 to 4 seconds
        val waitTime = Random.nextLong(2000, 4000)
        
        Handler(Looper.getMainLooper()).postDelayed({
            if (_binding != null) {
                binding.analysisProgressBar.isVisible = false
                binding.analysisStatusText.isVisible = false
                binding.aiFeedbackText.isVisible = true
                binding.addToNotesButton.isVisible = true
                binding.addToNotesButton.isEnabled = true
                binding.addToNotesButton.text = "Add to Diary"
                
                val feedback = generateMockFeedback()
                currentFeedback = feedback
                binding.aiFeedbackText.text = feedback
            }
        }, waitTime)
    }

    private fun generateMockFeedback(): String {
        val feedbacks = listOf(
            "Looking good! The dough structure is developing nicely. You can see the gluten network forming.",
            "Great progress. The volume has increased significantly. It might be ready for the next step soon.",
            "The surface looks a bit dry. Consider covering it more tightly during the next rest.",
            "Excellent fermentation signs. The bubbles are small and evenly distributed.",
            "It seems a bit slack. You might want to perform an extra fold to build more strength."
        )
        return "AI Analysis: ${feedbacks.random()}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}