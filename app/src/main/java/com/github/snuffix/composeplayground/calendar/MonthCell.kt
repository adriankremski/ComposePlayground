package com.github.snuffix.composeplayground.calendar

import android.graphics.RectF
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
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

typealias AnimationValue = Float

data class DayCellCanvasData(
    val dayOfMonth: Int,
    val offset: Offset,
    val rect: RectF
)


@Composable
fun MonthView(
    modifier: Modifier = Modifier,
    month: MonthData,
    calendarModifier: Modifier = Modifier,
    dayCellTextSize: TextUnit = 15.sp,
    dayCellHeight: Dp = 60.dp,
    monthNameFormatter: (LocalDate) -> String = { "${it.month} ${it.year}" },
    onDrawBehindDay: DrawScope.(Size, MonthData, DayNumber, AnimationValue) -> Unit = { _, _, _, _ -> },
    onDaySelected: ((MonthData, DayNumber) -> Unit)? = null,
    onMonthSelected: (MonthData, Offset) -> Unit,
) {
    var calendarFractionPosition by remember { mutableStateOf(Offset.Zero) } // (0,0) = topLeft, (1f,1f) = bottomRight
    var singleDigitDayLayoutRes by remember { mutableStateOf<TextLayoutResult?>(null) }
    var doubleDigitDayLayoutRes by remember { mutableStateOf<TextLayoutResult?>(null) }
    val textMeasurer = rememberTextMeasurer()
    val singleDigitText = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontSize = dayCellTextSize,
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
                fontSize = dayCellTextSize,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default,
            ),
        ) {
            append("11")
        }
    }

    Column(
        modifier = modifier
            .onPlaced {
                val x = it.positionInRoot().x
                val y = it.positionInRoot().y

                val parentSize = it.parentCoordinates?.size ?: it.size

                calendarFractionPosition = Offset(
                    x = x / parentSize.width.toFloat(),
                    y = y / parentSize.height.toFloat()
                )
            }
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = monthNameFormatter(month.firstDayOfMonth)
        )

        val firstDayOfMonth = month.firstDayOfMonth
        val lastDateOfMonth = month.lastDayOfMonth

        val firstWeek = firstDayOfMonth.get(WeekFields.ISO.weekOfYear())
        val weeks = overlappingWeeks(start = firstDayOfMonth, end = lastDateOfMonth)
        val height = dayCellHeight.times(weeks)

        val cellHeightInPix = with(LocalDensity.current) { dayCellHeight.toPx() }.toFloat()
        var cellsData by remember { mutableStateOf<List<DayCellCanvasData>>(listOf()) }

        var lastSelectedDay by remember { mutableIntStateOf(-1) }

        val clickAnim = remember { Animatable(0f) }
        val scope = rememberCoroutineScope()

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
                .pointerInput(month.key) {
                    detectTapGestures(
                        onTap = { tapOffset ->
                            if (onDaySelected != null) {
                                cellsData
                                    .firstOrNull {
                                        it.rect.contains(tapOffset.x, tapOffset.y)
                                    }
                                    ?.let {
                                        onDaySelected(month, it.dayOfMonth)
                                        lastSelectedDay = it.dayOfMonth

                                        scope.launch {
                                            clickAnim.snapTo(0f)
                                            clickAnim.animateTo(
                                                targetValue = 1f,
                                                animationSpec = tween(500)
                                            )
                                        }
                                    }
                            } else {
                                onMonthSelected(month, calendarFractionPosition)
                            }
                        }
                    )
                }
                .drawWithCache {
                    val cellWidth = size.width / 7
                    cellsData =
                        ((firstDayOfMonth.dayOfMonth - 1)..<lastDateOfMonth.dayOfMonth).map { dayOfMonth ->
                            val week = firstDayOfMonth
                                .plusDays(dayOfMonth.toLong())
                                .get(WeekFields.ISO.weekOfYear())
                            val dayOfWeek =
                                firstDayOfMonth
                                    .plusDays(dayOfMonth.toLong())
                                    .get(WeekFields.ISO.dayOfWeek())
                            val weekOffset = (week - firstWeek)
                            val x = cellWidth * (dayOfWeek - 1)
                            val y = cellHeightInPix * weekOffset

                            DayCellCanvasData(
                                dayOfMonth,
                                Offset(x, y),
                                RectF(x, y, x + cellWidth, y + cellHeightInPix)
                            )
                        }

                    val cellPositions = cellsData.map { it.offset }

                    onDrawBehind {
                        for (dayOfMonth in (firstDayOfMonth.dayOfMonth - 1)..<lastDateOfMonth.dayOfMonth) {
                            val (x, y) = cellPositions[dayOfMonth]

                            this.inset(
                                left = x,
                                top = y,
                                right = 0f,
                                bottom = 0f
                            ) {
                                onDrawBehindDay(
                                    Size(cellWidth, cellHeightInPix),
                                    month,
                                    dayOfMonth,
                                    if (dayOfMonth == lastSelectedDay) clickAnim.value else 1f
                                )
                            }

                            if (dayOfMonth < 9) {
                                singleDigitDayLayoutRes?.size?.let {
                                    drawText(
                                        text = (dayOfMonth + 1).toString(),
                                        topLeft = Offset(
                                            x + cellWidth / 2 - it.width / 2,
                                            y + cellHeightInPix / 2 - it.height / 2
                                        ),
                                        textMeasurer = textMeasurer,
                                        style = TextStyle(
                                            fontSize = dayCellTextSize,
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
                                            fontSize = dayCellTextSize,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Default,
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
        ) {

        }
    }
}

fun LocalDate.weekOfYear() = get(WeekFields.ISO.weekOfYear())

fun overlappingWeeks(start: LocalDate, end: LocalDate): Int {
    val firstWeek = start.get(WeekFields.ISO.weekOfYear())
    val lastWeek = end.get(WeekFields.ISO.weekOfYear())
    return (lastWeek - firstWeek) + 1
}