package com.lad.muscletracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cardio_sessions")
data class CardioSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long = System.currentTimeMillis(),
    val type: String,
    val distanceKm: Float,
    val durationMinutes: Int,
    val inclinePercent: Float = 0f,
    val caloriesBurned: Int = 0,
    val avgSpeedKmh: Float = 0f,
    val notes: String = ""
)
