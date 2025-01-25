package com.mycollege.schedule.presentation.screens.settings.data

/**
 * Sealed class for managing Settings events
 */
sealed class SettingsEvent {

    // switch settings
    data class MakeScheduleWeekFull(val isFull: Boolean) : SettingsEvent()
    data class MakeNavigationInvisible(val isVisible: Boolean) : SettingsEvent()

    // save in cache
    object SaveSettings : SettingsEvent()

}