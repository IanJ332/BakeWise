package com.example.bakewise

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings

object NotificationScheduler {

    fun scheduleNotifications(context: Context, scheduleItems: List<ScheduleItem>, recipeId: Int, scheduleName: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                return
            }
        }

        scheduleItems.forEachIndexed { index, item ->
            // Schedule if the time is in the future OR recently past (within last 15 minutes)
            if (item.whenMillis > System.currentTimeMillis() - 15 * 60 * 1000) {
                val intent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra("stepName", item.bakeStep.stepName)
                    putExtra("stepIndex", index)
                    putExtra("recipeId", recipeId)
                    putExtra("scheduleName", scheduleName)
                }

                val requestCode = (item.whenMillis % Int.MAX_VALUE).toInt()

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                try {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        item.whenMillis,
                        pendingIntent
                    )
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
