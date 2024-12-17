package com.imsit.schedule.tests

import com.imsit.schedule.models.Schedule
import org.junit.jupiter.api.Test

@Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
class ScheduleTest {

    @Test
    fun loadData() {
        val groups: HashMap<String, HashMap<String, ArrayList<Schedule.Group>>>
        var gap: Int
        val schedule = Schedule()
        groups = schedule.loadData { newProgress ->
            gap = newProgress // Update progress
        }
        println(groups)
    }
}