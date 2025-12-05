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

        when (stepIndex) {
            0 -> { // Feed Starter
                binding.notReadyTitle.text = "Under-active"
                binding.readyTitle.text = "Ready"
                binding.notReadyDescription.text = "Dough still looks dense, hasn't risen much, and feels tight. Few or no bubbles."
                binding.readyDescription.text = "Starter has doubled or tripled in size, is bubbly, and smells pleasant/tangy. It floats in water."
                binding.imageNotReady.setImageResource(R.drawable.starter_not_ready)
                binding.imageReady.setImageResource(R.drawable.starter_ready)
            }
            1 -> { // Autolyse
                binding.notReadyTitle.text = "Dry spots"
                binding.readyTitle.text = "Ready"
                binding.notReadyDescription.text = "You can see dry flour patches or hard lumps. The dough feels very stiff and uneven."
                binding.readyDescription.text = "No dry flour visible. The dough is a shaggy, sticky mass but is fully hydrated."
                binding.imageNotReady.setImageResource(R.drawable.autolyse_dry)
                binding.imageReady.setImageResource(R.drawable.autolyse_ready)
            }
            2 -> { // Add Salt + First Fold
                binding.notReadyTitle.text = "Gritty"
                binding.readyTitle.text = "Ready"
                binding.notReadyDescription.text = "You can still feel grains of salt when you pinch the dough. The dough tears immediately when stretched."
                binding.readyDescription.text = "Salt is fully dissolved and incorporated. The dough starts to show some stretchiness."
                binding.imageNotReady.setImageResource(R.drawable.autolyse_dry) // Reuse
                binding.imageReady.setImageResource(R.drawable.autolyse_ready)
            }
            3 -> { // Strengthening Fold
                binding.notReadyTitle.text = "Slack"
                binding.readyTitle.text = "Ready"
                binding.notReadyDescription.text = "The dough is very slack and spreads out like a puddle immediately after folding. No structure."
                binding.readyDescription.text = "The dough holds its shape for a while after folding. The surface is becoming smoother and stronger."
                binding.imageNotReady.setImageResource(R.drawable.bulk_not_ready) // Reuse
                binding.imageReady.setImageResource(R.drawable.bulk_ready)
            }
            4 -> { // Bulk Fermentation
                binding.notReadyTitle.text = "Under-fermented"
                binding.readyTitle.text = "Ready"
                binding.notReadyDescription.text = "Dough looks flat, dense, and hasn't grown much. No bubbles on the surface."
                binding.readyDescription.text = "Dough has grown 30-50% in volume, looks puffy, and jiggles like jello when you shake the bowl. Bubbles visible."
                binding.imageNotReady.setImageResource(R.drawable.bulk_not_ready)
                binding.imageReady.setImageResource(R.drawable.bulk_ready)
            }
            5 -> { // Preshape
                binding.notReadyTitle.text = "Sticky/Weak"
                binding.readyTitle.text = "Ready"
                binding.notReadyDescription.text = "Dough is an uncontrollable sticky mess that refuses to form a ball and spreads flat instantly."
                binding.readyDescription.text = "Dough forms a loose round shape and holds it reasonably well on the counter."
                binding.imageNotReady.setImageResource(R.drawable.shape_tearing) // Reuse
                binding.imageReady.setImageResource(R.drawable.shape_smooth)
            }
            6 -> { // Final Shape
                binding.notReadyTitle.text = "Tearing"
                binding.readyTitle.text = "Ready"
                binding.notReadyDescription.text = "The surface of the dough tears (looks like stretch marks) or feels weak and loose."
                binding.readyDescription.text = "The dough ball has a smooth, taut skin (tension) and springs back slightly when touched."
                binding.imageNotReady.setImageResource(R.drawable.shape_tearing)
                binding.imageReady.setImageResource(R.drawable.shape_smooth)
            }
            7 -> { // Cold Proof
                binding.notReadyTitle.text = "Under-proofed"
                binding.readyTitle.text = "Ready"
                binding.notReadyDescription.text = "If you poke the dough, the hole springs back very quickly and completely."
                binding.readyDescription.text = "If you poke the dough, the indentation slowly springs back but leaves a small dent."
                binding.imageNotReady.setImageResource(R.drawable.poke_underproofed)
                binding.imageReady.setImageResource(R.drawable.poke_ready)
            }
            8 -> { // Preheat
                binding.notReadyTitle.text = "Too Cold"
                binding.readyTitle.text = "Ready"
                binding.notReadyDescription.text = "Oven thermometer reads below 250°C."
                binding.readyDescription.text = "Oven is fully preheated to 250°C (482°F) and Dutch oven is scorching hot."
                binding.imageNotReady.setImageResource(R.drawable.bake_pale) // Reuse
                binding.imageReady.setImageResource(R.drawable.bake_golden)
            }
            9 -> { // Bake (Lid On)
                binding.notReadyTitle.text = "Flat"
                binding.readyTitle.text = "Ready"
                binding.notReadyDescription.text = "Loaf looks flat or pale."
                binding.readyDescription.text = "Loaf has expanded significantly (oven spring) and the cut has opened up."
                binding.imageNotReady.setImageResource(R.drawable.bake_pale)
                binding.imageReady.setImageResource(R.drawable.bake_golden)
            }
            10 -> { // Bake (Lid Off)
                binding.notReadyTitle.text = "Pale"
                binding.readyTitle.text = "Ready"
                binding.notReadyDescription.text = "Crust is pale or soft. Bottom does not sound hollow when tapped."
                binding.readyDescription.text = "Deep golden brown to dark crust. The loaf sounds hollow when tapped on the bottom."
                binding.imageNotReady.setImageResource(R.drawable.bake_pale)
                binding.imageReady.setImageResource(R.drawable.bake_golden)
            }
            11 -> { // Cooling
                binding.notReadyTitle.text = "Warm"
                binding.readyTitle.text = "Ready"
                binding.notReadyDescription.text = "Bread is still warm to the touch."
                binding.readyDescription.text = "Bread is completely cool (room temperature) and ready to slice."
                binding.imageNotReady.setImageResource(R.drawable.bake_golden)
                binding.imageReady.setImageResource(R.drawable.bake_golden)
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