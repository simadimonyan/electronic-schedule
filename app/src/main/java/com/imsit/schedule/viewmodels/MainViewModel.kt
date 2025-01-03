package com.imsit.schedule.viewmodels

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
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
import java.util.Objects
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val resources: ResourceManager,
    private val shared: SharedStateRepository
) : ViewModel() {

    fun handleEvent(event: DataEvent) {
        when (event) {
            is DataEvent.FetchData -> fetchData()
            is DataEvent.SetupCacheUpdater -> setupCacheUpdater()
            is DataEvent.ClearNotifications -> clearNotifications()
            is DataEvent.UpdateLoading -> shared.updateLoading(event.isLoading)
            is DataEvent.UpdateProgress -> shared.updateProgress(event.progress)
            is DataEvent.LoadGroups -> shared.loadGroups(event.newGroups)
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

    private fun navigate(navController: NavController, destination: Any) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                navController.navigate(destination) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

    private fun setupCacheUpdater() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val context = resources.getContext()
                CacheUpdater.setupPeriodicWork(context)
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
}