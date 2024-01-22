package com.github.snuffix.composeplayground.calendar

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun YearsCalendarView(
    modifier: Modifier = Modifier,
    calendarModifier: Modifier = Modifier,
    calendarData: CalendarList,
    onMonthSelected: (CalendarData, Offset) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(3)
    ) {
        items(
            count = calendarData.calendarData.size,
            key = { index -> calendarData.calendarData[index].index }
        ) {
            val month = calendarData.calendarData[it]
            MonthView(
                month = month,
                calendarModifier = calendarModifier,
                dayCellHeight = 20.dp,
                cellTextSize = 10.sp,
                onMonthSelected = onMonthSelected
            )
        }
    }
}