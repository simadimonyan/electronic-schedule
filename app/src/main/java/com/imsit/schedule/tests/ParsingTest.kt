package com.imsit.schedule.tests

import org.junit.jupiter.api.Test

class ParsingTest {

    private fun parsing(text: String) {
        val regex = Regex("""^(пр\.|л\.|лаб\.)\s*(\D+?)\s*([А-ЯЁ][а-яё]+ [А-ЯЁ]\.[А-ЯЁ]\.)?\s*(с/зал|[\d-]+(?:[а-яё]?)?)?${'$'}""")
        val match = regex.find(text)

        val type = match?.groups?.get(1)?.value // Type: пр., л., лаб.
        val finalType = if (type == "пр.") "Практика" else if (type == "л.") "Лекция" else "Лаборатория"
        val name = match?.groups?.get(2)?.value // Name: Электротехника
        val teacher = match?.groups?.get(3)?.value // Teacher: Лисин Д.А.
        val location = match?.groups?.get(4)?.value // Location: 1-126

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
    }

}