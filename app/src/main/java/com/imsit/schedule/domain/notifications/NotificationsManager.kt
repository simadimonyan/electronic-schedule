package com.imsit.schedule.domain.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.imsit.schedule.R

class NotificationsManager {

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            "GROUP_SYNC_CHANNEL",
            "Schedule Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for schedule work"
        }
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }

    fun createNotification(context: Context, message: String): Notification {
        return NotificationCompat.Builder(context, "GROUP_SYNC_CHANNEL")
            .setSmallIcon(R.drawable.noification, 0)
            .setContentTitle(context.getString(R.string.update_schedule))
            .setContentText(message)
            .setProgress(100, 0, false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSilent(true)
            .setOngoing(true)
            .build()
    }

    fun createLessonAlertNotification(context: Context, message: String): Notification {
        return NotificationCompat.Builder(context, "GROUP_SYNC_CHANNEL")
            .setSmallIcon(R.drawable.noification, 0)
            .setContentTitle(context.getString(R.string.lesson_alert))
            .setContentText(message)
            .setProgress(100, 0, false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    fun updateProgressNotification(id: Int, context: Context, progress: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val updatedNotification = NotificationCompat.Builder(context, "GROUP_SYNC_CHANNEL")
            .setSmallIcon(R.drawable.noification, 0)
            .setContentTitle(context.getString(R.string.update_schedule))
            .setContentText(context.getString(R.string.get_data))
            .setProgress(100, progress, false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSilent(true)
            .setOngoing(true)
            .build()

        notificationManager.notify(id, updatedNotification)
    }

    fun cancelNotification(id: Int, context: Context) {
        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id)
    }

}