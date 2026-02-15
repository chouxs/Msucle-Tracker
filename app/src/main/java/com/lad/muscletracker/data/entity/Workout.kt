package com.lad.muscletracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val date: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
    val isCompleted: Boolean = false,
    val templateId: Long? = null
)
