package com.lad.muscletracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: Long? = null,
    val title: String,
    val targetWeight: Float? = null,
    val targetReps: Int? = null,
    val isAchieved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val achievedAt: Long? = null
)
