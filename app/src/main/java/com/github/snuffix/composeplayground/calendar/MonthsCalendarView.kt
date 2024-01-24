package com.github.snuffix.composeplayground.calendar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch

@Composable
fun MonthsCalendarView(
    modifier: Modifier = Modifier,
    calendarModifier: Modifier = Modifier,
    pivotFractionX: Float = 0f,
    pivotFractionY: Float = 0f,
    selectedMonthKey: String = "",
    calendar: Calendar,
    onDrawBehindDay: DrawScope.(Size, MonthData, DayNumber, AnimationValue) -> Unit = { _, _, _, _ -> },
    onDaySelected: ((MonthData, DayNumber) -> Unit)? = null,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var targetSize by remember { mutableFloatStateOf(0f) }
    val monthScale by animateFloatAsState(targetSize, label = "monthsScaleAnim",
        animationSpec = tween(500))
    val monthsAlpha by animateFloatAsState(targetSize, label = "monthsAlphaAnim",
        animationSpec = tween(500))

    LaunchedEffect(key1 = selectedMonthKey) {
        targetSize = 1f // Scale from 0 to 1f
        scope.launch {
            listState.scrollToItem(calendar.months.indexOfFirst { it.key == selectedMonthKey }
                .takeIf { it >= 0 } ?: 0)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.graphicsLayer {
            alpha = monthsAlpha
            scaleX = monthScale
            scaleY = monthScale
            transformOrigin = TransformOrigin(pivotFractionX, pivotFractionY)
        }
    ) {
        items(
            count = calendar.months.size,
            key = { index -> calendar.months[index].key }
        ) {
            MonthView(
                month = calendar.months[it],
                calendarModifier = calendarModifier,
                onDrawBehindDay = onDrawBehindDay,
                onDaySelected = onDaySelected,
                onMonthSelected = { _, _ ->

                })
        }
    }
}