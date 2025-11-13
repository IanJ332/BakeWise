package com.example.bakewise

// A data class to represent a single step in the baking process
data class BakeStep(
    val stepName: String,
    val hoursBeforeReady: Int // How many hours before the "Ready Time" this step occurs
)

// A data class for the recipe itself
data class Recipe(
    val id: Int,
    val name: String,
    val totalTime: String, // e.g., "30h" (for Design Idea #1)
    val schedule: List<BakeStep>
)

// Our mock database
val MOCK_RECIPES = listOf(
    Recipe(
        id = 1,
        name = "Classic Sourdough",
        totalTime = "30h",
        schedule = listOf(
            BakeStep("Feed starter", 28),
            BakeStep("Mix Dough", 22),
            BakeStep("Bulk Ferment", 21),
            BakeStep("Shape & Proof", 4),
            BakeStep("Bake", 1)
        )
    ),
    Recipe(
        id = 2,
        name = "Sourdough Baguettes",
        totalTime = "40h",
        schedule = listOf(
            BakeStep("Feed starter", 38),
            BakeStep("Mix Dough", 32),
            BakeStep("Bulk Ferment", 31),
            BakeStep("Shape & Proof", 5),
            BakeStep("Bake", 1)
        )
    ),
    Recipe(
        id = 3,
        name = "Seeded Sourdough",
        totalTime = "36h",
        schedule = listOf(
            BakeStep("Feed starter", 34),
            BakeStep("Mix Dough", 28),
            BakeStep("Bulk Ferment", 27),
            BakeStep("Shape & Proof", 4),
            BakeStep("Bake", 1)
        )
    )
)
