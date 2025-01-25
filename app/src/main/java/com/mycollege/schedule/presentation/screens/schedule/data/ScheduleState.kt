package com.mycollege.schedule.presentation.screens.schedule.data

import com.mycollege.schedule.data.models.DataClasses

/**
 * Data class for managing state of Schedule Screen
 */
data class ScheduleState(

    // system local dates
    val todayDate: String = "",
    val weekDates: HashMap<Int, String> = HashMap(),

    // schedule params
    val todayLessons: ArrayList<DataClasses.Lesson> = ArrayList(),
    val weekLessons: HashMap<Int, ArrayList<DataClasses.Lesson>> = HashMap()

)