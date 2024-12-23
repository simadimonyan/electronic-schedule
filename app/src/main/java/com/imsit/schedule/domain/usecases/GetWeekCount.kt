package com.imsit.schedule.domain.usecases

import java.time.LocalDate
import java.time.temporal.ChronoUnit

class GetWeekCount {

    companion object {

        fun calculateCount(): Int {
            val currentDate = LocalDate.now()

            val firstSeptember = LocalDate.of(currentDate.year, 9, 1)

            val startDate = if (currentDate.isBefore(firstSeptember)) {
                LocalDate.of(currentDate.year - 1, 9, 1)
            } else {
                firstSeptember
            }

            val weeksBetween = ChronoUnit.WEEKS.between(startDate, currentDate).toInt()

            // Count: 0 - even, 1 - odd
            return weeksBetween % 2
        }

    }

}