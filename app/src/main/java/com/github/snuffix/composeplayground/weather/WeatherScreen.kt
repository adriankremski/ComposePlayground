package com.github.snuffix.composeplayground.weather

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
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
    val viewHeaderHeight = 100.dp

    val viewMaxHeight = if (index == 0) {
        screenHeight
    } else {
        screenHeight - index * viewHeaderHeight
    }

    val viewMinHeight = if (index == 0) {
        screenHeight
    } else {
        (TimeOfDay.entries.size - index) * viewHeaderHeight
    }

    val viewHeight = animateDpAsState(
        targetValue = if (selectedTimeOfDay.ordinal >= timeOfDay.ordinal) viewMaxHeight else viewMinHeight,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(viewHeight.value)
            .background(timeOfDay.bgColor)
            .clickable {
                onSelected(timeOfDay)
            }
    ) {
        Row {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
            )
            Column(
                modifier = Modifier
                    .height(viewHeaderHeight)
                    .fillMaxWidth()
                    .weight(0.4f)
                    .background(Color.Red)
            ) {
                Text(text = timeOfDay.name)

            }
        }
    }
}

@Composable
@Preview
fun WeatherScreenPreview() {
    WeatherScreen()
}