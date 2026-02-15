package com.lad.muscletracker.data.dao

import androidx.room.*
import com.lad.muscletracker.data.entity.WorkoutTemplate
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {
    @Query("SELECT * FROM workout_templates ORDER BY dayOfWeek ASC")
    fun getAllTemplates(): Flow<List<WorkoutTemplate>>

    @Query("SELECT * FROM workout_templates WHERE dayOfWeek = :dayOfWeek OR dayOfWeek = 0 ORDER BY dayOfWeek DESC")
    fun getTemplatesForDay(dayOfWeek: Int): Flow<List<WorkoutTemplate>>

    @Query("SELECT * FROM workout_templates WHERE id = :id")
    suspend fun getById(id: Long): WorkoutTemplate?

    @Insert
    suspend fun insert(template: WorkoutTemplate): Long

    @Insert
    suspend fun insertAll(templates: List<WorkoutTemplate>)

    @Update
    suspend fun update(template: WorkoutTemplate)

    @Delete
    suspend fun delete(template: WorkoutTemplate)
}
