package com.github.snuffix.composeplayground.calendar

import androidx.compose.runtime.Immutable
import java.time.LocalDate
import java.util.UUID

typealias MonthKey = String
typealias DayNumber = Int

@Immutable
data class MonthData(
    val key: MonthKey = UUID.randomUUID().toString(),
    val firstDayOfMonth: LocalDate,
    val lastDayOfMonth: LocalDate,
)
