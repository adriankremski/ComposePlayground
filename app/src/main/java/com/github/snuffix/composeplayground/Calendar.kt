package com.github.snuffix.composeplayground

import android.os.Build
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.time.measureTime

@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    calendarModifier: Modifier = Modifier,
    pivotFractionX: Float = 0f,
    pivotFractionY: Float = 0f,
    selectedDateNumber: Int = 0,
    calendarData: CalendarList,
) {
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var targetSize by remember { mutableStateOf(0f) }
    val sizeScale by animateFloatAsState(targetSize, label = "")

    LaunchedEffect(key1 = calendarData.calendarData) {
        targetSize = 1f
        scope.launch {
            state.scrollToItem(selectedDateNumber)
        }
    }

    LazyColumn(
        state = state,
        modifier = modifier.graphicsLayer {
            scaleX = sizeScale
            scaleY = sizeScale
            transformOrigin = TransformOrigin(pivotFractionX, pivotFractionY)
        }
    ) {
        items(calendarData.calendarData.size) {
            val month = calendarData.calendarData[it]
            MonthCell(
                month = month,
                calendarModifier = calendarModifier,
                onMonthSelected = { _, _, _ ->

                })
        }
    }
}

@Composable
fun MonthCell(
    modifier: Modifier = Modifier,
    month: CalendarData,
    calendarModifier: Modifier = Modifier,
    cellTextSize: TextUnit = 15.sp,
    dayCellHeight: Dp = 60.dp,
    onMonthSelected: (CalendarData, Int, Int) -> Unit
) {
    var position by remember { mutableStateOf(Offset.Zero) }
    var singleDigitDayLayoutRes by remember { mutableStateOf<TextLayoutResult?>(null) }
    var doubleDigitDayLayoutRes by remember { mutableStateOf<TextLayoutResult?>(null) }
    val textMeasurer = rememberTextMeasurer()
    val singleDigitText = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontSize = cellTextSize,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default,
            ),
        ) {
            append("1")
        }
    }

    val doubleDigitText = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontSize = cellTextSize,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default,
            ),
        ) {
            append("11")
        }
    }

    Column(
        modifier = modifier
            .clickable {
                Log.i("AdrianTest", "position: $position")
                onMonthSelected(month, position.x.toInt(), position.y.toInt())
            }
            .onPlaced {
                val x = it.positionInRoot().x
                val y = it.positionInRoot().y

                position = Offset(
                    x = x / it.size.width.toFloat(),
                    y = y / it.size.height.toFloat()
                )
            }
    ) {

        Text(text = month.name)

        val firstDayOfMonth = month.firstDayOfMonth
        val lastDateOfMonth = month.lastDateOfMonth
        val cellHeight = dayCellHeight
        val cellHeightInPix = with(LocalDensity.current) { cellHeight.toPx() }.toFloat()
        val firstWeek = firstDayOfMonth.get(WeekFields.ISO.weekOfWeekBasedYear())
        val weeks =
            (lastDateOfMonth.get(WeekFields.ISO.weekOfWeekBasedYear()) - firstDayOfMonth.get(
                WeekFields.ISO.weekOfWeekBasedYear()
            )) + 1
        val height = cellHeight.times(weeks)

        Canvas(
            modifier = calendarModifier
                .height(height)
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)

                    singleDigitDayLayoutRes = textMeasurer.measure(singleDigitText)
                    doubleDigitDayLayoutRes = textMeasurer.measure(doubleDigitText)

                    layout(placeable.width, placeable.height) {
                        placeable.place(0, 0)
                    }
                }
        ) {
            for (dayOfMonth in (firstDayOfMonth.dayOfMonth - 1)..<lastDateOfMonth.dayOfMonth) {
                measureTime {
                    val week = firstDayOfMonth.plusDays(dayOfMonth.toLong())
                        .get(WeekFields.ISO.weekOfWeekBasedYear())
                    val dayOfWeek =
                        firstDayOfMonth.plusDays(dayOfMonth.toLong()).get(WeekFields.ISO.dayOfWeek())
                    val weekOffset = (week - firstWeek)
                    val cellWidth = size.width / 7
                }.apply {
                    Log.i("AdrianTest", "measureTime: $this")
                }

                val week = firstDayOfMonth.plusDays(dayOfMonth.toLong())
                    .get(WeekFields.ISO.weekOfWeekBasedYear())
                val dayOfWeek =
                    firstDayOfMonth.plusDays(dayOfMonth.toLong()).get(WeekFields.ISO.dayOfWeek())
                val weekOffset = (week - firstWeek)
                val cellWidth = size.width / 7
                val x = cellWidth * (dayOfWeek - 1)
                val y = cellHeightInPix * weekOffset

                if (dayOfMonth <= 9) {
                    singleDigitDayLayoutRes?.size?.let {
                        drawText(
                            text = (dayOfMonth + 1).toString(),
                            topLeft = Offset(
                                x + cellWidth / 2 - it.width  / 2,
                                y + cellHeightInPix / 2 - it.height / 2
                            ),
                            textMeasurer = textMeasurer,
                            style = TextStyle(
                                fontSize = cellTextSize,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Default,
                            )
                        )
                    }
                } else {
                    doubleDigitDayLayoutRes?.size?.let {
                        drawText(
                            text = (dayOfMonth + 1).toString(),
                            topLeft = Offset(
                                x + cellWidth / 2 - it.width / 2,
                                y + cellHeightInPix / 2 - it.height / 2
                            ),
                            textMeasurer = textMeasurer,
                            style = TextStyle(
                                fontSize = cellTextSize,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Default,
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun YearCalendar(
    modifier: Modifier = Modifier,
    calendarModifier: Modifier = Modifier,
    calendarData: CalendarList,
    onMonthSelected: (CalendarData, Int, Int) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(3)
    ) {
        items(calendarData.calendarData.size) {
            val month = calendarData.calendarData[it]
            MonthCell(
                month = month,
                calendarModifier = calendarModifier,
                dayCellHeight = 20.dp,
                cellTextSize = 10.sp,
                onMonthSelected = onMonthSelected
            )
        }
    }
}

@Immutable
data class CalendarList(
    val calendarData: List<CalendarData>
)

@Immutable
data class CalendarData(
    val index: Int = 0,
    val name: String,
    val firstDayOfMonth: LocalDate,
    val lastDateOfMonth: LocalDate,
)