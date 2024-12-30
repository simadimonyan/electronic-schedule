package com.imsit.schedule.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imsit.schedule.data.cache.CacheManager
import com.imsit.schedule.data.models.DataClasses
import com.imsit.schedule.di.ResourceManager
import com.imsit.schedule.di.SharedStateRepository
import com.imsit.schedule.domain.usecases.GetWeekCount
import com.imsit.schedule.events.UIScheduleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val resources: ResourceManager,
    private val shared: SharedStateRepository
) : ViewModel() {

    private val _todayLessons = MutableStateFlow<ArrayList<DataClasses.Lesson>>(ArrayList())
    val todayLessons: StateFlow<ArrayList<DataClasses.Lesson>> = _todayLessons

    private val _today = MutableStateFlow("")
    val today: StateFlow<String> = _today

    private val _weekLessons = MutableStateFlow<HashMap<Int, ArrayList<DataClasses.Lesson>>>(HashMap())
    val weekLessons: StateFlow<HashMap<Int, ArrayList<DataClasses.Lesson>>> = _weekLessons

    fun handleEvent(event: UIScheduleEvent) {
        when(event) {
            is UIScheduleEvent.ShowWeekLessons -> showWeekLessons()
            is UIScheduleEvent.ShowTodayLessons -> showTodayLessons()
            is UIScheduleEvent.ShowDateToday -> showDateToday()
        }
    }

    private fun showTodayLessons() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _todayLessons.update { getTodayLessons() }
            }
        }
    }

    private fun showDateToday() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _today.update { getTodayDate() }
            }
        }
    }

    private fun showWeekLessons() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _weekLessons.update { getWeekLessonsByGroup() }
            }
        }
    }

    private suspend fun getWeekLessonsByGroup(): HashMap<Int, ArrayList<DataClasses.Lesson>> {
        return withContext(Dispatchers.Default) {
            val cacheManager = CacheManager(resources.getContext())

            val groups: ArrayList<DataClasses.Group>? =
                cacheManager.loadGroupsFromCache()[shared.course.value]?.get(
                    when {
                        shared.group.value.contains("СПО") -> "СПО"
                        shared.group.value.contains("Мг") -> "Магистратура"
                        else -> "Бакалавриат"
                    }
                )

            var chosenGroup: DataClasses.Group? = null
            val count = GetWeekCount.calculateCount()

            if (groups != null) {
                for (group in groups.iterator()) {
                    if (group.group == shared.group.value) {
                        chosenGroup = group
                        break
                    }
                }
            }

            try {
                if (chosenGroup != null) {
                    return@withContext if (count == 0) chosenGroup.lessons?.weekEven!! else chosenGroup.lessons?.weekOdd!!
                }
            } catch (e: NullPointerException) {
                return@withContext HashMap()
            }
            return@withContext HashMap()
        }
    }

    private suspend fun getTodayLessons(): ArrayList<DataClasses.Lesson> {
        return withContext(Dispatchers.Default) {
            val week: HashMap<Int, ArrayList<DataClasses.Lesson>> = getWeekLessonsByGroup()

            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH)
            val dayWeek = currentDate.format(formatter).uppercase()

            for (day in week.keys) {
                if (DataClasses.DayWeek.findById(day)?.name == dayWeek) {
                    return@withContext week[day] as ArrayList<DataClasses.Lesson>
                }
            }
            return@withContext ArrayList()
        }
    }

    suspend fun getTodayDate(): String {
        return withContext(Dispatchers.IO) {
            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM", Locale("RU"))
            return@withContext currentDate.format(formatter).replaceFirstChar { it.uppercase() }
        }
    }

//    fun getTodayWeekCount(): Int {
//        return if (GetWeekCount.calculateCount() == 0) 2 else 1
//    }

}

