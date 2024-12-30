package com.imsit.schedule.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imsit.schedule.R
import com.imsit.schedule.data.cache.CacheManager
import com.imsit.schedule.di.ResourceManager
import com.imsit.schedule.di.SharedStateRepository
import com.imsit.schedule.events.UIGroupEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val resources: ResourceManager,
    val shared: SharedStateRepository
) : ViewModel() {

    private var _groupState = MutableStateFlow(GroupState())
    val groupState: StateFlow<GroupState> = _groupState

    data class GroupState(
        val course: String = "1 курс",
        val speciality: String = "Все специальности",
        val group: String = "Выбрать",
        val showBottomSheet: Boolean = false,
        val selectedIndex: Int = 0,
        val scheduleCreation: Boolean = false,
        val coursesToDisplay: List<String> = ArrayList(),
        val specialitiesToDisplay: List<String> = ArrayList(),
        val groupsToDisplay: List<String> = ArrayList()
    )

    fun handleEvent(event: UIGroupEvent) {
        when (event) {
            is UIGroupEvent.UpdateCourse -> updateCourse(event.course)
            is UIGroupEvent.UpdateSpeciality -> updateSpeciality(event.speciality)
            is UIGroupEvent.UpdateGroup -> updateGroup(event.group)
            is UIGroupEvent.ShowBottomSheet -> toggleBottomSheet(true)
            is UIGroupEvent.HideBottomSheet -> toggleBottomSheet(false)
            is UIGroupEvent.SetSelectedIndex -> setSelectedIndex(event.index)
            is UIGroupEvent.RestoreCache -> restoreCache()
            is UIGroupEvent.CreateSchedule -> createSchedule()
            is UIGroupEvent.DisplayGroups -> displayGroups(event.course, event.speciality)
            is UIGroupEvent.DisplayCourses -> displayCourses()
            is UIGroupEvent.DisplaySpecialities -> displaySpecialities(event.course)
        }
    }

    private fun createSchedule() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _groupState.update { it.copy(scheduleCreation = createScheduleState()) }
            }
        }
    }

    private fun displayGroups(course: String, speciality: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _groupState.update { it.copy(groupsToDisplay = getGroupsToDisplay(course, speciality)) }
            }
        }
    }

    private fun displaySpecialities(course: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _groupState.update { it.copy(specialitiesToDisplay = getSpecialitiesToDisplay(course)) }
            }
        }
    }

    private fun displayCourses() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _groupState.update { it.copy(coursesToDisplay = getCoursesToDisplay()) }
            }
        }
    }

    private fun updateCourse(course: String) {
        _groupState.update { it.copy(course = course) }
        updateSpeciality(resources.getString(R.string.all_specialities))
        updateGroup(resources.getString(R.string.choose))
        updateCache()
    }

    private fun updateSpeciality(speciality: String) {
        _groupState.update { it.copy(speciality = speciality) }
        updateGroup(resources.getString(R.string.choose))
        updateCache()
    }

    private fun updateGroup(group: String) {
        _groupState.update { it.copy(group = group) }
        updateCache()
    }

    private fun setSelectedIndex(index: Int) {
        _groupState.update { it.copy(selectedIndex = index) }
    }

    private fun toggleBottomSheet(toggle: Boolean) {
        _groupState.update { it.copy(showBottomSheet = toggle) }
    }

    private fun restoreCache() {
        try {
            val cacheManager = CacheManager(resources.getContext())
            val configuration = cacheManager.loadLastConfiguration()
            if (configuration.group.isNotEmpty()) {
                _groupState.update { it.copy(
                    course = configuration.course,
                    speciality = configuration.speciality,
                    group = configuration.group
                )}
            }
        } catch (e: Exception) {
            // first-time setup or empty cache case
        }
    }

    private suspend fun createScheduleState(): Boolean {
        return withContext(Dispatchers.IO) {
            if (!groupState.value.group.contains("Выбрать")) {
                shared.updateCourse(groupState.value.course)
                shared.updateSpeciality(groupState.value.speciality)
                shared.updateGroup(groupState.value.group)
                return@withContext true
            }
            return@withContext false
        }
    }

    private suspend fun getCoursesToDisplay(): List<String> {
        return withContext(Dispatchers.IO) {
            shared.groups.value.keys.toList()
        }
    }

    private suspend fun getSpecialitiesToDisplay(courseChosen: String): List<String> {
        return withContext(Dispatchers.IO) {
            shared.groups.value[courseChosen]?.keys?.toList().orEmpty()
        }
    }

    private suspend fun getGroupsToDisplay(courseChosen: String, specialityChosen: String): List<String> {
        return withContext(Dispatchers.IO) {
            if (specialityChosen != "Все специальности") {
                shared.groups.value[courseChosen]?.get(specialityChosen)?.map { it.group }.orEmpty()
            } else {
                shared.groups.value[courseChosen]?.values?.flatten()?.map { it.group }.orEmpty()
            }
        }
    }

    private fun updateCache() {
        val cacheManager = CacheManager(resources.getContext())
        val configuration = CacheManager.Configuration(
            _groupState.value.course,
            _groupState.value.speciality,
            _groupState.value.group
        )
        cacheManager.saveActualConfiguration(configuration)
    }
}