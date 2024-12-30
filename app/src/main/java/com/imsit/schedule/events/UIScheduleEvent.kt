package com.imsit.schedule.events

sealed class UIScheduleEvent {
    object ShowTodayLessons : UIScheduleEvent()
    object ShowWeekLessons : UIScheduleEvent()
    object ShowDateToday : UIScheduleEvent()
}