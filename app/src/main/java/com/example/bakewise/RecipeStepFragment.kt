package com.example.bakewise

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bakewise.databinding.FragmentRecipeStepBinding

class RecipeStepFragment : Fragment() {

    private var _binding: FragmentRecipeStepBinding? = null
    private val binding get() = _binding!!

    private var currentImageUri: String? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri.toString()
            binding.stepPhotoImageView.setImageURI(uri)
            binding.stepPhotoImageView.isVisible = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipeId = arguments?.getInt("recipeId") ?: -1
        val stepIndex = arguments?.getInt("stepIndex") ?: -1
        val isViewingOnly = arguments?.getBoolean("isViewingOnly") ?: false

        val recipe = MOCK_RECIPES.find { it.id == recipeId }
        val step = recipe?.schedule?.getOrNull(stepIndex)

        if (step != null) {
            binding.stepTitleTextView.text = "Step ${stepIndex + 1}: ${step.stepName}"
            binding.instructionsTextView.text = step.description
            // ---------------------------------------------
            // DISPLAY STATIC IMAGES FOR THE RECIPE STEP
            // ---------------------------------------------
            if (step.imageResId.isNotEmpty()) {
                binding.imageScrollView.isVisible = true

                val container = binding.imageContainer
                container.removeAllViews()

                for (resId in step.imageResId) {

                    val layoutParams = ViewGroup.MarginLayoutParams(
                        300, // width in px
                        ViewGroup.LayoutParams.MATCH_PARENT // height
                    )
                    layoutParams.rightMargin = 16

                    val imageView = ImageView(requireContext())
                    imageView.layoutParams = layoutParams
                    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    imageView.setImageResource(resId)

                    container.addView(imageView)
                }
            }
            if (step.videoResId.isNotEmpty()) {
                binding.videoView.isVisible = true

                val videoResId = step.videoResId.first() // For now, show the first video
                val videoUri = Uri.parse("android.resource://${requireContext().packageName}/$videoResId")

                binding.videoView.setVideoURI(videoUri)

                // Optional auto-start
                binding.videoView.setOnPreparedListener { mp ->
                    mp.isLooping = true
                    binding.videoView.start()
                }

                // Show simple tap-to-play/pause behavior
                binding.videoView.setOnClickListener {
                    if (binding.videoView.isPlaying) {
                        binding.videoView.pause()
                    } else {
                        binding.videoView.start()
                    }
                }
            }
        }


        // Restore saved note and image if any
        val existingNote = CurrentBakeSession.stepNotes.find { it.stepIndex == stepIndex }
        if (existingNote != null) {
            if (!existingNote.note.isNullOrEmpty()) {
                binding.noteInputEditText.setText(existingNote.note)
            }
            if (existingNote.imageUri != null) {
                currentImageUri = existingNote.imageUri
                binding.stepPhotoImageView.setImageURI(Uri.parse(existingNote.imageUri))
                binding.stepPhotoImageView.isVisible = true
            }
        }

        // The back button should always be visible and functional.
        binding.bakingNavBar.navBackButton.setOnClickListener {
            saveCurrentNote(stepIndex, step?.stepName)
            findNavController().popBackStack()
        }

        // Only show the other buttons if we are in an active bake.
        binding.doneButton.isVisible = !isViewingOnly
        binding.bakingNavBar.navViewStepsButton.isVisible = !isViewingOnly
        
        // Hide note input and photo button if viewing only
        binding.noteInputLayout.isVisible = !isViewingOnly
        binding.noteInputEditText.isVisible = !isViewingOnly
        binding.addPhotoButton.isVisible = !isViewingOnly

        binding.addPhotoButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        if (!isViewingOnly) {
            // Update session info if needed
            if (CurrentBakeSession.recipeId == -1 && recipe != null) {
                CurrentBakeSession.recipeId = recipe.id
                CurrentBakeSession.recipeName = recipe.name
            }

            // This is an active bake, so set up the listeners for the other buttons.
            binding.doneButton.setOnClickListener {
                saveCurrentNote(stepIndex, step?.stepName)

                val bundle = Bundle().apply {
                    putInt("recipeId", recipeId)
                    putInt("stepIndex", stepIndex)
                }
                findNavController().navigate(R.id.action_recipeStepFragment_to_stepWaitingFragment, bundle)
            }

            binding.bakingNavBar.navViewStepsButton.setOnClickListener {
                saveCurrentNote(stepIndex, step?.stepName)
                
                if (recipe != null) {
                    val bundle = Bundle().apply {
                        putString("recipeName", recipe.name)
                        putParcelableArray("scheduleData", recipe.schedule.toTypedArray())
                    }
                    findNavController().navigate(R.id.action_recipeStepFragment_to_scheduleFragment, bundle)
                }
            }
        }
    }

    private fun saveCurrentNote(stepIndex: Int, stepName: String?) {
        val note = binding.noteInputEditText.text.toString()
        if ((note.isNotBlank() || currentImageUri != null) && stepName != null) {
            CurrentBakeSession.addNote(stepIndex, stepName, note, currentImageUri)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}