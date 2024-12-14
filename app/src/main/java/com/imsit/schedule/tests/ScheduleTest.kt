package com.imsit.schedule.tests

import com.imsit.schedule.models.Schedule
import org.junit.jupiter.api.Test

class ScheduleTest {

    @Test
    fun loadData() {
        var groups: HashMap<String, ArrayList<Schedule.Group>>? = null

        val schedule = Schedule()
        groups = schedule.loadData()
        println(groups)
    }
}