package com.github.snuffix.composeplayground.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun YearsCalendarView(
    modifier: Modifier = Modifier,
    calendarModifier: Modifier = Modifier,
    calendarData: Calendar,
    onMonthSelected: (MonthData, Offset) -> Unit,
    onDrawBehindDay: DrawScope.(Size, MonthData, DayNumber, AnimationValue) -> Unit = { _, _, _, _ -> },
    onDaySelected: ((MonthData, DayNumber) -> Unit)? = null,
) {
    val monthsGroupedByYear by remember {
        mutableStateOf(calendarData.months.groupBy { it.firstDayOfMonth.year })
    }

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        monthsGroupedByYear.forEach { (year, months) ->
            item(key = year.toString(), span = {
                GridItemSpan(this.maxLineSpan)
            }) {
                Text(
                    text = "$year",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp).fillMaxWidth()
                )
            }

            months.forEach { month ->
                item(key = month.key) {
                    MonthView(
                        month = month,
                        calendarModifier = calendarModifier,
                        dayCellTextSize = 9.sp,
                        cellHeight = 16.dp,
                        monthNameFormatter = { "${it.month}" },
                        onMonthSelected = onMonthSelected,
                        onDrawBehindDay = onDrawBehindDay,
                        onDaySelected = onDaySelected,
                    )
                }
            }

            item(key = "Divider$year", span = {
                GridItemSpan(this.maxLineSpan)
            }) {
                Divider()
            }
        }
    }
}