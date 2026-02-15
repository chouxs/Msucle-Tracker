package com.lad.muscletracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MuscleTrackerApp : Application() {

    companion object {
        const val SUPPLEMENT_CHANNEL_ID = "supplement_reminders"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SUPPLEMENT_CHANNEL_ID,
                "Rappels supplements",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications de rappel pour prendre vos supplements"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
