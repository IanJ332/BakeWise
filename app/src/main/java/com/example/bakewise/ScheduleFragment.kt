package com.example.bakewise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.bakewise.databinding.FragmentScheduleBinding

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipeName = arguments?.getString("recipeName")
        val scheduleData = arguments?.getStringArray("scheduleData")

        binding.recipeNameTextView.text = recipeName

        scheduleData?.forEach { step ->
            val textView = TextView(requireContext())
            textView.text = step
            textView.textSize = 16f
            binding.scheduleStepsLayout.addView(textView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}