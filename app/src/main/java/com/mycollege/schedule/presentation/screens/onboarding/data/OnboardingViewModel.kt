package com.mycollege.schedule.presentation.screens.onboarding.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycollege.schedule.data.cache.CacheManager
import com.mycollege.schedule.presentation.repository.SharedStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val cacheManager: CacheManager,
    val shared: SharedStateRepository
) : ViewModel() {

    fun setFirstStartup() {
        viewModelScope.launch {
            shared.updatingFirstStartup(false)
            cacheManager.setFirstStartup(false)
        }
    }

}