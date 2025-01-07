package com.imsit.schedule.events

import com.imsit.schedule.data.models.DataClasses

sealed class UIScheduleEvent {
    object ShowTodayLessons : UIScheduleEvent()
    data class ShowWeekLessons(val week: HashMap<Int, ArrayList<DataClasses.Lesson>>) : UIScheduleEvent()
    object ShowDateToday : UIScheduleEvent()
}