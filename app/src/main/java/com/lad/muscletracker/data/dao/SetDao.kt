package com.lad.muscletracker.data.dao

import androidx.room.*
import com.lad.muscletracker.data.entity.WorkoutSet
import kotlinx.coroutines.flow.Flow

data class SetWithExerciseName(
    val id: Long,
    val workoutId: Long,
    val exerciseId: Long,
    val exerciseName: String,
    val muscleGroup: String,
    val setNumber: Int,
    val weight: Float,
    val reps: Int,
    val setType: String = "working"
)

data class ExerciseProgress(
    val date: Long,
    val maxWeight: Float,
    val totalVolume: Float
)

data class WeeklyMuscleVolume(
    val muscleGroup: String,
    val totalSets: Int,
    val totalTonnage: Float
)

data class LastPerformance(
    val weight: Float,
    val reps: Int
)

data class ExerciseCoachData(
    val exerciseId: Long,
    val exerciseName: String,
    val muscleGroup: String,
    val lastWeight: Float,
    val lastReps: Int,
    val lastDate: Long
)

data class PersonalRecord(
    val exerciseId: Long,
    val exerciseName: String,
    val maxWeight: Float,
    val maxWeightReps: Int,
    val achievedDate: Long
)

@Dao
interface SetDao {
    @Query("""
        SELECT ws.id, ws.workoutId, ws.exerciseId, e.name as exerciseName,
               e.muscleGroup, ws.setNumber, ws.weight, ws.reps, ws.setType
        FROM workout_sets ws
        INNER JOIN exercises e ON ws.exerciseId = e.id
        WHERE ws.workoutId = :workoutId
        ORDER BY ws.id ASC
    """)
    fun getSetsForWorkout(workoutId: Long): Flow<List<SetWithExerciseName>>

    @Query("""
        SELECT w.date, MAX(ws.weight) as maxWeight, SUM(ws.weight * ws.reps) as totalVolume
        FROM workout_sets ws
        INNER JOIN workouts w ON ws.workoutId = w.id
        WHERE ws.exerciseId = :exerciseId
        GROUP BY ws.workoutId
        ORDER BY w.date ASC
    """)
    fun getProgressForExercise(exerciseId: Long): Flow<List<ExerciseProgress>>

    @Query("""
        SELECT e.muscleGroup, COUNT(ws.id) as totalSets, SUM(ws.weight * ws.reps) as totalTonnage
        FROM workout_sets ws
        INNER JOIN exercises e ON ws.exerciseId = e.id
        INNER JOIN workouts w ON ws.workoutId = w.id
        WHERE w.date >= :weekStartMillis AND w.date <= :weekEndMillis
        GROUP BY e.muscleGroup
    """)
    fun getWeeklyVolumeByMuscle(weekStartMillis: Long, weekEndMillis: Long): Flow<List<WeeklyMuscleVolume>>

    @Query("""
        SELECT ws.weight, ws.reps FROM workout_sets ws
        INNER JOIN workouts w ON ws.workoutId = w.id
        WHERE ws.exerciseId = :exerciseId AND w.isCompleted = 1
        ORDER BY w.date DESC, ws.setNumber ASC
        LIMIT :setCount
    """)
    suspend fun getLastPerformance(exerciseId: Long, setCount: Int = 4): List<LastPerformance>

    @Insert
    suspend fun insert(set: WorkoutSet): Long

    @Delete
    suspend fun delete(set: WorkoutSet)

    @Query("DELETE FROM workout_sets WHERE id = :setId")
    suspend fun deleteById(setId: Long)

    @Query("UPDATE workout_sets SET weight = :weight, reps = :reps, setType = :setType WHERE id = :setId")
    suspend fun updateSet(setId: Long, weight: Float, reps: Int, setType: String = "working")

    @Query("""
        SELECT e.id as exerciseId, e.name as exerciseName, e.muscleGroup,
               ws.weight as lastWeight, ws.reps as lastReps, w.date as lastDate
        FROM workout_sets ws
        INNER JOIN exercises e ON ws.exerciseId = e.id
        INNER JOIN workouts w ON ws.workoutId = w.id
        WHERE w.isCompleted = 1
        AND ws.id = (
            SELECT ws2.id FROM workout_sets ws2
            INNER JOIN workouts w2 ON ws2.workoutId = w2.id
            WHERE ws2.exerciseId = e.id AND w2.isCompleted = 1
            ORDER BY w2.date DESC, ws2.setNumber DESC
            LIMIT 1
        )
        GROUP BY e.id
        ORDER BY w.date DESC
    """)
    fun getAllExercisesWithLastPerformance(): Flow<List<ExerciseCoachData>>

    @Query("""
        SELECT ws.exerciseId, e.name as exerciseName,
               MAX(ws.weight) as maxWeight, ws.reps as maxWeightReps, w.date as achievedDate
        FROM workout_sets ws
        INNER JOIN exercises e ON ws.exerciseId = e.id
        INNER JOIN workouts w ON ws.workoutId = w.id
        WHERE w.isCompleted = 1
        GROUP BY ws.exerciseId
        ORDER BY maxWeight DESC
    """)
    fun getAllPersonalRecords(): Flow<List<PersonalRecord>>

    @Query("SELECT COALESCE(SUM(ws.weight * ws.reps), 0) FROM workout_sets ws INNER JOIN workouts w ON ws.workoutId = w.id WHERE w.isCompleted = 1")
    fun getTotalVolume(): Flow<Float>

    @Query("""
        SELECT ws.id, ws.workoutId, ws.exerciseId, e.name as exerciseName,
               e.muscleGroup, ws.setNumber, ws.weight, ws.reps, ws.setType
        FROM workout_sets ws
        INNER JOIN exercises e ON ws.exerciseId = e.id
        ORDER BY ws.workoutId, ws.id ASC
    """)
    suspend fun getAllSetsOnce(): List<SetWithExerciseName>
}
