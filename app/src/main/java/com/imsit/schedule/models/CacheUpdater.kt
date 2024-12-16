package com.imsit.schedule.models

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.imsit.schedule.viewmodels.NotificationsManager.Companion.cancelNotification
import com.imsit.schedule.viewmodels.NotificationsManager.Companion.createNotification
import com.imsit.schedule.viewmodels.NotificationsManager.Companion.updateProgressNotification
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

@Suppress("SameParameterValue")
class GroupSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            if (CacheManager(applicationContext).getLastUpdatedTime() != 0L) {
                val notification = createNotification(applicationContext,"Получаем данные с сервера")
                Log.d("GroupSyncWorker", "notification created: $notification visibility: ${notification.visibility}")
                setForeground(ForegroundInfo(1, notification))

                val cacheManager = CacheManager(applicationContext)
                val schedule = Schedule()
                val loadedGroups = withContext(Dispatchers.IO) {
                    Log.d("GroupSyncWorker", "loading data")
                    schedule.loadData { newProgress ->
                        updateProgressNotification(1, applicationContext, newProgress) // Update progress
                    }
                }

                cacheManager.saveGroupsToCache(loadedGroups)
                cacheManager.saveLastUpdatedTime(System.currentTimeMillis())
                Log.d("GroupSyncWorker", "data cached")

                cancelNotification(1, applicationContext)
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

}
