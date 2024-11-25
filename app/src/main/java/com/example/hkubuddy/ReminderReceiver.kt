package com.example.hkubuddy

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskName = intent.getStringExtra("task_name") ?: "Task Reminder"
        val taskDeadline = intent.getStringExtra("task_deadline") ?: "Unknown Date"

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Notification Channel for Android 8.0+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "task_reminder_channel",
                "Task Reminder Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Channel for Task Reminder Notifications" }
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notification = NotificationCompat.Builder(context, "task_reminder_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Task Reminder")
            .setContentText("$taskName is due on $taskDeadline. Don't forget!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Show the notification
        notificationManager.notify(taskName.hashCode(), notification)
    }
}
