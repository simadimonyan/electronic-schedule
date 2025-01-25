package com.mycollege.schedule.domain.notifications

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mycollege.schedule.R

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
            .setOngoing(false)
            .setSilent(true)
            .build()
    }

    fun createLessonAlertNotification(context: Context, message: String): Notification {
        return NotificationCompat.Builder(context, "GROUP_SYNC_CHANNEL")
            .setSmallIcon(R.drawable.noification, 0)
            .setContentTitle(context.getString(R.string.lesson_alert))
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(false)
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
            .setOngoing(false)
            .build()

        notificationManager.notify(id, updatedNotification)
    }

    fun cancelNotification(id: Int, context: Context) {
        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id)
    }

}

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val lesson = intent.getStringExtra("lesson")
        val manager = NotificationsManager()
        val notification = manager.createLessonAlertNotification(context, lesson.toString())
        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(lesson.hashCode(), notification)
    }

}