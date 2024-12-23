package com.imsit.schedule.viewmodels

import android.app.NotificationManager
import android.content.Context
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imsit.schedule.R
import com.imsit.schedule.data.cache.CacheManager
import com.imsit.schedule.data.models.DataClasses
import com.imsit.schedule.domain.background.CacheUpdater
import com.imsit.schedule.domain.notifications.NotificationsManager
import com.imsit.schedule.domain.usecases.GetSchedule.Companion.getSchedule
import com.imsit.schedule.domain.usecases.GetWeekCount
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@HiltViewModel
class MainViewModel : ViewModel() {

    @ApplicationContext private lateinit var context: Context

    override fun onCleared() {
        super.onCleared()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        notificationManager.cancel(2)
    }

    private var _groups = MutableStateFlow<HashMap<String, HashMap<String, ArrayList<DataClasses.Group>>>>(HashMap())
    var groups: StateFlow<HashMap<String, HashMap<String, ArrayList<DataClasses.Group>>>> = _groups

    private var _loading = MutableStateFlow(true)
    var loading: StateFlow<Boolean> = _loading

    private var _progress = MutableStateFlow(0)
    var progress: StateFlow<Int> = _progress

    private var _course = MutableStateFlow("1 курс")
    var course: StateFlow<String> = _course

    private var _speciality = MutableStateFlow("Все специальности")
    var speciality: StateFlow<String> = _speciality

    private var _group = MutableStateFlow("Выбрать")
    var group: StateFlow<String> = _group

    private var _showBottomSheet = MutableStateFlow(false)
    var showBottomSheet: StateFlow<Boolean> = _showBottomSheet

    private var _selectedIndex = MutableStateFlow(0)
    var selectedIndex: StateFlow<Int> = _selectedIndex

    fun fetchData(context: Context) {
        viewModelScope.launch {
            val cacheManager = CacheManager(context)
            val notificationsManager = NotificationsManager()

            try {
                notificationsManager.createNotificationChannel(context)
                if (cacheManager.shouldUpdateCache()) {
                    try {
                        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        val notification = notificationsManager.createNotification(context, context.getString(R.string.get_data))
                        notificationManager.notify(2, notification)

                        val loadedGroups = withContext(Dispatchers.IO) {
                            getSchedule { newProgress ->
                                _progress.value = newProgress
                                notificationsManager.updateProgressNotification(2, context,
                                    _progress.value
                                )
                            }
                        }

                        _loading.value = true
                        _groups.value = loadedGroups

                        cacheManager.saveGroupsToCache(_groups.value)
                        cacheManager.saveLastUpdatedTime(System.currentTimeMillis())

                        notificationsManager.cancelNotification(2, context)
                        _loading.value = false
                    } catch (e: Exception) {
                        e.printStackTrace()

                        _loading.value = true
                        _groups.value = cacheManager.loadGroupsFromCache()
                        if (_groups.value.size > 1) {
                            _loading.value = false
                        }
                    }
                } else {
                    _groups.value = cacheManager.loadGroupsFromCache()
                    _loading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setupCacheUpdater(context: Context) {
        val cacheManager = CacheManager(context)
        CacheUpdater.setupPeriodicWork(context, cacheManager.getLastUpdatedTime())
    }

    fun setSelectedIndex(index: Int) {
        _selectedIndex.value = index
    }

    fun onSelectItem(context: Context, index: Int, newValue: String) {
        val cacheManager = CacheManager(context)

        when (index) {
            0 -> {
                _course.value = newValue
                _group.value = getString(context,  R.string.choose)
                _speciality.value = getString(context,  R.string.all_specialities)
            }
            1 -> {
                _speciality.value = newValue
                _group.value = getString(context,  R.string.choose)
            }
            2 -> _group.value = newValue
        }

        _course.value.let {
            _speciality.value.let { it1 ->
                _group.value.let { it2 ->
                    CacheManager.Configuration(it,
                        it1, it2
                    )
                }
            }
        }.let { cacheManager.saveActualConfiguration(it) }
    }

    fun restoreCache(context: Context) {
        try {
            val cacheManager = CacheManager(context)

            // Restore last chosen parameters
            val configuration = cacheManager.loadLastConfiguration()
            if (configuration.group != "") {
                _course.value = configuration.course
                _speciality.value = configuration.speciality
                _group.value = configuration.group
            }
        }
        catch (_: Exception) {} // first start up is always null
    }

    fun toggleBottomSheet(toggle: Boolean) {
        _showBottomSheet.value = toggle
    }

    fun getWeekLessonsByGroup(): HashMap<Int, ArrayList<DataClasses.Lesson>>? {
        val groups: java.util.ArrayList<DataClasses.Group>? =
            _groups.value[_course.value]?.get(if (_group.value.contains("СПО")) "СПО"
                else if (_group.value.contains("Мг")) "Магистратура"
                else "Бакалавриат")

        var chosenGroup: DataClasses.Group? = null
        val count = GetWeekCount.calculateCount()

        if (groups != null) {
            for (group in groups.iterator()) {
                if (group.group == _group.value) {
                    chosenGroup = group
                    break
                }
            }
        }

        if (chosenGroup != null) {
            return if (count == 0) chosenGroup.lessons?.weekEven else chosenGroup.lessons?.weekOdd
        }
        return HashMap()
    }

    fun getTodayLessons(): ArrayList<DataClasses.Lesson> {
        val week: HashMap<Int, ArrayList<DataClasses.Lesson>>? = getWeekLessonsByGroup()

        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH)
        val dayWeek = currentDate.format(formatter).uppercase()

        if (week != null) {
            for (day in week.keys) {
                if (DataClasses.DayWeek.findById(day)?.name == dayWeek) {
                    return week[day] as ArrayList<DataClasses.Lesson>
                }
            }
        }
        return ArrayList()
    }

    fun getTodayDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM", Locale("RU"))
        return currentDate.format(formatter).replaceFirstChar { it.uppercase() }
    }

    fun getTodayWeekCount(): Int {
        return if (GetWeekCount.calculateCount() == 0) 2 else 1
    }

}