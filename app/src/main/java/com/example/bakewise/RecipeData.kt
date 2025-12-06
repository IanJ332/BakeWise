package com.example.bakewise

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BakeStep(
    val stepName: String,
    val hoursBeforeReady: Double, // How many hours before the "Ready Time" this step occurs
    val description: String,
    val imageResId: List<Int> = emptyList(),
    val videoResId: List<Int> = emptyList()
) : Parcelable


@Parcelize
data class Ingredient(
    val name: String,
    val amount: String // "300g", "1 tbsp", "10–15g", etc.
) : Parcelable


@Parcelize
data class Recipe(
    val id: Int,
    val name: String,
    val totalTime: String,
    val activeTime: String,
    val ingredients: List<Ingredient>,
    val schedule: List<BakeStep>,
    val imageResId: Int = 0
) : Parcelable


// ---------------------------------------------------------
// CLASSIC SOURDOUGH RECIPE
// ---------------------------------------------------------

val CLASSIC_SOURDOUGH_RECIPE = Recipe(
    id = 1,
    name = "Classic Sourdough",
    totalTime = "24h",
    activeTime = "1.75h",
    imageResId = R.drawable.bake_golden,
    ingredients = listOf(
        Ingredient("Active starter", "150g"),
        Ingredient("Water (lukewarm)", "300g"),
        Ingredient("Flour (Type 550 / bread flour)", "500g"),
        Ingredient("Salt", "12–15g")
    ),
    schedule = listOf(
        BakeStep(
            stepName = "Feed Starter",
            hoursBeforeReady = 24.0,
            description = """
                In general you want to feed your starter about 6–8 hours before you’re ready to bake. It’s tricky to time perfectly,
                so just keep an eye on it—when it has doubled in size, is at its peak, bubbly, and smells nicely tangy, you’re good to go.
                
                Start by measuring 50g of starter, 50g of water and 50g of flour and mixing everything together until reached an even consistency. 
                You may mark your container to see how much the starter has risen.
                Check on your starter every few hours to see if it has doubled in size. 

                Why: A strong, happy starter means great fermentation and a loaf that rises well instead of turning dense.
            """.trimIndent(),
            imageResId = listOf(R.drawable.mix, R.drawable.mixed, R.drawable.justmixed)

        ),
        BakeStep(
            stepName = "Autolyse",
            hoursBeforeReady = 17.0,
            description = """
                Now that we have our active starter, lets start mixing the dough. 
                
                Mix your 150g starter with the 300g lukewarm water, then add the 500g flour.
                Just stir until everything comes together - don’t worry if it’s still a bit lumpy.
                
                Now let it rest for 60 minutes.

                Why: This step is a process called Autolyse.This little break lets the flour hydrate and the gluten start forming on its own,
                which makes the dough easier to handle later.
            """.trimIndent()
        ),
        BakeStep(
            stepName = "Add Salt + First Fold",
            hoursBeforeReady = 16.0,
            description = """
                Now its time to add salt and start working the dough. Sprinkle in 12–15g of salt (Usually go with 12g for breakfast bread, 15g if want it a bit saltier).
                Wet your hands—this helps for the dough to not stick so much—and start stretching and folding the salt into the dough.
                Give it a nice stretch-and-fold around the bowl following the technique in the video below. 
                
                Now rest the dough for 30 minutes and repeat the stretch and fold.
                
                In general we want to do about 3-4 stretch-and-fold rounds until the dough is smooth and elastic. We will evaluate this in the following steps.

                Why: Salt strengthens the dough, slows fermentation a bit, and improves the flavor.
                The stretch-and-fold technique helps building the dough structure.
            """.trimIndent(),
            videoResId = listOf(R.raw.stretchfold)

        ),
        BakeStep(
            stepName = "Strengthening Fold",
            hoursBeforeReady = 15.5,
            description = """
                Now that the dough has rested, repeat the stretch-and-fold session.
                The dough should start feeling smoother, bouncier, and generally more alive.
                Timing doesn’t have to be perfect; you’ll get a feel for what your dough likes.
                
                
                Let the dough rest for another 30 minutes and repeat the stretch-and-fold for the 3rd round. 
                

                Why: These folds help build strong gluten strands so your dough can hold more air
                and rise up instead of spreading outward.
            """.trimIndent(),
            videoResId = listOf(R.raw.stretchfold)
        ),
        BakeStep(
            stepName = "Bulk Fermentation",
            hoursBeforeReady = 15.0,
            description = """
                Now that the dough has rested, repeat the stretch-and-fold session one last time and move on to the bulk fermentation stage.
                
                At this step - bulk fermentation - we let the dough rest until it looks puffier, a bit jiggly, and has grown about 30–50%.
                This usually takes 3–5 hours depending on temperature. 
                Little bubbles around the edges are a great sign.

                Why: This is where most flavor and structure develop—yeast makes gas,
                bacteria bring acidity, and the dough becomes light and airy.
            """.trimIndent(),
            videoResId = listOf(R.raw.stretchfold)
        ),
        BakeStep(
            stepName = "Preshape + Bench Rest",
            hoursBeforeReady = 11.0,
            description = """
                Gently turn the dough onto a lightly floured surface.
                Pull the edges toward the middle to form a loose ball, then flip it.
                Let it sit uncovered for about 15 minutes.

                Why: This quick rest relaxes the dough so final shaping is easier and cleaner.
            """.trimIndent()

        ),
        BakeStep(
            stepName = "Final Shape",
            hoursBeforeReady = 10.75,
            description = """
                Shape the dough into a tight ball (or a bâtard if that’s your style).
                Use your hands to pull the dough toward you to build tension.
                Pop it seam-side up into a floured banneton—or just a bowl lined with a towel.

                Why: A tight shape helps the dough rise upward in the oven
                and gives you a better bloom when you score it.
            """.trimIndent(),
            videoResId = listOf(R.raw.shaping)
        ),
        BakeStep(
            stepName = "Cold Proof",
            hoursBeforeReady = 10.25,
            description = """
                Let the dough rest in the fridge for 8–12 hours. Overnight is perfect.
                This step can be extended to up to 24h for convenience. Feel free to adjust the timer. 

                Why: Cold proofing makes the dough firmer (so scoring is easier),
                slows fermentation, and gives the bread a deeper, more complex flavor.
            """.trimIndent()
        ),
        BakeStep(
            stepName = "Take Dough Out of Fridge and Preheat Oven",
            hoursBeforeReady = 2.0,
            description = """
                The next day, preheat your oven with the Dutch oven inside to 250°C (482°F).
                Let it heat for at least 20 minutes so everything is really hot.     
            """.trimIndent()
        ),

        BakeStep(
            stepName = "Bake (Lid On)",
            hoursBeforeReady = 1.5,
            description = """
                Flip your dough onto parchment, score it (a simple long cut works great), 
                then place it into the preheated Dutch oven. Be careful as its very hot!

                Bake for 35 minutes with the lid on. This traps steam and helps the dough rise.
            """.trimIndent()
        ),

        BakeStep(
            stepName = "Bake (Lid Off)",
            hoursBeforeReady = 0.75,
            description = """
                Remove the lid and bake for another 15 minutes 
                until your crust is as dark and crispy as you like it.
            """.trimIndent()
        ),

        BakeStep(
            stepName = "Cooling – Ready to Eat",
            hoursBeforeReady = 0.5,
            description = """
                Remove the loaf from the oven and let the bread cool for at least 30 minutes.
                It’s tempting, but cutting early lets steam escape and dries the loaf out faster.

                After cooling — enjoy your sourdough!
            """.trimIndent()
        )
    )
)

