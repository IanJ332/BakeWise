package com.example.bakewise

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bakewise.databinding.FragmentBakeCompleteBinding
import java.io.File
import java.util.Date
import kotlin.random.Random

class BakeCompleteFragment : Fragment() {

    private var _binding: FragmentBakeCompleteBinding? = null
    private val binding get() = _binding!!

    private var currentPhotoUri: Uri? = null
    private var currentFeedback: String? = null
    private var overallConclusion: String? = null

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

        // Generate Overall Conclusion
        generateOverallConclusion()

        // Display the accumulated diary (notes + photos)
        setupDiaryRecyclerView()

        binding.takePhotoButton.setOnClickListener {
            val photoFile = File(requireContext().getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES), "final_loaf_${System.currentTimeMillis()}.jpg")
            currentPhotoUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", photoFile)
            takePhotoLauncher.launch(currentPhotoUri)
        }

        binding.selectGalleryButton.setOnClickListener {
            selectGalleryLauncher.launch("image/*")
        }

        binding.returnHomeButton.setOnClickListener {
            removeActiveSchedule()
            CurrentBakeSession.clear()
            findNavController().navigate(R.id.action_bakeCompleteFragment_to_homeFragment)
        }

        binding.saveLoafButton.setOnClickListener {
            if (recipe != null) {
                // We save the accumulated notes AND the final loaf feedback/photo
                val loaf = PastLoaf(
                    recipeName = recipe.name,
                    dateBaked = Date(),
                    notes = CurrentBakeSession.stepNotes.toList(),
                    finalPhotoUri = currentPhotoUri?.toString(),
                    feedback = currentFeedback,
                    overallConclusion = overallConclusion
                )
                PastLoavesRepository.addLoaf(loaf)
                Toast.makeText(requireContext(), "Loaf saved to Diary!", Toast.LENGTH_SHORT).show()
                
                removeActiveSchedule()
                CurrentBakeSession.clear()
                findNavController().navigate(R.id.action_bakeCompleteFragment_to_homeFragment)
            }
        }
    }

    private fun setupDiaryRecyclerView() {
        val notes = CurrentBakeSession.stepNotes
        if (notes.isNotEmpty()) {
            binding.diaryRecyclerView.isVisible = true
            binding.diaryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.diaryRecyclerView.adapter = LoafDetailsAdapter(notes) { noteToEdit ->
                showEditNoteDialog(noteToEdit)
            }
            binding.emptyDiaryText.isVisible = false
        } else {
            binding.diaryRecyclerView.isVisible = false
            binding.emptyDiaryText.isVisible = true
        }
    }

    private fun showEditNoteDialog(note: StepNote) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Edit Note: ${note.stepName}")

        val input = EditText(requireContext())
        input.setText(note.note)
        builder.setView(input)

        builder.setPositiveButton("Update") { _, _ ->
            val newText = input.text.toString()
            // Update the note in the session
            val index = CurrentBakeSession.stepNotes.indexOf(note)
            if (index != -1) {
                CurrentBakeSession.stepNotes[index] = note.copy(note = newText)
                binding.diaryRecyclerView.adapter?.notifyItemChanged(index)
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        
        builder.setNeutralButton("Delete") { _, _ ->
             CurrentBakeSession.stepNotes.remove(note)
             setupDiaryRecyclerView() // Refresh list
        }

        builder.show()
    }

    private fun displayPhotoAndAnalyze(uri: Uri) {
        binding.loafPhotoImageView.setImageURI(uri)
        binding.loafPhotoImageView.isVisible = true
        binding.takePhotoButton.text = "Retake Photo"
        startAnalysisSimulation()
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

    private fun generateOverallConclusion() {
        // Mock logic to generate a conclusion based on "data"
        val conclusions = listOf(
            "Overall, this was a very successful bake! Your dough handling has improved significantly.",
            "A solid attempt. The final loaf shows great potential, though fermentation could be tweaked.",
            "Excellent work! You nailed the timing and temperature control today.",
            "Good job. Keep practicing your shaping technique for an even better ear next time."
        )
        overallConclusion = conclusions.random()
        binding.overallConclusionText.text = overallConclusion
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