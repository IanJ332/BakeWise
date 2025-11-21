package com.example.bakewise

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BakeStep(
    val stepName: String,
    val hoursBeforeReady: Int, // How many hours before the "Ready Time" this step occurs
    val description: String
) : Parcelable

@Parcelize
data class Recipe(
    val id: Int,
    val name: String,
    val totalTime: String, // e.g., "30h"
    val activeTime: String, // e.g., "1h"
    val schedule: List<BakeStep>
) : Parcelable

// Our mock database
val MOCK_RECIPES = listOf(
    Recipe(
        id = 1,
        name = "Classic Sourdough",
        totalTime = "30h",
        activeTime = "1h",
        schedule = listOf(
            BakeStep("Feed starter", 28, "Feed your sourdough starter with flour and water to ensure it's active and ready for baking."),
            BakeStep("Mix Dough", 22, "Combine the flour, water, and starter to form a shaggy dough. Let it rest before adding salt."),
            BakeStep("Bulk Ferment", 21, "This is the first rise for the dough, where it will develop flavor and strength. Perform folds every 30-60 minutes."),
            BakeStep("Shape & Proof", 4, "Gently shape the dough into its final form (boule or bâtard) and place it in a proofing basket for its final rise."),
            BakeStep("Bake", 1, "Preheat your oven and Dutch oven. Score the loaf and bake until golden brown and cooked through.")
        )
    ),
    Recipe(
        id = 2,
        name = "Sourdough Baguettes",
        totalTime = "40h",
        activeTime = "1.5h",
        schedule = listOf(
            BakeStep("Feed starter", 38, "Feed your sourdough starter to get it bubbly and active."),
            BakeStep("Mix Dough", 32, "Mix flour, water, and starter. Let it autolyse (rest) for an hour before adding salt."),
            BakeStep("Bulk Ferment", 31, "Let the dough rise, performing a series of folds to build strength."),
            BakeStep("Shape & Proof", 5, "Divide and shape the dough into long baguettes. Let them proof until they are puffy."),
            BakeStep("Bake", 1, "Bake in a hot, steamy oven until the crust is golden and crisp.")
        )
    ),
    Recipe(
        id = 3,
        name = "Seeded Sourdough",
        totalTime = "36h",
        activeTime = "1.2h",
        schedule = listOf(
            BakeStep("Feed starter", 34, "Prepare your starter, ensuring it is vigorous and passes the 'float test'."),
            BakeStep("Mix Dough", 28, "Incorporate the seeds into the dough during the initial mix for even distribution."),
            BakeStep("Bulk Ferment", 27, "Allow the dough to ferment, developing its classic sour flavor."),
            BakeStep("Shape & Proof", 4, "Shape the dough and roll it in more seeds for a crusty exterior before the final proof."),
            BakeStep("Bake", 1, "Bake in a Dutch oven to trap steam, creating a great crust.")
        )
    ),
    Recipe(
        id = 4,
        name = "Whole Wheat Sourdough",
        totalTime = "32h",
        activeTime = "1h",
        schedule = listOf(
            BakeStep("Feed starter", 30, "Feed your starter. Whole wheat flour may cause it to ferment faster."),
            BakeStep("Autolyse", 24, "Combine the whole wheat flour and water for a longer autolyse to fully hydrate the bran."),
            BakeStep("Mix Dough", 23, "Add the starter and salt, and mix until the dough comes together."),
            BakeStep("Bulk Ferment", 22, "Bulk ferment, keeping an eye on the dough as whole wheat can ferment more quickly."),
            BakeStep("Shape & Proof", 6, "Shape the loaf and allow it to proof. This may take less time than with white flour."),
            BakeStep("Bake", 1, "Bake until the internal temperature reaches about 205-210°F (96-99°C).")
        )
    ),
    Recipe(
        id = 5,
        name = "Rye Sourdough",
        totalTime = "48h",
        activeTime = "0.5h",
        schedule = listOf(
            BakeStep("Feed starter", 46, "Rye starters are common. Ensure yours is active and smells sweet."),
            BakeStep("Mix Dough", 40, "Rye dough is stickier and has less gluten. Do not expect the same elasticity as wheat dough."),
            BakeStep("Bulk Ferment", 39, "The first rise. Rye ferments quickly, so watch it closely."),
            BakeStep("Shape & Proof", 12, "Shape the loaf, often in a loaf pan, as rye dough spreads more. The final proof is often long."),
            BakeStep("Bake", 1, "Bake in a preheated oven. Rye breads benefit from a longer, slower bake.")
        )
    )
)