val SEEDED_SOURDOUGH_RECIPE = Recipe(
    id = 2,
    name = "Seeded Sourdough",
    totalTime = "24h",
    activeTime = "1.3h",
    imageResId = R.drawable.seeded_sourdough,
    ingredients = listOf(
        Ingredient("Active starter", "150g"),
        Ingredient("Water (lukewarm)", "320g"), // +20g for seed absorption
        Ingredient("Flour (Type 550 / bread flour)", "500g"),
        Ingredient("Salt", "12–15g"),
        Ingredient("Mixed seeds (sunflower, sesame, flax, pumpkin)", "80–100g"),
        Ingredient("Hot water for seed soak", "Just enough to cover")
    ),
    schedule = listOf(
        BakeStep(
            stepName = "Feed Starter",
            hoursBeforeReady = 24.0,
            description = """
                In general you want to feed your starter about 6–8 hours before you’re ready to bake. It’s tricky to time perfectly,
                so just keep an eye on it—when it has doubled in size, is at its peak, bubbly, and smells nicely tangy, you’re good to go.
                
                Start by measuring 50g of starter, 50g of water and 50g of flour and mixing everything together until reached an even consistency. 
                You may mark your container to see how much the starter has risen.
                Check on your starter every few hours to see if it has doubled in size. 

                Why: A strong, happy starter means great fermentation and a loaf that rises well instead of turning dense.
            """.trimIndent()
        ),

        BakeStep(
            stepName = "Soak Seeds",
            hoursBeforeReady = 18.0,
            description = """
                Place your mixed seeds into a bowl and cover them with hot water. 
                Let them soak for at least 1 hour, then drain any excess water.

                Why: Soaking keeps the seeds from pulling moisture out of the dough, 
                helping maintain a good hydration level and preventing a dry loaf.
            """.trimIndent()
        ),

        BakeStep(
            stepName = "Autolyse",
            hoursBeforeReady = 17.0,
            description = """
                Now that we have our active starter, lets start mixing the dough. Mix your 150g starter with the 320g lukewarm water, then add the 500g flour.
                Just stir until everything comes together - don’t worry if it’s still a bit lumpy.
                Now let it rest for 45 minutes.

                Why: This step is a process called Autolyse.This little break lets the flour hydrate and the gluten start forming on its own,
                which makes the dough easier to handle later.
            """.trimIndent()
        ),

        BakeStep(
            stepName = "Add Salt + Seeds + First Fold",
            hoursBeforeReady = 16.0,
            description = """
                Now its time to add salt and start working the dough. Sprinkle in 12–15g of salt and add your soaked seeds.
                Wet your hands—this helps for the dough to not stick so much—and start stretching and folding everything into the dough.
                Give it a nice stretch-and-fold around the bowl following the technique in the video below. 
                
                Now rest the dough for 30 minutes and repeat the stretch and fold.
                
                In general we want to do about 3-4 stretch-and-fold rounds until the dough is smooth and elastic. We will evaluate this in the following steps.

                Why: Salt strengthens the dough, slows fermentation a bit, and improves the flavor.
                The stretch-and-fold technique helps building the dough structure and distributes the seeds evenly.
            """.trimIndent()
        ),

        // From here on, everything is identical to Classic Sourdough
        BakeStep(
            stepName = "Strengthening Fold",
            hoursBeforeReady = 15.5,
            description = CLASSIC_SOURDOUGH_RECIPE.schedule[3].description
        ),
        BakeStep(
            stepName = "Bulk Fermentation",
            hoursBeforeReady = 15.0,
            description = CLASSIC_SOURDOUGH_RECIPE.schedule[4].description
        ),
        BakeStep(
            stepName = "Preshape + Bench Rest",
            hoursBeforeReady = 11.0,
            description = CLASSIC_SOURDOUGH_RECIPE.schedule[5].description
        ),
        BakeStep(
            stepName = "Final Shape",
            hoursBeforeReady = 10.75,
            description = CLASSIC_SOURDOUGH_RECIPE.schedule[6].description
        ),
        BakeStep(
            stepName = "Cold Proof",
            hoursBeforeReady = 10.25,
            description = CLASSIC_SOURDOUGH_RECIPE.schedule[7].description
        ),
        BakeStep(
            stepName = "Take Dough Out of Fridge and Preheat Oven",
            hoursBeforeReady = 2.0,
            description = CLASSIC_SOURDOUGH_RECIPE.schedule[8].description
        ),
        BakeStep(
            stepName = "Bake (Lid On)",
            hoursBeforeReady = 1.5,
            description = CLASSIC_SOURDOUGH_RECIPE.schedule[9].description
        ),
        BakeStep(
            stepName = "Bake (Lid Off)",
            hoursBeforeReady = 0.75,
            description = CLASSIC_SOURDOUGH_RECIPE.schedule[10].description
        ),
        BakeStep(
            stepName = "Cooling – Ready to Eat",
            hoursBeforeReady = 0.5,
            description = CLASSIC_SOURDOUGH_RECIPE.schedule[11].description
        )
    )
)

