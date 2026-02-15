package com.lad.muscletracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val muscleGroup: String,
    val isCustom: Boolean = false,
    val targetSets: Int = 3,
    val targetRepsMin: Int = 6,
    val targetRepsMax: Int = 12,
    val restSeconds: Int = 90,
    val exerciseType: String = "compound", // "compound" or "isolation"
    val dayType: String = "",              // "push", "pull", "legs", "upper", "lower", "fullbody"
    val isActive: Boolean = true
)
