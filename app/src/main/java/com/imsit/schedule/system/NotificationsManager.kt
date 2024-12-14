package com.imsit.schedule.system

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class NotificationsManager {

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "GROUP_SYNC_CHANNEL",
                "Schedule Notifications",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for schedule work"
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

}