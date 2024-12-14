package com.imsit.schedule.models

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.Calendar


class Schedule {

    private var doc: Document? = null
    private var weekCount: Byte = 1
    private var today: Calendar = Calendar.getInstance()

    data class Group(val group: String, val link: String)
    private var groups: HashMap<String, ArrayList<Group>> = HashMap()

    private fun connect() {
        val url = "https://imsit.ru/timetable/stud/raspisan.html"
        this.doc = Jsoup.connect(url)
            .userAgent("Mozilla")
            .timeout(5000)
            .get()
    }

    fun loadData(): HashMap<String, ArrayList<Group>> {
        connect()

        val table: Element = doc!!.select("table")[0]
        val rows = table.select("tr")

        // First row
        val columns: Elements = rows[0].select("td")

        // Get all of the courses
        for (column in columns) {
            this.groups[column.text()] = ArrayList()
        }

        val sorted = groups.toSortedMap(Comparator.comparingInt {
            it.split(" ")[0].toInt()
        })

        // Get all of the groups
        for (i in 0 until sorted.size) {
            val array = ArrayList<Group>()

            for (row in rows.drop(1)) {
                val tableData = row.select("td")
                val a = tableData[i].select("a").attr("href")

                if (!tableData[i].text().equals(""))
                    array.add(Group(tableData[i].text(), a))
            }

            sorted[sorted.keys.elementAt(i)] = array
        }
        this.groups = sorted.toMutableMap() as HashMap<String, ArrayList<Group>>
        return this.groups
    }

    private fun getSchedule(course: String, speciality: String, group: String) {

    }



}
