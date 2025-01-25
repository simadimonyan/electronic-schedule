package com.mycollege.schedule.tests

import com.mycollege.schedule.data.models.DataClasses
import com.mycollege.schedule.domain.usecases.GetSchedule.Companion.getSchedule
import org.junit.jupiter.api.Test

@Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
class GetScheduleTest {

    @Test
    fun loadData() {
        val groups: HashMap<String, HashMap<String, ArrayList<DataClasses.Group>>>
        var gap: Int
        groups = getSchedule { newProgress ->
            gap = newProgress // Update progress
        }
        println(groups)
    }
}