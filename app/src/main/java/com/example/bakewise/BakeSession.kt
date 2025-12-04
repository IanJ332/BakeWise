package com.example.bakewise

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class StepNote(
    val stepIndex: Int,
    val stepName: String,
    val note: String,
    val imageUri: String? = null
) : Parcelable

@Parcelize
data class PastLoaf(
    val recipeName: String,
    val dateBaked: Date,
    val notes: List<StepNote>
) : Parcelable

object CurrentBakeSession {
    var recipeId: Int = -1
    var recipeName: String = ""
    var scheduleName: String? = null // Added to track the active schedule
    val stepNotes = mutableListOf<StepNote>()

    fun clear() {
        recipeId = -1
        recipeName = ""
        scheduleName = null
        stepNotes.clear()
    }

    fun addNote(stepIndex: Int, stepName: String, note: String, imageUri: String?) {
        // Remove existing note for this step if any, to update it
        stepNotes.removeAll { it.stepIndex == stepIndex }
        stepNotes.add(StepNote(stepIndex, stepName, note, imageUri))
    }
}

object PastLoavesRepository {
    val loaves = mutableListOf<PastLoaf>()

    fun addLoaf(loaf: PastLoaf) {
        loaves.add(loaf)
    }
}
