package com.github.snuffix.composeplayground.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

var data = (0..2).flatMap { year ->
    List(11) { month ->
        val firstDayOfMonth = LocalDate.now().withMonth(month + 1).plusYears(year.toLong())
            .with(TemporalAdjusters.firstDayOfMonth())
        val lastDayOfMonth = LocalDate.now().withMonth(month + 1).plusYears(year.toLong())
            .with(TemporalAdjusters.lastDayOfMonth())

        CalendarData(
            firstDayOfMonth = firstDayOfMonth,
            lastDayOfMonth = lastDayOfMonth,
            index = month + year * 12,
            name = "${firstDayOfMonth.month} ${firstDayOfMonth.year}"
        )
    }
}

@Composable
fun CalendarScreen() {
    var pivotFractionX by remember { mutableFloatStateOf(0f) }
    var pivotFractionY by remember { mutableFloatStateOf(0f) }
    var selectedDateNumber by remember { mutableIntStateOf(0) }
    var selectedTab by remember { mutableStateOf(CalendarTab.Month) }
    var daysOfWeekRowAlpha =
        animateFloatAsState(targetValue = if (selectedTab == CalendarTab.Month) 1f else 0f)

    val data = remember { data }

    Column {
        CalendarTabs(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            selectedTab = selectedTab, onTabSelected = {
                pivotFractionX = 0f
                pivotFractionY = 0f
                selectedTab = it
            })

        Column {
            Column {
                AnimatedVisibility(
                    visible = selectedTab == CalendarTab.Month
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("M", "T", "W", "T", "F", "S", "S").forEach {
                                DayOfWeekTextView(
                                    modifier = Modifier.weight(1f),
                                    text = it
                                )
                            }
                        }

                        Divider()
                    }
                }
            }
            when (selectedTab) {
                CalendarTab.Month -> {
                    MonthsCalendarView(
                        calendarData = CalendarList(data),
                        calendarModifier = Modifier.fillMaxWidth(),
                        pivotFractionX = pivotFractionX,
                        pivotFractionY = pivotFractionY,
                        selectedDateNumber = selectedDateNumber
                    )
                }

                CalendarTab.Year -> {
                    YearsCalendarView(
                        calendarData = CalendarList(data),
                        calendarModifier = Modifier.fillMaxWidth(),
                        onMonthSelected = { data, offset ->
                            selectedDateNumber = data.index
                            pivotFractionX = offset.x
                            pivotFractionY = offset.y
                            selectedTab = CalendarTab.Month
                        }
                    )
                }

                CalendarTab.Day -> {
                }
            }
        }
    }
}

@Composable
fun DayOfWeekTextView(modifier: Modifier = Modifier, text: String) {
    Text(
        text = text,
        modifier = modifier
            .padding(8.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodySmall
    )
}