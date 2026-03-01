package com.lad.muscletracker.data.dao

import androidx.room.*
import com.lad.muscletracker.data.entity.Workout
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getAllWorkouts(): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: Long): Workout?

    @Query("SELECT * FROM workouts ORDER BY date DESC LIMIT 1")
    fun getLastWorkout(): Flow<Workout?>

    @Query("SELECT COUNT(*) FROM workouts WHERE isCompleted = 1 AND date >= :weekStartMillis AND date <= :weekEndMillis")
    fun getWeeklyWorkoutCount(weekStartMillis: Long, weekEndMillis: Long): Flow<Int>

    @Insert
    suspend fun insert(workout: Workout): Long

    @Update
    suspend fun update(workout: Workout)

    @Delete
    suspend fun delete(workout: Workout)

    @Query("SELECT date FROM workouts WHERE isCompleted = 1 ORDER BY date DESC")
    fun getAllWorkoutDates(): Flow<List<Long>>

    @Query("SELECT * FROM workouts ORDER BY date DESC")
    suspend fun getAllWorkoutsOnce(): List<Workout>

    @Query("SELECT * FROM workouts WHERE date >= :monthStart AND date < :monthEnd ORDER BY date ASC")
    fun getWorkoutsByMonth(monthStart: Long, monthEnd: Long): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE isCompleted = 0 ORDER BY date DESC LIMIT 1")
    suspend fun getUnfinishedWorkout(): Workout?
}
