package com.imsit.schedule.di

import com.imsit.schedule.data.models.DataClasses
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedStateRepository @Inject constructor() {

    // ---------- Group Preferences ----------

    private val _course = MutableStateFlow("1 курс")
    val course: StateFlow<String> = _course

    private val _speciality = MutableStateFlow("Все специальности")
    val speciality: StateFlow<String> = _speciality

    private val _group = MutableStateFlow("Выбрать")
    val group: StateFlow<String> = _group

    fun updateCourse(course: String) {
        _course.update { course }
    }

    fun updateSpeciality(speciality: String) {
        _speciality.update { speciality }
    }

    fun updateGroup(group: String) {
        _group.update { group }
    }

    // ---------- Data loading in cache ----------

    private val _groups = MutableStateFlow<HashMap<String, HashMap<String, ArrayList<DataClasses.Group>>>>(HashMap())
    val groups: StateFlow<HashMap<String, HashMap<String, ArrayList<DataClasses.Group>>>> = _groups

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress

    fun updateLoading(isLoading: Boolean) {
        _loading.update { isLoading }
    }

    fun updateProgress(progress: Int) {
        _progress.update { progress }
    }

    fun loadGroups(groups: HashMap<String, HashMap<String, ArrayList<DataClasses.Group>>>) {
        _groups.update { groups }
    }

    // ---------- Settings ----------


}