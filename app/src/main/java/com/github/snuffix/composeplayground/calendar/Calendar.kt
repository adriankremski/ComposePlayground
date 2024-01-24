package com.github.snuffix.composeplayground.calendar

import androidx.compose.runtime.Immutable

@Immutable
data class Calendar(
    val months: List<MonthData>
)
