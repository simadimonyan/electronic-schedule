package com.imsit.schedule.domain.usecases

import com.imsit.schedule.data.models.DataClasses.Group
import com.imsit.schedule.data.parsers.WebParser

class GetSchedule {

    companion object {

        fun getSchedule(progress: (Int) -> Unit): HashMap<String, HashMap<String, ArrayList<Group>>> {
            return WebParser.loadData(progress)
        }

    }

}