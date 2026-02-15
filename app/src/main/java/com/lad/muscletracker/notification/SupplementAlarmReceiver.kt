package com.lad.muscletracker.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lad.muscletracker.MainActivity
import com.lad.muscletracker.MuscleTrackerApp
import com.lad.muscletracker.R

class SupplementAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val supplementName = intent.getStringExtra("supplement_name") ?: "Supplement"
        val dosage = intent.getStringExtra("dosage") ?: ""
        val reminderId = intent.getIntExtra("reminder_id", 0)

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, reminderId, tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val text = if (dosage.isNotBlank()) "$supplementName — $dosage" else supplementName

        val notification = NotificationCompat.Builder(context, MuscleTrackerApp.SUPPLEMENT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Rappel supplement")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(reminderId, notification)
        } catch (_: SecurityException) {
            // Notification permission not granted
        }
    }
}
