package com.imsit.schedule.tests

import com.imsit.schedule.data.models.DataClasses
import com.imsit.schedule.domain.usecases.GetSchedule.Companion.getSchedule
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