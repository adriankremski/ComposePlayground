package com.github.snuffix.composeplayground.calendar_list

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateRectAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class Day(val text: String) {
    Sunday("SUN"),
    Monday("MON"),
    Tuesday("TUE"),
    Wednesday("WED"),
    Thursday("THU"),
    Friday("FRI"),
    Saturday("SAT");
}

data class DateModel(val day: Day, val number: Int)

private val days = listOf(
    DateModel(Day.Sunday, 1),
    DateModel(Day.Monday, 2),
    DateModel(Day.Tuesday, 3),
    DateModel(Day.Wednesday, 4),
    DateModel(Day.Thursday, 5),
    DateModel(Day.Friday, 6),
    DateModel(Day.Saturday, 7),
    DateModel(Day.Sunday, 8),
    DateModel(Day.Monday, 9),
    DateModel(Day.Tuesday, 10),
    DateModel(Day.Wednesday, 11),
    DateModel(Day.Thursday, 12),
    DateModel(Day.Friday, 13),
    DateModel(Day.Saturday, 14),
    DateModel(Day.Sunday, 15),
    DateModel(Day.Monday, 16),
    DateModel(Day.Tuesday, 17),
    DateModel(Day.Wednesday, 18),
    DateModel(Day.Thursday, 19),
    DateModel(Day.Friday, 20),
    DateModel(Day.Saturday, 21),
    DateModel(Day.Sunday, 22),
    DateModel(Day.Monday, 23),
    DateModel(Day.Tuesday, 24),
    DateModel(Day.Wednesday, 25),
    DateModel(Day.Thursday, 26),
    DateModel(Day.Friday, 27),
    DateModel(Day.Saturday, 28),
    DateModel(Day.Sunday, 29),
    DateModel(Day.Monday, 30),
    DateModel(Day.Tuesday, 31),
)

val selectedTop = Color(0xFF565F6D)
val selectedBottom = Color(0xFF748399)

val unselectedTop1 = Color(0xFF141D21)
val unselectedBottom1 = Color(0xFF1A2128)

val unselectedTop2 = Color(0xFF232930)
val unselectedBottom2 = Color(0xFF2B3138)

@Composable
fun CalendarList() {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val padding = 2.dp
    val cellWidth = (screenWidth / 5) - padding
    val cellWidthPx = with(LocalDensity.current) { cellWidth.toPx() }
    var selectedDay by remember { mutableIntStateOf(2) }
    val listState = rememberLazyListState()

    LaunchedEffect(key1 = selectedDay) {

        val scrollOffset =
            listState.firstVisibleItemIndex * cellWidthPx + listState.firstVisibleItemScrollOffset

        val targetOffset = (selectedDay * cellWidthPx - 2 * cellWidthPx - selectedDay / 2) - 9

        if (targetOffset > scrollOffset) {
            listState.animateScrollBy(
                value = targetOffset - scrollOffset,
                animationSpec = tween(durationMillis = 500)
            )
        } else {
            listState.animateScrollBy(
                value = targetOffset - scrollOffset,
                animationSpec = tween(durationMillis = 500)
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            state = listState
        ) {
            items(
                count = days.size,
                key = {
                    days[it].number
                }) { index ->

                val background = if (selectedDay == index) {
                    Brush.linearGradient(
                        colors = listOf(selectedTop, selectedBottom)
                    )
                } else if (selectedDay % 2 == 1) {
                    if (index % 2 == 0) {
                        Brush.linearGradient(
                            colors = listOf(unselectedTop1, unselectedBottom1)
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(unselectedTop2, unselectedBottom2)
                        )
                    }
                } else if (selectedDay % 2 == 0) {
                    if (index % 2 == 0) {
                        Brush.linearGradient(
                            colors = listOf(unselectedTop2, unselectedBottom2)
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(unselectedTop1, unselectedBottom1)
                        )
                    }
                } else {
                    throw IllegalStateException("Invalid state $selectedDay $index")
                }

                val heightInDp = animateDpAsState(
                    targetValue = if (index == selectedDay) 140.dp else 120.dp,
                    animationSpec = tween(
                        durationMillis = 250,
                    )
                )

                val shape = cellShape(index, selectedDay)

                Box(
                    modifier = Modifier
                        .width(cellWidth)
                ) {
                    Box(
                        modifier = Modifier
                            .width(cellWidth)
                            .height(heightInDp.value)
                            .background(background, shape)
                            .clickable {
                                selectedDay = index
                            },
                    )
                    Box(
                        modifier = Modifier
                            .width(cellWidth)
                            .height(120.dp)
                            .padding(
                                top = if (index == selectedDay) 20.dp else 0.dp,
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(bottom = if (index == selectedDay) 5.dp else 0.dp),
                        ) {
                            val textColor =
                                animateColorAsState(targetValue = if (selectedDay == index) Color.White else selectedBottom)

                            Text(
                                text = days[index].day.text,
                                modifier = Modifier.fillMaxWidth(),
                                color = textColor.value,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = days[index].number.toString(),
                                modifier = Modifier.fillMaxWidth(),
                                color = textColor.value,
                                textAlign = TextAlign.Center,
                                fontSize = 40.sp
                            )

                            if (selectedDay == index) {
                                Text(
                                    text = "APRIL",
                                    modifier = Modifier.fillMaxWidth(),
                                    color = textColor.value,
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}

@Composable
private fun cellShape(dayIndex: Int, selectedDay: Int): Shape {
    val selectedCornerSizePx = with(LocalDensity.current) { 8.dp.toPx() }

    val targetRect = if (dayIndex == selectedDay) {
        Rect(0f, 0f, selectedCornerSizePx, selectedCornerSizePx)
    } else {
        Rect(0f, 0f, 0f, 0f)
    }

    val animatedRect by animateRectAsState(targetRect)

    return RoundedCornerShape(
        animatedRect.left, animatedRect.top, animatedRect.right, animatedRect.bottom
    )
}