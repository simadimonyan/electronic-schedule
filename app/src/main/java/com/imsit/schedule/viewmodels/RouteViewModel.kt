package com.imsit.schedule.viewmodels

import androidx.lifecycle.ViewModel
import com.imsit.schedule.data.cache.CacheManager
import com.imsit.schedule.di.ResourceManager
import com.imsit.schedule.di.SharedStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    resources: ResourceManager,
    val shared: SharedStateRepository
) : ViewModel() {

    init {
        try {
            val cacheManager = CacheManager(resources.getContext())
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