package com.mycollege.schedule.presentation.screens.groups.data

/**
 * Data class for managing state of Group Screen
 */
data class GroupState(

    // choose configuration
    val course: String = "1 курс",
    val speciality: String = "Все специальности",
    val group: String = "Выбрать",

    // utils for interacting
    val showBottomSheet: Boolean = false,
    val selectedIndex: Int = 0,

    // action button
    val scheduleCreation: Boolean = false,

    // bottom sheet data
    val coursesToDisplay: List<String> = ArrayList(),
    val specialitiesToDisplay: List<String> = ArrayList(),
    val groupsToDisplay: List<String> = ArrayList(),

)