package com.imsit.schedule.events

import androidx.compose.animation.core.Spring

sealed class UIGroupEvent {
    data class UpdateCourse(val course: String) : UIGroupEvent()
    data class UpdateSpeciality(val speciality: String) : UIGroupEvent()
    data class UpdateGroup(val group: String) : UIGroupEvent()
    object ShowBottomSheet : UIGroupEvent()
    object HideBottomSheet : UIGroupEvent()
    data class SetSelectedIndex(val index: Int) : UIGroupEvent()
    object RestoreCache : UIGroupEvent()
    object CreateSchedule : UIGroupEvent()
    data class DisplayGroups(val course: String, val speciality: String) : UIGroupEvent()
}