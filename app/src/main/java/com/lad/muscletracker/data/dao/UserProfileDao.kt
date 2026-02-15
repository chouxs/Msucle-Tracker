package com.lad.muscletracker.data.dao

import androidx.room.*
import com.lad.muscletracker.data.entity.UserProfile
import com.lad.muscletracker.data.entity.WeightEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getProfileOnce(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfile)

    @Query("SELECT * FROM weight_entries ORDER BY date DESC")
    fun getAllWeightEntries(): Flow<List<WeightEntry>>

    @Query("SELECT * FROM weight_entries ORDER BY date DESC LIMIT 1")
    fun getLatestWeight(): Flow<WeightEntry?>

    @Insert
    suspend fun insertWeightEntry(entry: WeightEntry): Long

    @Delete
    suspend fun deleteWeightEntry(entry: WeightEntry)
}
