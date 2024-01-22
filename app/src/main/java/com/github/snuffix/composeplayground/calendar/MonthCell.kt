package com.github.snuffix.composeplayground.calendar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
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
import java.time.LocalDate
import java.time.temporal.WeekFields

@Composable
fun MonthView(
    modifier: Modifier = Modifier,
    month: CalendarData,
    calendarModifier: Modifier = Modifier,
    cellTextSize: TextUnit = 15.sp,
    dayCellHeight: Dp = 60.dp,
    onMonthSelected: (CalendarData, Offset) -> Unit
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
                onMonthSelected(month, position)
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
        val lastDateOfMonth = month.lastDayOfMonth
        val cellHeightInPix = with(LocalDensity.current) { dayCellHeight.toPx() }.toFloat()
        val firstWeek = firstDayOfMonth.get(WeekFields.ISO.weekOfWeekBasedYear())
        val weeks =
            (lastDateOfMonth.get(WeekFields.ISO.weekOfWeekBasedYear()) - firstDayOfMonth.get(
                WeekFields.ISO.weekOfWeekBasedYear()
            )) + 1
        val height = dayCellHeight.times(weeks)

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
                .pointerInput(Unit) {
//                    detectTapGestures(
//                        onTap = { tapOffset ->
//                            // When the user taps on the Canvas, you can
//                            // check if the tap offset is in one of the
//                            // tracked Rects.
//                            var index = 0
//                            for (rect in dotRects) {
//                                if (rect.contains(tapOffset)) {
//                                    // Handle the click here and do
//                                    // some action based on the index
//                                    break // don't need to check other points,
//                                    // so break
//                                }
//                                index++
//                            }
//                        }
//                    )
                }
                .drawWithCache {
                    val cellWidth = size.width / 7
                    val cellPositions =
                        ((firstDayOfMonth.dayOfMonth - 1)..<lastDateOfMonth.dayOfMonth).map { dayOfMonth ->
                            val week = firstDayOfMonth
                                .plusDays(dayOfMonth.toLong())
                                .get(WeekFields.ISO.weekOfWeekBasedYear())
                            val dayOfWeek =
                                firstDayOfMonth
                                    .plusDays(dayOfMonth.toLong())
                                    .get(WeekFields.ISO.dayOfWeek())
                            val weekOffset = (week - firstWeek)
                            val x = cellWidth * (dayOfWeek - 1)
                            val y = cellHeightInPix * weekOffset

                            Offset(x, y)
                        }

                    onDrawBehind {
                        for (dayOfMonth in (firstDayOfMonth.dayOfMonth - 1)..<lastDateOfMonth.dayOfMonth) {
                            val (x, y) = cellPositions[dayOfMonth]
                            if (dayOfMonth <= 9) {
                                singleDigitDayLayoutRes?.size?.let {
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
        ) {

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
    val lastDayOfMonth: LocalDate,
)