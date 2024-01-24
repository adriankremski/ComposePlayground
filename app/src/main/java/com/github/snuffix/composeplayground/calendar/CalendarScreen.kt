package com.github.snuffix.composeplayground.calendar

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters


// Mock data
var months = (0..36).map { month ->
    val firstDayOfMonth =
        LocalDate.now().plusMonths(month.toLong()).with(TemporalAdjusters.firstDayOfMonth())
    val lastDayOfMonth =
        LocalDate.now().plusMonths(month.toLong()).with(TemporalAdjusters.lastDayOfMonth())

    MonthData(
        firstDayOfMonth = firstDayOfMonth,
        lastDayOfMonth = lastDayOfMonth,
    )
}

@Composable
fun CalendarScreen() {
    var selectedMonthOffsetFraction by remember { mutableStateOf(Offset(0.5f, 0.5f)) }
    var selectedMonthKey by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(CalendarTab.Month) }

    val calendar = remember { Calendar(today = LocalDate.now(), months = months) }
    val monthsListState = rememberLazyListState()

    Column {
        var displayCurrentDayButton by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxWidth()) {
            CalendarTabs(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                selectedTab = selectedTab, onTabSelected = { tab ->
                    selectedMonthOffsetFraction = Offset(0.5f, 0.5f)
                    selectedTab = tab
                })

            if (displayCurrentDayButton) {
                val scope = rememberCoroutineScope()
                Text(
                    text = "Today",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(8.dp)
                        .clickable {
                            scope.launch {
                                monthsListState.scrollToItem(calendar.months.indexOfFirst {
                                    it.firstDayOfMonth.month == calendar.today.month
                                })
                            }
                        },
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Column {
            Column {
                AnimatedVisibility(
                    visible = selectedTab == CalendarTab.Month
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
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

            val selectedDays = remember { mutableStateMapOf<String, Set<Int>>() }
            val onDaySelected: (MonthData, DayNumber) -> Unit = { month, day ->
                selectedDays[month.key] = selectedDays[month.key]?.plus(day) ?: setOf(day)
            }
            val onDrawBehindDay: DrawScope.(Size, MonthData, DayNumber, AnimationValue) -> Unit =
                { size, month, dayNumber, animationValue ->
                    val width = size.width
                    val height = size.height

                    if (selectedDays[month.key]?.contains(dayNumber) == true) {
                        val dashesCount = 30f
                        val dashPortion = 0.75f
                        val gapPortion = 0.25f
                        val circumference = 2f * Math.PI * width / 2f
                        val dashPlusGapSize = (circumference / dashesCount).toFloat()

                        val padding = 4.dp.toPx()

                        val selectionSize = Size(width - padding * 2, height - padding * 2)
                        val topLeft = Offset(padding, padding)

                        when (dayNumber) {
                            in 0..10 -> {
                                drawArc(
                                    startAngle = 90f,
                                    sweepAngle = (360f) * animationValue,
                                    color = Color.Red,
                                    useCenter = false,
                                    topLeft = topLeft,
                                    size = selectionSize,
                                    style = Stroke(
                                        width = 2.dp.toPx(),
                                        cap = Stroke.DefaultCap,
                                        pathEffect = PathEffect.dashPathEffect(
                                            floatArrayOf(
                                                dashPlusGapSize * dashPortion,
                                                dashPlusGapSize * gapPortion
                                            ), 0f
                                        )
                                    )
                                )
                            }

                            else -> {
                                drawCircle(
                                    center = Offset(width / 2, height / 2),
                                    color = Color.Red,
                                    radius = selectionSize.width / 2f * animationValue,
                                    style = Stroke(
                                        width = 2.dp.toPx(),
                                        cap = Stroke.DefaultCap,
                                        pathEffect = PathEffect.dashPathEffect(
                                            floatArrayOf(
                                                dashPlusGapSize * dashPortion,
                                                dashPlusGapSize * gapPortion
                                            ), 0f
                                        )
                                    )
                                )
                            }
                        }
                    }
                }

            when (selectedTab) {
                CalendarTab.Month -> {
                    MonthsCalendarView(
                        calendar = calendar,
                        calendarModifier = Modifier.fillMaxWidth(),
                        pivotFractionX = selectedMonthOffsetFraction.x,
                        pivotFractionY = selectedMonthOffsetFraction.y,
                        selectedMonthKey = selectedMonthKey,
                        onDaySelected = onDaySelected,
                        onDrawBehindDay = onDrawBehindDay,
                        onCurrentMonthVisibilityChanged = { visible ->
                            displayCurrentDayButton = !visible
                        },
                        monthsListState = monthsListState
                    )
                }

                CalendarTab.Year -> {
                    YearsCalendarView(
                        calendarData = calendar,
                        calendarModifier = Modifier.fillMaxWidth(),
                        onMonthSelected = { data, offset ->
                            selectedMonthKey = data.key
                            selectedMonthOffsetFraction = offset
                            selectedTab = CalendarTab.Month
                        },
                        onDrawBehindDay = onDrawBehindDay,
                    )
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