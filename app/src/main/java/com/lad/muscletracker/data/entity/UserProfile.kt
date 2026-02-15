package com.lad.muscletracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Long = 1,
    val weightKg: Float = 70f,
    val heightCm: Float = 175f,
    val age: Int = 25,
    val gender: String = "male",
    val activityLevel: String = "moderate",
    val goal: String = "maintenance"
)
