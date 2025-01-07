package com.imsit.schedule.events

import com.imsit.schedule.data.models.DataClasses

sealed class DataEvent {
    object FetchData : DataEvent()
    object SetupCacheUpdater : DataEvent()
    object ClearNotifications : DataEvent()
    data class UpdateLoading(val isLoading: Boolean) : DataEvent()
    data class UpdateProgress(val progress: Int) : DataEvent()
    data class LoadGroups(val newGroups: HashMap<String, HashMap<String, ArrayList<DataClasses.Group>>>) : DataEvent()
    object RestoreCache : DataEvent()
}