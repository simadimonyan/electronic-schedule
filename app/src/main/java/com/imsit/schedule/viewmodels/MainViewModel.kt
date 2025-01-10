package com.imsit.schedule.viewmodels

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imsit.schedule.R
import com.imsit.schedule.data.cache.CacheManager
import com.imsit.schedule.domain.background.CacheUpdater
import com.imsit.schedule.domain.notifications.NotificationsManager
import com.imsit.schedule.domain.usecases.GetSchedule.Companion.getSchedule
import com.imsit.schedule.di.ResourceManager
import com.imsit.schedule.di.SharedStateRepository
import com.imsit.schedule.events.DataEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val resources: ResourceManager,
    val shared: SharedStateRepository
) : ViewModel() {

    fun handleEvent(event: DataEvent) {
        when (event) {
            is DataEvent.FetchData -> fetchData()
            is DataEvent.SetupCacheUpdater -> setupCacheUpdater()
            is DataEvent.ClearNotifications -> clearNotifications()
            is DataEvent.UpdateLoading -> shared.updateLoading(event.isLoading)
            is DataEvent.UpdateProgress -> shared.updateProgress(event.progress)
            is DataEvent.LoadGroups -> shared.loadGroups(event.newGroups)
            is DataEvent.RestoreCache -> restoreCache()
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("MainViewModel", "onCleared called")
        val notificationManager = resources.getContext().getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        notificationManager.cancelAll()
    }

    private fun fetchData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val context = resources.getContext()

                val cacheManager = CacheManager(context)
                val notificationsManager = NotificationsManager()

                try {
                    notificationsManager.createNotificationChannel(context)
                    if (cacheManager.shouldUpdateCache()) {
                        shared.updateLoading(true)

                        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        val notification = notificationsManager.createNotification(context, context.getString(R.string.get_data))
                        notificationManager.notify(2, notification)

                        val loadedGroups = withContext(Dispatchers.IO) {
                            getSchedule { newProgress ->
                                shared.updateProgress(newProgress)
                                notificationsManager.updateProgressNotification(2, context,
                                    newProgress
                                )
                            }
                        }

                        cacheManager.saveGroupsToCache(loadedGroups)
                        cacheManager.saveLastUpdatedTime(System.currentTimeMillis())
                        shared.loadGroups(loadedGroups)

                        notificationsManager.cancelNotification(2, context)
                        shared.updateLoading(false)
                    }
                    else {
                        shared.loadGroups(cacheManager.loadGroupsFromCache())
                    }
                } catch (e: Exception) {
                    shared.updateLoading(true)
                    e.printStackTrace()
                }
            }
        }
    }

    private fun setupCacheUpdater() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val context = resources.getContext()
                CacheUpdater.setupPeriodicWork(context)
                CacheUpdater.setupPeriodicScheduleWork(context)
            }
        }
    }

    private fun clearNotifications() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val notificationManager = resources.getContext().getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager
                notificationManager.cancel(2)
            }
        }
    }

    // global app restore
    // in main thread only | to avoid delay of loading
    private fun restoreCache() {
        try {
            val cacheManager = CacheManager(resources.getContext())
            val configuration = cacheManager.loadLastConfiguration()

            if (configuration.group.isNotEmpty()) {
                shared.updateCourse(configuration.course)
                shared.updateSpeciality(configuration.speciality)
                shared.updateGroup(configuration.group)
            }

        } catch (e: Exception) {
            // first-time setup or empty cache case
        }
    }

}