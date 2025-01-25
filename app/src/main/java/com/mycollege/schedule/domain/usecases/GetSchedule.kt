package com.mycollege.schedule.domain.usecases

import com.mycollege.schedule.data.models.DataClasses.Group
import com.mycollege.schedule.data.parsers.WebParser

class GetSchedule {

    companion object {

        fun getSchedule(progress: (Int) -> Unit): HashMap<String, HashMap<String, ArrayList<Group>>> {
            return WebParser.loadData(progress)
        }

    }

}