package com.lad.muscletracker.data.dao

import androidx.room.*
import com.lad.muscletracker.data.entity.Exercise
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises WHERE isActive = 1 ORDER BY muscleGroup, name")
    fun getAllExercises(): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE muscleGroup = :group AND isActive = 1 ORDER BY name")
    fun getExercisesByGroup(group: String): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE dayType LIKE '%' || :dayType || '%' AND isActive = 1 ORDER BY exerciseType DESC, name")
    fun getExercisesByDayType(dayType: String): Flow<List<Exercise>>

    @Query("SELECT DISTINCT muscleGroup FROM exercises WHERE isActive = 1 ORDER BY muscleGroup")
    fun getAllMuscleGroups(): Flow<List<String>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getById(id: Long): Exercise?

    @Query("SELECT * FROM exercises WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): Exercise?

    @Insert
    suspend fun insert(exercise: Exercise): Long

    @Insert
    suspend fun insertAll(exercises: List<Exercise>)

    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun count(): Int

    @Query("UPDATE exercises SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long)

    @Query("SELECT * FROM exercises WHERE isFavorite = 1 AND isActive = 1 ORDER BY muscleGroup, name")
    fun getFavoriteExercises(): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE isFavorite = 1 AND muscleGroup = :group AND isActive = 1 ORDER BY name")
    suspend fun getFavoritesByGroup(group: String): List<Exercise>
}
