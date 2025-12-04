package com.example.bakewise

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val stepName = intent.getStringExtra("stepName") ?: "Baking Step"
        val stepIndex = intent.getIntExtra("stepIndex", -1)
        val recipeId = intent.getIntExtra("recipeId", -1)
        val scheduleName = intent.getStringExtra("scheduleName")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "bakewise_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "BakeWise Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Create an intent to open the app when notification is clicked.
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to_step", true)
            putExtra("recipeId", recipeId)
            putExtra("stepIndex", stepIndex)
            putExtra("scheduleName", scheduleName)
        }
        
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            stepIndex, // Use unique request code per step to avoid overwriting
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("BakeWise Reminder")
            .setContentText("It's time for: $stepName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(stepIndex, notification)
    }
}
