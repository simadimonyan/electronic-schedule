package com.imsit.schedule.data.parsers

import com.imsit.schedule.data.models.DataClasses
import com.imsit.schedule.data.models.DataClasses.Group
import com.imsit.schedule.data.network.Network
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class WebParser {

    companion object {

        private const val TIMEOUT: Int = 10000
        private var doc: Document? = null
        private var groups: HashMap<String, HashMap<String, ArrayList<Group>>> = HashMap()
        private const val FULL_URL = "https://imsit.ru/timetable/stud/raspisan.html"
        private var PATTERN_RUL = "https://imsit.ru/timetable/stud/"

        fun loadData(progress: (Int) -> Unit): HashMap<String, HashMap<String, ArrayList<Group>>> {
            doc = Network.connect(FULL_URL, TIMEOUT)

            val table: Element = doc!!.select("table")[0]
            val rows = table.select("tr")

            // First row
            val columns: Elements = rows[0].select("td")

            // Get all of the courses
            for (column in columns) {
                this.groups[column.text()] = HashMap()
            }

            val sorted = groups.toSortedMap(Comparator.comparingInt {
                it.split(" ")[0].toInt()
            })

            // Get all of the groups
            val total = sorted.size + 3
            var current = 2
            progress((current * 100) / total)

            // Get all of the groups
            for (i in 0 until sorted.size) {
                val specialityArray = HashMap<String, ArrayList<Group>>()

                for (row in rows.drop(1)) {
                    val tableData = row.select("td")
                    val a = tableData[i].select("a").attr("href")

                    // if cell is not empty
                    if (!tableData[i].text().equals("")) {

                        // getting group schedule
                        val schedule = Network.connect("$PATTERN_RUL$a", TIMEOUT)

                        val weeks = DataClasses.Weeks(null, null)

                        for (j in 1..2) {
                            val tables: Element = schedule.select("table")[if (j == 1) 0 else 1] //odd week and even week as index on the page
                            val tRows = tables.select("tr")
                            val counts = tRows[0]
                            val period = tRows[1]

                            val week: HashMap<Int, ArrayList<DataClasses.Lesson>> = HashMap()

                            // getting day lessons
                            for (day in tRows.drop(2)) {
                                val data = day.select("td")
                                val dayWeek = DataClasses.DayWeek.findByShort(data[0].text())
                                val lessonsArray: ArrayList<DataClasses.Lesson> = ArrayList()

                                var l = 0
                                for (cell in data) {

                                    val text = cell.text()
                                    if (text.length < 4) {
                                        l++
                                        continue
                                    }

                                    val count = counts.select("td")[l].text().split("-")[0].toInt()
                                    val time: String = period.select("td")[l].text()

                                    val regex = Regex("""^(пр\.|л\.|лаб\.)\s*(\D+(?:\s+\D+)*)\s+([А-ЯЁ][а-яё]+ [А-ЯЁ]\.[А-ЯЁ]\.)\s+(.+)$""")
                                    val match = regex.find(text)

                                    val type = match?.groups?.get(1)?.value // Type: пр., л., лаб.
                                    val finalType = if (type == "пр.") "Практика" else if (type == "л.") "Лекция" else "Лаборатория"
                                    val teacher = match?.groups?.get(3)?.value // Teacher: Лисин Д.А.
                                    val location = match?.groups?.get(4)?.value // Location: 1-126
                                    val name = match?.groups?.get(2)?.value // Name: Электротехника

                                    lessonsArray.add(
                                        DataClasses.Lesson(
                                            count,
                                            time,
                                            finalType,
                                            name,
                                            teacher,
                                            location
                                        )
                                    )
                                    l++
                                }

                                // sort by lesson order in a day
                                val sortedLessons = lessonsArray.sortedWith(Comparator.comparingInt {
                                    it.count
                                })

                                // adding a lesson to a day
                                if (dayWeek != null) {
                                    week[dayWeek.id] = sortedLessons.toMutableList() as ArrayList<DataClasses.Lesson>
                                }
                            }

                            // sort by day order in a week
                            val sortedWeek = week.toSortedMap().toMutableMap() as HashMap<Int, ArrayList<DataClasses.Lesson>>

                            // setting full schedule on 2 weeks
                            if (j == 1) weeks.weekOdd = sortedWeek else weeks.weekEven = sortedWeek
                        }

                        val speciality: String = if (tableData[i].text().contains("СПО")) "СПО"
                        else if (tableData[i].text().contains("Мг")) "Магистратура" else "Бакалавриат"

                        if (specialityArray[speciality] != null)
                            specialityArray[speciality]?.add(Group(tableData[i].text(), a, weeks))
                        else {
                            val groupArray = ArrayList<Group>()
                            groupArray.add(Group(tableData[i].text(), a, weeks))
                            specialityArray[speciality] = groupArray
                        }
                    }

                }
                sorted[sorted.keys.elementAt(i)] = specialityArray.toSortedMap(java.util.Comparator.comparingInt
                { it.length }).toMutableMap() as HashMap<String, ArrayList<Group>>

                current++
                progress((current * 100) / total)  // update the progress
            }
            this.groups = sorted.toMutableMap() as HashMap<String, HashMap<String, ArrayList<Group>>>

            return this.groups
        }

    }

}