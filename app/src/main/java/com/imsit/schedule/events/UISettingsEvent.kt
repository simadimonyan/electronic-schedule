package com.imsit.schedule.events

import com.imsit.schedule.data.cache.CacheManager

sealed class UISettingsEvent {
    data class MakeScheduleWeekFull(val isFull: Boolean) : UISettingsEvent()
    data class MakeNavigationInvisible(val isVisible: Boolean) : UISettingsEvent()
    object SaveSettings : UISettingsEvent()
}