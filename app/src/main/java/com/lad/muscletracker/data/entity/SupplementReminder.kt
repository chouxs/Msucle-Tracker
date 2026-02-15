package com.lad.muscletracker.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "supplement_reminders",
    foreignKeys = [
        ForeignKey(
            entity = Supplement::class,
            parentColumns = ["id"],
            childColumns = ["supplementId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("supplementId")]
)
data class SupplementReminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val supplementId: Long,
    val timeLabel: String = "",
    val hour: Int,
    val minute: Int,
    val isEnabled: Boolean = true
)
