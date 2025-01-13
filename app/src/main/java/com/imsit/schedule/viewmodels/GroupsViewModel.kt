package com.imsit.schedule.viewmodels

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imsit.schedule.R
import com.imsit.schedule.data.cache.CacheManager
import com.imsit.schedule.data.models.DataClasses
import com.imsit.schedule.di.ResourceManager
import com.imsit.schedule.di.SharedStateRepository
import com.imsit.schedule.domain.notifications.NotificationReceiver
import com.imsit.schedule.domain.usecases.GetWeekCount
import com.imsit.schedule.events.UIGroupEvent
import com.imsit.schedule.events.UIScheduleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale
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
        val groupsToDisplay: List<String> = ArrayList(),
        val weekDates: HashMap<Int, String> = HashMap()
    )

    init {

        // subscribe UI state on external groups loading
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                shared.groups.collect { newGroups ->
                    _groupState.update { currentState ->
                        val allGroups = mutableListOf<String>()

                        newGroups[currentState.course]?.keys?.let { specialities ->
                            newGroups[currentState.course]?.forEach { (_, groups) ->

                                // all groups of all specialities by default
                                groups.forEach { group ->
                                    allGroups.add(group.group)
                                }
                            }

                            // change state of groups to display by defaults when loading finished
                            currentState.copy(
                                coursesToDisplay = newGroups.keys.toList(),
                                specialitiesToDisplay = specialities.toList(),
                                groupsToDisplay = allGroups
                            )
                        } ?: currentState
                    }
                }
            }
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // get last chosen configuration
                restoreCache()
            }
        }

    }

    fun handleEvent(event: UIGroupEvent) {
        when (event) {
            is UIGroupEvent.UpdateCourse -> updateCourse(event.course)
            is UIGroupEvent.UpdateSpeciality -> updateSpeciality(event.speciality)
            is UIGroupEvent.UpdateGroup -> updateGroup(event.group)
            is UIGroupEvent.ShowBottomSheet -> toggleBottomSheet(true)
            is UIGroupEvent.HideBottomSheet -> toggleBottomSheet(false)
            is UIGroupEvent.SetSelectedIndex -> setSelectedIndex(event.index)
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
    }

    private fun updateSpeciality(speciality: String) {
        _groupState.update { it.copy(speciality = speciality) }
        updateGroup(resources.getString(R.string.choose))
    }

    private fun updateGroup(group: String) {
        _groupState.update { it.copy(group = group) }
    }

    private fun setSelectedIndex(index: Int) {
        _groupState.update { it.copy(selectedIndex = index) }
    }

    private fun toggleBottomSheet(toggle: Boolean) {
        _groupState.update { it.copy(showBottomSheet = toggle) }
    }

    private fun updateWeekDates() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _groupState.update { it.copy(weekDates = getCurrentWeekDate()) }
            }
        }
    }

    // screen variables init
    // in main thread only | to avoid delay of loading
    private fun restoreCache() {
        try {
            showDateToday()

            val cacheManager = CacheManager(resources.getContext())
            val configuration = cacheManager.loadLastConfiguration()
            if (configuration.group.isNotEmpty()) {
                _groupState.update { it.copy(
                    course = configuration.course,
                    speciality = configuration.speciality,
                    group = configuration.group
                )}
            }
            showTodayLessons()
        } catch (e: Exception) {
            // first-time setup or empty cache case
        }
    }

    private fun createScheduleState(): Boolean {
        if (!groupState.value.group.contains("Выбрать")) {
            shared.updateCourse(groupState.value.course)
            shared.updateSpeciality(groupState.value.speciality)
            shared.updateGroup(groupState.value.group)

            // save configuration on schedule create only
            updateCache()

            showTodayLessons()
            return true
        }
        return false
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



    // --------------------------------------------




    private val _todayLessons = MutableStateFlow<ArrayList<DataClasses.Lesson>>(ArrayList())
    val todayLessons: StateFlow<ArrayList<DataClasses.Lesson>> = _todayLessons

    private val _today = MutableStateFlow("")
    val today: StateFlow<String> = _today

    private val _weekLessons = MutableStateFlow<HashMap<Int, ArrayList<DataClasses.Lesson>>>(HashMap())
    val weekLessons: StateFlow<HashMap<Int, ArrayList<DataClasses.Lesson>>> = _weekLessons

    fun handleEvent(event: UIScheduleEvent) {
        when(event) {
            is UIScheduleEvent.ShowWeekLessons -> showWeekLessons(event.week)
            is UIScheduleEvent.ShowTodayLessons -> showTodayLessons()
            is UIScheduleEvent.ShowDateToday -> showDateToday()
        }
    }

    private fun showTodayLessons() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _todayLessons.value = getTodayLessons()
            }
        }
    }

    private fun showDateToday() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _today.value = getTodayDate()
            }
        }
    }

    private fun showWeekLessons(week: HashMap<Int, ArrayList<DataClasses.Lesson>>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _weekLessons.value = week
            }
        }
    }

    private suspend fun getWeekLessonsByGroup(): HashMap<Int, ArrayList<DataClasses.Lesson>> {
        return withContext(Dispatchers.IO) {
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
        return withContext(Dispatchers.IO) {
            val week: HashMap<Int, ArrayList<DataClasses.Lesson>> = getWeekLessonsByGroup()
            val cacheManager = CacheManager(resources.getContext())

            // update week
            showWeekLessons(week)
            updateWeekDates()

            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH)
            val dayWeek = currentDate.format(formatter).uppercase()

            for (day in week.keys) {
                if (DataClasses.DayWeek.findById(day)?.name == dayWeek) {
                    cacheManager.saveTodaySchedule(week[day] as ArrayList<DataClasses.Lesson>)

                    // clear all of the alarms
                    val alarmManager = resources.getContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

                    val alarms = cacheManager.loadAlarms()

                    if (alarms != null && alarms.isNotEmpty()) {
                        for (alarm in alarms) {

                            val pendingIntent = PendingIntent.getBroadcast(
                                resources.getContext(),
                                alarm.id,
                                alarm.intent,
                                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )

                            alarmManager.cancel(pendingIntent)
                        }
                    }

                    // set new alarms
                    val intents = ArrayList<CacheManager.IntentConf>()
                    for ((i, lesson) in (week[day] as ArrayList<DataClasses.Lesson>).withIndex()) {
                        val intent = setNotificationForLesson(resources.getContext(), lesson, i)
                        if (intent != null) intents.add(CacheManager.IntentConf(i, intent))
                    }
                    cacheManager.saveAlarms(intents)

                    return@withContext week[day] as ArrayList<DataClasses.Lesson>
                }
            }
            return@withContext ArrayList()
        }
    }

    private suspend fun getCurrentWeekDate(): HashMap<Int, String> {
        return withContext(Dispatchers.IO) {
            val week = HashMap<Int, String>()

            val today = LocalDate.now()
            val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM", Locale("RU"))

            for (i in 0..7) {
                val dayOfWeek = startOfWeek.plusDays(i.toLong())
                week[i + 1] = dayOfWeek.format(formatter).replaceFirstChar { it.uppercase() }
            }

            return@withContext week
        }
    }

    private suspend fun setNotificationForLesson(context: Context, lesson: DataClasses.Lesson, id: Int): Intent? {
        return withContext(Dispatchers.IO) {
            val lessonName = lesson.name
            val lessonCount = lesson.count
            val lessonLocation = lesson.location
            val lessonTime = lesson.time

            val lessonStartTimeString = lessonTime.split("-")[0]
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val lessonStartTime = LocalTime.parse(lessonStartTimeString, formatter)
            val currentDate = LocalDate.now()
            val lessonTimeInMillis = currentDate.atTime(lessonStartTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            if (lessonTimeInMillis > System.currentTimeMillis()) {
                val notificationTime = lessonTimeInMillis - 5 * 60 * 1000

                val intent = Intent(context, NotificationReceiver::class.java).apply {
                    putExtra("lesson", "Пара $lessonCount: $lessonName в $lessonLocation")
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    id,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    notificationTime,
                    pendingIntent
                )
                return@withContext intent
            }
            return@withContext null
        }
    }

    private suspend fun getTodayDate(): String {
        return withContext(Dispatchers.IO) {
            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM", Locale("RU"))
            return@withContext currentDate.format(formatter).replaceFirstChar { it.uppercase() }
        }
    }

}