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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
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
import java.util.UUID

typealias MonthKey = String
typealias DayNumber = Int

@Immutable
data class Calendar(
    val months: List<MonthData>
)

@Immutable
data class MonthData(
    val key: MonthKey = UUID.randomUUID().toString(),
    val firstDayOfMonth: LocalDate,
    val lastDayOfMonth: LocalDate,
)

@Composable
fun MonthView(
    modifier: Modifier = Modifier,
    month: MonthData,
    calendarModifier: Modifier = Modifier,
    dayCellTextSize: TextUnit = 15.sp,
    dayCellHeight: Dp = 60.dp,
    monthNameFormatter: (LocalDate) -> String = { "${it.month} ${it.year}" },
    onDrawBehindDay: DrawScope.(Float, Float, MonthData, DayNumber, Float) -> Unit = { _, _, _, _, _ -> },
    onDaySelected: ((MonthData, DayNumber) -> Unit)? = null,
    onMonthSelected: (MonthData, Offset) -> Unit,
) {
    var position by remember { mutableStateOf(Offset.Zero) }
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

                position = Offset(
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
        val cellHeightInPix = with(LocalDensity.current) { dayCellHeight.toPx() }.toFloat()
        val firstWeek = firstDayOfMonth.get(WeekFields.ISO.weekOfYear())
        val weeks =
            (lastDateOfMonth.get(WeekFields.ISO.weekOfYear()) - firstDayOfMonth.get(
                WeekFields.ISO.weekOfYear()
            )) + 1
        val height = dayCellHeight.times(weeks)

        data class DayCellCanvasData(
            val dayOfMonth: Int,
            val offset: Offset,
            val rect: RectF
        )

        var cellsData by remember { mutableStateOf<List<DayCellCanvasData>>(listOf()) }

        var lastSelectedDay by remember { mutableStateOf(-1) }

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
                                onMonthSelected(month, position)
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
                                    cellWidth,
                                    cellHeightInPix,
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