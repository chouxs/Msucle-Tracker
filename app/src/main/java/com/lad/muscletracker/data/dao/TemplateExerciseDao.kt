package com.lad.muscletracker.data.dao

import androidx.room.*
import com.lad.muscletracker.data.entity.TemplateExercise
import kotlinx.coroutines.flow.Flow

data class TemplateExerciseWithDetails(
    val id: Long,
    val templateId: Long,
    val exerciseId: Long,
    val exerciseName: String,
    val muscleGroup: String,
    val exerciseType: String,
    val restSeconds: Int,
    val sortOrder: Int,
    val targetSets: Int,
    val targetRepsMin: Int,
    val targetRepsMax: Int
)

@Dao
interface TemplateExerciseDao {
    @Query("""
        SELECT te.id, te.templateId, te.exerciseId, e.name as exerciseName,
               e.muscleGroup, e.exerciseType, e.restSeconds,
               te.sortOrder, te.targetSets, te.targetRepsMin, te.targetRepsMax
        FROM template_exercises te
        INNER JOIN exercises e ON te.exerciseId = e.id
        WHERE te.templateId = :templateId
        ORDER BY te.sortOrder ASC
    """)
    fun getExercisesForTemplate(templateId: Long): Flow<List<TemplateExerciseWithDetails>>

    @Insert
    suspend fun insert(templateExercise: TemplateExercise): Long

    @Insert
    suspend fun insertAll(templateExercises: List<TemplateExercise>)

    @Delete
    suspend fun delete(templateExercise: TemplateExercise)
}
