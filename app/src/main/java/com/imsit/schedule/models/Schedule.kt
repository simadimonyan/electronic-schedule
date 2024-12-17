package com.imsit.schedule.models

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements


class Schedule {

    private val timeout: Int = 10000
    private var doc: Document? = null

    data class Weeks(var weekOdd: HashMap<Int, ArrayList<Lesson>>?, var weekEven: HashMap<Int, ArrayList<Lesson>>?)
    data class Lesson(val count: Int, val time: String, val type: String, val name: String?, val teacher: String?, val location: String?)
    data class Group(val group: String, val link: String, val lessons: Weeks?)
    private var groups: HashMap<String, HashMap<String, ArrayList<Group>>> = HashMap()

    private fun connect() {
        val url = "https://imsit.ru/timetable/stud/raspisan.html"
        this.doc = Jsoup.connect(url)
            .userAgent("Mozilla")
            .timeout(timeout)
            .get()
    }

    enum class DayWeek(val id: Int, val short: String, val long: String) {
        MONDAY(1, "Пнд", "Понедельник"),
        TUESDAY(2, "Втр", "Вторник"),
        WEDNESDAY(3, "Срд", "Среда"),
        THURSDAY(4, "Чтв", "Четверг"),
        FRIDAY(5, "Птн", "Пятница"),
        SATURDAY(6, "Сбт", "Суббота");

        companion object {
            fun findByShort(shortName: String): DayWeek? {
                return entries.find { it.short.equals(shortName, ignoreCase = true) }
            }
            fun findById(id: Int): DayWeek? {
                return entries.find { it.id == id }
            }
        }

    }

    fun loadData(progress: (Int) -> Unit): HashMap<String, HashMap<String, ArrayList<Group>>> {
        connect()

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
                    val url = "https://imsit.ru/timetable/stud/$a"
                    val schedule = Jsoup.connect(url)
                        .userAgent("Mozilla")
                        .timeout(timeout)
                        .get()

                    val weeks = Weeks(null, null)

                    for (j in 1..2) {
                        val tables: Element = schedule.select("table")[if (j == 1) 0 else 1] //odd week and even week as index on the page
                        val tRows = tables.select("tr")
                        val counts = tRows[0]
                        val period = tRows[1]

                        val week: HashMap<Int, ArrayList<Lesson>> = HashMap()

                        // getting day lessons
                        for (day in tRows.drop(2)) {
                            val data = day.select("td")
                            val dayWeek = DayWeek.findByShort(data[0].text())
                            val lessonsArray: ArrayList<Lesson> = ArrayList()

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

                                lessonsArray.add(Lesson(count, time, finalType, name, teacher, location))
                                l++
                            }

                            // sort by lesson order in a day
                            val sortedLessons = lessonsArray.sortedWith(Comparator.comparingInt {
                                it.count
                            })

                            // adding a lesson to a day
                            if (dayWeek != null) {
                                week[dayWeek.id] = sortedLessons.toMutableList() as ArrayList<Lesson>
                            }
                        }

                        // sort by day order in a week
                        val sortedWeek = week.toSortedMap().toMutableMap() as HashMap<Int, ArrayList<Lesson>>

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
