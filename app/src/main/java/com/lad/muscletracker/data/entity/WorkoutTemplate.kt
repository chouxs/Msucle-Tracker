package com.lad.muscletracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_templates")
data class WorkoutTemplate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val dayOfWeek: Int, // 1=Monday..7=Sunday, 0=any
    val description: String = "",
    val isDefault: Boolean = true
)
