package com.github.snuffix.composeplayground.calendar

import androidx.compose.runtime.Immutable
import java.time.LocalDate

@Immutable
data class Calendar(
    val today: LocalDate,
    val months: List<MonthData>
)
