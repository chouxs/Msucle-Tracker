package com.lad.muscletracker.data.dao

import androidx.room.*
import com.lad.muscletracker.data.entity.CardioSession
import kotlinx.coroutines.flow.Flow

@Dao
interface CardioDao {
    @Query("SELECT * FROM cardio_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<CardioSession>>

    @Query("SELECT SUM(caloriesBurned) FROM cardio_sessions WHERE date >= :startMillis AND date <= :endMillis")
    fun getWeeklyCaloriesBurned(startMillis: Long, endMillis: Long): Flow<Int?>

    @Query("SELECT SUM(distanceKm) FROM cardio_sessions WHERE date >= :startMillis AND date <= :endMillis")
    fun getWeeklyDistance(startMillis: Long, endMillis: Long): Flow<Float?>

    @Insert
    suspend fun insert(session: CardioSession): Long

    @Delete
    suspend fun delete(session: CardioSession)
}
