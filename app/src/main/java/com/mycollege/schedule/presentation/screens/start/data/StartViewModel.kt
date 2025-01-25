package com.mycollege.schedule.presentation.screens.start.data

import androidx.lifecycle.ViewModel
import com.mycollege.schedule.data.cache.CacheManager
import com.mycollege.schedule.presentation.repository.SharedStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val cacheManager: CacheManager,
    val shared: SharedStateRepository
) : ViewModel() {

    fun init() {
        try {
            val settings = cacheManager.loadLastSettings()

            // UI Screen Index
            if (settings.isNavInvisible) {
                shared.updateIndex(1)
            }

            // Settings
            shared.updateFullWeek(settings.fullWeek)
            shared.updateNavInvisibility(settings.isNavInvisible)
        }
        catch (_: Exception) {}
    }

}