package com.example.bakewise

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScheduleItem(
    val whenMillis: Long,
    val bakeStep: BakeStep
) : Parcelable
