package com.example.bakewise

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SavedSchedule(
    val name: String,
    val recipeName: String,
    val scheduleDetails: List<BakeStep>
) : Parcelable
