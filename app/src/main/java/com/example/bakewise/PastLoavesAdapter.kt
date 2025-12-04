package com.example.bakewise

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bakewise.databinding.ItemPastLoafBinding
import java.text.SimpleDateFormat
import java.util.Locale

class PastLoavesAdapter(
    private val loaves: List<PastLoaf>,
    private val onItemClick: (PastLoaf) -> Unit
) : RecyclerView.Adapter<PastLoavesAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemPastLoafBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(loaf: PastLoaf) {
            binding.recipeNameTextView.text = loaf.recipeName
            
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            binding.dateBakedTextView.text = dateFormat.format(loaf.dateBaked)
            
            val notesCount = loaf.notes.size
            binding.notesSummaryTextView.text = "$notesCount notes recorded"

            binding.root.setOnClickListener {
                onItemClick(loaf)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPastLoafBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(loaves[position])
    }

    override fun getItemCount() = loaves.size
}