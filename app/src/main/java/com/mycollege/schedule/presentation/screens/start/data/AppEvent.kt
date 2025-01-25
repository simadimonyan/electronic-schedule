package com.mycollege.schedule.presentation.screens.start.data

/**
 * Sealed class for managing global App events
 */
sealed class DataEvent {

    // get schedule from web
    object FetchData : DataEvent()

    // setting WorkManager processes
    object SetupCacheUpdater : DataEvent()

    // restoring cache data
    object RestoreCache : DataEvent()

}