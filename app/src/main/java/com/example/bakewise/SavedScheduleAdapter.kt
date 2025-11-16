package com.example.bakewise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SavedScheduleAdapter(
    private val schedules: MutableList<SavedSchedule>,
    private val onItemClick: (SavedSchedule) -> Unit,
    private val onDeleteClick: (SavedSchedule) -> Unit
) : RecyclerView.Adapter<SavedScheduleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schedule = schedules[position]
        holder.scheduleNameTextView.text = schedule.name
        holder.itemView.setOnClickListener { onItemClick(schedule) }
        holder.deleteButton.setOnClickListener { onDeleteClick(schedule) }
    }

    override fun getItemCount() = schedules.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val scheduleNameTextView: TextView = view.findViewById(R.id.schedule_name_text_view)
        val deleteButton: Button = view.findViewById(R.id.delete_schedule_button)
    }
}