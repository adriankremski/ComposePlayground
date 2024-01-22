package com.github.snuffix.composeplayground.calendar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch

@Composable
fun MonthsCalendarView(
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
        items(
            count = calendarData.calendarData.size,
            key = { index -> calendarData.calendarData[index].index }
        ) {
            val month = calendarData.calendarData[it]
            MonthView(
                month = month,
                calendarModifier = calendarModifier,
                onMonthSelected = { _, _ ->

                })
        }
    }
}