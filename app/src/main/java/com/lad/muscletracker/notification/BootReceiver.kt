package com.lad.muscletracker.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lad.muscletracker.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            rescheduleAllReminders(context)
        }
    }

    private fun rescheduleAllReminders(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            val enabledReminders = db.supplementDao().getAllEnabledReminders()

            for (reminder in enabledReminders) {
                val supplement = db.supplementDao().getSupplementById(reminder.supplementId)
                if (supplement != null) {
                    SupplementAlarmScheduler.scheduleReminder(
                        context,
                        reminder,
                        supplement.name,
                        supplement.dosage
                    )
                }
            }
        }
    }
}
