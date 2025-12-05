package com.example.bakewise

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.bakewise.databinding.ItemLoafNoteBinding

class LoafDetailsAdapter(
    private val notes: List<StepNote>,
    private val onItemClick: ((StepNote) -> Unit)? = null
) : RecyclerView.Adapter<LoafDetailsAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemLoafNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: StepNote) {
            binding.root.setOnClickListener { onItemClick?.invoke(note) }
            binding.stepNameTextView.text = note.stepName
            binding.noteTextView.text = note.note
            
            if (note.imageUri != null) {
                binding.noteImageView.isVisible = true
                try {
                    binding.noteImageView.setImageURI(Uri.parse(note.imageUri))
                } catch (e: Exception) {
                    // Handle potential URI permissions or loading errors
                    e.printStackTrace()
                }
            } else {
                binding.noteImageView.isVisible = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLoafNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount() = notes.size
}