package com.imsit.schedule.tests

import org.junit.jupiter.api.Test

class ParsingTest {

    private fun parsing(text: String) {
        val regex = Regex("""^(пр\.|л\.|лаб\.)\s*(\D+?)(?:\n?([А-ЯЁ][а-яё]+ [А-ЯЁ]\.[А-ЯЁ]\.))?\s*(с/зал|[\d-]+(?:[а-яё]?)?)?\s*${'$'}""")
        val match = regex.find(text)

        val type = match?.groups?.get(1)?.value // Type: пр., л., лаб.
        val finalType = if (type == "пр.") "Практика" else if (type == "л.") "Лекция" else "Лаборатория"
        var name = match?.groups?.get(2)?.value // Name: Электротехника
        var teacher = match?.groups?.get(3)?.value // Teacher: Лисин Д.А.
        val location = match?.groups?.get(4)?.value // Location: 1-126

        if (location == null) {
            val nameParts = name?.split(" ")?.toMutableList()

            if (nameParts != null && nameParts.size >= 3) {
                teacher = (teacher ?: "") + " " + nameParts.takeLast(3).joinToString(" ")
                name = nameParts.dropLast(3).joinToString(" ")
            }
        }

        teacher = teacher?.trim()?.let {
            if (it.matches(Regex("^[А-ЯЁ][а-яё]+ [А-ЯЁ]\\.[А-ЯЁ]\\.$"))) {
                it
            } else { // if "Докторов С.Э.." replace last '.' with ""
                it.replace(Regex("\\.+$"), "")
            }
        }

        println("""
            |$finalType
            |$name 
            |$teacher
            |$location""")
    }

    @Test
    fun test() {
        parsing("пр.Физическая культура с/зал")
        parsing("л.Кубановедение Клечковская Е.В. 2-404")
        parsing("пр.Профессиональная этика и психология делового общения Оздоган И.С. 2-409")
        parsing("пр.Экономический анализ Крутова А.В. 1-236")
        parsing("пр.Информационные технологии в профессиональной деятельности Докторов С.Э. 1-114а")
        parsing("л.Информационные технологии в профессиональной деятельности Докторов С.Э. 1-308")
        parsing("л.Современные педагогические технологии Салменкова М.В. .")
    }

}