package com.example.bakewise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bakewise.databinding.FragmentCurrentSchedulesBinding

class CurrentSchedulesFragment : Fragment() {

    private var _binding: FragmentCurrentSchedulesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SavedScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrentSchedulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SavedScheduleAdapter(
            ScheduleRepository.savedSchedules,
            onItemClick = { schedule ->
                val bundle = Bundle().apply {
                    putString("recipeName", schedule.recipeName)
                    // Pass the full ScheduleItems array so we get timestamps
                    putParcelableArray("scheduleItems", schedule.scheduleItems.toTypedArray())
                    putString("scheduleName", schedule.name)
                }
                findNavController().navigate(R.id.action_currentSchedulesFragment_to_scheduleFragment, bundle)
            },
            onDeleteClick = { schedule ->
                ScheduleRepository.savedSchedules.remove(schedule)
                adapter.notifyDataSetChanged()
            }
        )

        binding.schedulesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.schedulesRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}