package com.mycollege.schedule.presentation.screens.settings.data

import androidx.lifecycle.ViewModel
import com.mycollege.schedule.data.cache.CacheManager
import com.mycollege.schedule.presentation.repository.SharedStateRepository
import com.mycollege.schedule.presentation.screens.settings.data.SettingsEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val cacheManager: CacheManager,
    val shared: SharedStateRepository
) : ViewModel() {

    fun handleEvent(event: SettingsEvent) {
        when(event) {
            is SettingsEvent.MakeNavigationInvisible -> makeNavInvisible(event.isVisible)
            is SettingsEvent.SaveSettings -> saveSettings()
            is SettingsEvent.MakeScheduleWeekFull -> makeScheduleFullWeek(event.isFull)
        }
    }

    private fun saveSettings() {
        cacheManager.saveActualSettings(CacheManager.Settings(shared.scheduleFullWeek.value, shared.navigationInvisibility.value))
    }

    private fun makeScheduleFullWeek(isFull: Boolean) {
        shared.updateFullWeek(isFull)
    }

    private fun makeNavInvisible(isVisible: Boolean) {
        shared.updateNavInvisibility(isVisible)
    }

}