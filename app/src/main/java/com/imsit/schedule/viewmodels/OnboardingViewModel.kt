package com.imsit.schedule.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imsit.schedule.data.cache.CacheManager
import com.imsit.schedule.di.ResourceManager
import com.imsit.schedule.di.SharedStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    val resources: ResourceManager,
    val shared: SharedStateRepository
) : ViewModel() {

    fun setFirstStartup() {
        viewModelScope.launch {
            val cacheManager = CacheManager(resources.getContext())
            shared.updatingFirstStartup(false)
            cacheManager.setFirstStartup(false)
        }
    }

}