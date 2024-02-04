package com.github.snuffix.composeplayground.weather

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times

val morningBackgroundColor = Color(0xFFD4B689)
val afternoonBackgroundColor = Color(0xFFC99568)
val eveningBackgroundColor = Color(0xFFA0685A)
val nightBackgroundColor = Color(0xFF5C4546)


enum class TimeOfDay(val bgColor: Color) {
    MORNING(morningBackgroundColor),
    AFTERNOON(afternoonBackgroundColor),
    EVENING(eveningBackgroundColor),
    NIGHT(nightBackgroundColor)
}

@Composable
fun WeatherScreen() {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    var selectedTimeOfDay by remember { mutableStateOf(TimeOfDay.MORNING) }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TimeOfDay.entries.forEachIndexed { index, timeOfDay ->
            WeatherForecastView(
                modifier = Modifier.align(Alignment.BottomCenter),
                index = index,
                timeOfDay = timeOfDay,
                screenHeight = screenHeight,
                selectedTimeOfDay = selectedTimeOfDay,
                onSelected = { selectedTimeOfDay = it }
            )
        }
    }
}

@Composable
fun WeatherForecastView(
    modifier: Modifier,
    index: Int,
    timeOfDay: TimeOfDay,
    screenHeight: Dp,
    selectedTimeOfDay: TimeOfDay,
    onSelected: (TimeOfDay) -> Unit
) {
    val viewHeaderHeight = 150.dp

    val viewMaxHeight = if (index == 0) {
        screenHeight
    } else {
        screenHeight - index * 100.dp
    }

    val viewMinHeight = if (index == 0) {
        screenHeight
    } else {
        (TimeOfDay.entries.size - index) * viewHeaderHeight
    }

    var viewHeight by remember {
        mutableStateOf(viewMinHeight)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(viewHeight)
            .background(timeOfDay.bgColor)
            .pointerInput(timeOfDay) {
                detectVerticalDragGestures { change, dragAmount ->
                    change.consume()
                    viewHeight = (viewHeight.value + dragAmount)

                    Log.i("AdrianTest", "${dragAmount}")
                }
            }
            .clickable {
//                onSelected(timeOfDay)
            }
    ) {
    }
}

@Composable
@Preview
fun WeatherScreenPreview() {
    WeatherScreen()
}