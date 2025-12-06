package com.example.bakewise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecipeAdapter(
    private val recipes: List<Recipe>,
    private val onItemClick: (Recipe, String) -> Unit,
    private val onDetailsClick: (Recipe) -> Unit,
    private val source: String
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount() = recipes.size

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.recipe_name_text)
        private val timeTextView: TextView = itemView.findViewById(R.id.recipe_time_text)
        private val detailsButton: ImageButton = itemView.findViewById(R.id.details_arrow_button)
        private val recipeImageView: android.widget.ImageView = itemView.findViewById(R.id.recipe_image)

        fun bind(recipe: Recipe) {
            nameTextView.text = recipe.name
            
            // Display total time and active time
            val timeText = "${recipe.totalTime}\n(Active: ${recipe.activeTime})"
            timeTextView.text = timeText

            if (recipe.imageResId != 0) {
                recipeImageView.setImageResource(recipe.imageResId)
            } else {
                recipeImageView.setImageResource(android.R.color.transparent)
            }

            itemView.setOnClickListener {
                onItemClick(recipe, source)
            }

            detailsButton.setOnClickListener {
                onDetailsClick(recipe)
            }
        }
    }
}