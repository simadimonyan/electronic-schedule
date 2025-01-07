package com.imsit.schedule.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imsit.schedule.data.cache.CacheManager
import com.imsit.schedule.di.ResourceManager
import com.imsit.schedule.di.SharedStateRepository
import com.imsit.schedule.events.UISettingsEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val resources: ResourceManager,
    val shared: SharedStateRepository
) : ViewModel() {

    fun handleEvent(event: UISettingsEvent) {
        when(event) {
            is UISettingsEvent.MakeNavigationInvisible -> makeNavInvisible(event.isVisible)
            is UISettingsEvent.SaveSettings -> saveSettings()
            is UISettingsEvent.MakeScheduleWeekFull -> makeScheduleFullWeek(event.isFull)
        }
    }

    private fun saveSettings() {
        val cache = CacheManager(resources.getContext())
        cache.saveActualSettings(CacheManager.Settings(shared.scheduleFullWeek.value, shared.navigationInvisibility.value))
    }

    private fun makeScheduleFullWeek(isFull: Boolean) {
        viewModelScope.launch {
            shared.updateFullWeek(isFull)
        }
    }

    private fun makeNavInvisible(isVisible: Boolean) {
        viewModelScope.launch {
            shared.updateNavInvisibility(isVisible)
        }
    }

}