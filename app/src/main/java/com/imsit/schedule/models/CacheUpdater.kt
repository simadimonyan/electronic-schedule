package com.imsit.schedule.models

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.imsit.schedule.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class CacheUpdater {

    private fun calculateDelayUntilNextUpdate(lastUpdateTime: Long): Long {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastUpdate = currentTime - lastUpdateTime
        val oneDayInMillis = 24 * 60 * 60 * 1000

        return if (timeSinceLastUpdate < oneDayInMillis) {
            oneDayInMillis - timeSinceLastUpdate
        } else {
            0
        }
    }

    fun setupPeriodicWork(context: Context, lastUpdateTime: Long) {
        val delay = calculateDelayUntilNextUpdate(lastUpdateTime)

        val periodicWorkRequest = PeriodicWorkRequestBuilder<GroupSyncWorker>(delay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "GroupSyncWorker",
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest
        )
    }

}

class GroupSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val notification = createNotification("Получаем данные с сервера")
            Log.d("GroupSyncWorker", "notification created: $notification visibility: ${notification.visibility}")
            setForeground(ForegroundInfo(1, notification))

            val cacheManager = CacheManager(applicationContext)
            val schedule = Schedule()
            val loadedGroups = withContext(Dispatchers.IO) {
                Log.d("GroupSyncWorker", "loading data")
                schedule.loadData()
            }

            cacheManager.saveGroupsToCache(loadedGroups)
            cacheManager.saveLastUpdatedTime(System.currentTimeMillis())
            Log.d("GroupSyncWorker", "data cached")

            cancelNotification()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    fun createNotification(message: String): Notification {
        return NotificationCompat.Builder(applicationContext, "GROUP_SYNC_CHANNEL")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Обновление расписания")
            .setContentText(message)
            .setProgress(100, 0, true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setOngoing(true)
            .build()
    }

    fun cancelNotification() {
        val notificationManager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)
    }
}
