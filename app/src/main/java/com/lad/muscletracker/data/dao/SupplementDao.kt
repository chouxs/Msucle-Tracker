package com.lad.muscletracker.data.dao

import androidx.room.*
import com.lad.muscletracker.data.entity.Supplement
import com.lad.muscletracker.data.entity.SupplementReminder
import kotlinx.coroutines.flow.Flow

data class SupplementWithReminders(
    @Embedded val supplement: Supplement,
    @Relation(
        parentColumn = "id",
        entityColumn = "supplementId"
    )
    val reminders: List<SupplementReminder>
)

@Dao
interface SupplementDao {
    @Transaction
    @Query("SELECT * FROM supplements ORDER BY name ASC")
    fun getAllWithReminders(): Flow<List<SupplementWithReminders>>

    @Query("SELECT * FROM supplements ORDER BY name ASC")
    fun getAll(): Flow<List<Supplement>>

    @Query("SELECT * FROM supplements WHERE id = :id")
    suspend fun getSupplementById(id: Long): Supplement?

    @Insert
    suspend fun insertSupplement(supplement: Supplement): Long

    @Insert
    suspend fun insertReminder(reminder: SupplementReminder): Long

    @Update
    suspend fun updateReminder(reminder: SupplementReminder)

    @Delete
    suspend fun deleteSupplement(supplement: Supplement)

    @Delete
    suspend fun deleteReminder(reminder: SupplementReminder)

    @Query("SELECT * FROM supplement_reminders WHERE isEnabled = 1")
    suspend fun getAllEnabledReminders(): List<SupplementReminder>
}