val SOURDOUGH_BAGUETTES_RECIPE = Recipe(
    id = 3,
    name = "Sourdough Baguettes",
    totalTime = "18–24h",
    activeTime = "1.4h",
    imageResId = R.drawable.baguettes,
    ingredients = listOf(
        Ingredient("Active starter", "150g"),
        Ingredient("Water (lukewarm)", "350g"), // higher hydration
        Ingredient("Flour (Type 550 / bread flour)", "500g"),
        Ingredient("Salt", "12–15g")
    ),
    schedule = listOf(
        BakeStep(
            stepName = "Feed Starter",
            hoursBeforeReady = 24.0,
            description = CLASSIC_SOURDOUGH_RECIPE.schedule[0].description
        ),

        BakeStep(
            stepName = "Autolyse",
            hoursBeforeReady = 17.0,
            description = """
                Now that we have our active starter, lets start mixing the dough. Mix your 150g starter with the 350g lukewarm water, then add the 500g flour.
                Just stir until everything comes together - don’t worry if it’s still a bit lumpy.
                Now let it rest for 45 minutes.

                Why: This step is a process called Autolyse.This little break lets the flour hydrate and the gluten start forming on its own,
                which makes the dough easier to handle later.
            """.trimIndent()
        ),

        BakeStep(
            stepName = "Add Salt + First Fold",
            hoursBeforeReady = 16.0,
            description = CLASSIC_SOURDOUGH_RECIPE.schedule[2].description
        ),

        BakeStep(
            stepName = "Strengthening Fold",
            hoursBeforeReady = 15.5,
            description = CLASSIC_SOURDOUGH_RECIPE.schedule[3].description
        ),

        BakeStep(
            stepName = "Bulk Fermentation",
            hoursBeforeReady = 15.0,
            description = CLASSIC_SOURDOUGH_RECIPE.schedule[4].description
        ),

        BakeStep(
            stepName = "Preshape + Bench Rest",
            hoursBeforeReady = 11.0,
            description = CLASSIC_SOURDOUGH_RECIPE.schedule[5].description
        ),

        BakeStep(
            stepName = "Final Baguette Shape",
            hoursBeforeReady = 10.75,
            description = """
                Divide the dough into 2 or 3 equal pieces.
                Shape each into a loose cylinder, rest 10 minutes,
                then roll into long baguettes by gently elongating the dough while maintaining tension.
                Place seam-side up in a floured couche or towel.

                Why: Baguettes need a long, tight shape to get an open crumb and classic oven spring.
            """.trimIndent()
        ),

        BakeStep(
            stepName = "Cold Proof",
            hoursBeforeReady = 10.25,
            description = CLASSIC_SOURDOUGH_RECIPE.schedule[7].description
        ),

        BakeStep(
            stepName = "Preheat Oven + Steam Setup",
            hoursBeforeReady = 2.0,
            description = """
                Preheat your oven to 250°C (482°F).
                Place a baking steel or stone inside and also a tray on the bottom of the oven.

                Why: Baguettes bake on high direct heat, and adding steam helps them expand fully.
            """.trimIndent()
        ),

        BakeStep(
            stepName = "Bake with Steam",
            hoursBeforeReady = 1.5,
            description = """
                Transfer your baguettes onto parchment, score them with several diagonal cuts,
                place them onto the hot steel/stone, and pour a cup of hot water into the bottom tray to create steam.

                Bake 12–15 minutes with steam.
            """.trimIndent()
        ),

        BakeStep(
            stepName = "Bake without Steam",
            hoursBeforeReady = 1.25,
            description = """
                Open the oven door to release steam, remove the tray, 
                and continue baking for 10–12 minutes until deep golden.

                Why: Dry heat hardens the crust and gives baguettes their signature snap.
            """.trimIndent()
        ),

        BakeStep(
            stepName = "Cooling – Ready to Eat",
            hoursBeforeReady = 0.5,
            description = CLASSIC_SOURDOUGH_RECIPE.schedule[11].description
        )
    )
)



// ---------------------------------------------------------
// Mock recipe database
// ---------------------------------------------------------

val MOCK_RECIPES = listOf(
    CLASSIC_SOURDOUGH_RECIPE,
    SEEDED_SOURDOUGH_RECIPE,
    SOURDOUGH_BAGUETTES_RECIPE

)