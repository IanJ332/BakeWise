package com.example.bakewise

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class StepDetailDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_STEP_NAME = "step_name"
        private const val ARG_STEP_DESCRIPTION = "step_description"

        fun newInstance(stepName: String, stepDescription: String): StepDetailDialogFragment {
            val args = Bundle().apply {
                putString(ARG_STEP_NAME, stepName)
                putString(ARG_STEP_DESCRIPTION, stepDescription)
            }
            return StepDetailDialogFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val stepName = arguments?.getString(ARG_STEP_NAME) ?: ""
        val stepDescription = arguments?.getString(ARG_STEP_DESCRIPTION) ?: ""

        return AlertDialog.Builder(requireContext())
            .setTitle(stepName)
            .setMessage(stepDescription)
            .setPositiveButton("OK", null)
            .create()
    }
}