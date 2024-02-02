package com.github.snuffix.composeplayground.graphs

import android.util.Log
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

// Spacing between each slice of the pie chart.
private const val DividerLengthInDegrees = 1.8f

/**
 * A donut chart that animates when loaded.
 */
@Composable
fun DonutChart(
    proportions: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val currentState = remember {
        MutableTransitionState(AnimatedCircleProgress.START)
            .apply { targetState = AnimatedCircleProgress.END }
    }
    val stroke = with(LocalDensity.current) { Stroke(30.dp.toPx()) }
    val selectedArcStroke = with(LocalDensity.current) { Stroke(40.dp.toPx()) }
    val transition = updateTransition(currentState, label = "transition")
    val angleOffset by transition.animateFloat(
        transitionSpec = {
            tween(
                delayMillis = 500,
                durationMillis = 900,
                easing = LinearOutSlowInEasing
            )
        }, label = "angleAnim"
    ) { progress ->
        if (progress == AnimatedCircleProgress.START) {
            0f
        } else {
            360f
        }
    }
    val shift by transition.animateFloat(
        transitionSpec = {
            tween(
                delayMillis = 500,
                durationMillis = 900,
                easing = CubicBezierEasing(0f, 0.75f, 0.35f, 0.85f)
            )
        },
        label = "shiftAnim",
    ) { progress ->
        if (progress == AnimatedCircleProgress.START) {
            0f
        } else {
            proportions.first() * 360f
        }
    }

    val canvasSize = remember {
        mutableStateOf(Size.Zero)
    }

    val angles = remember { mutableStateOf(Array(proportions.size) { 0f to 0f }) }
    var innerRadius by remember {
        mutableFloatStateOf(0f)
    }

    var selectedArc by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier.fillMaxWidth().height(400.dp)
    ) {
        Canvas(modifier
            .fillMaxSize()
            .pointerInput(proportions) {
                detectTapGestures(
                    onTap = { tapOffset ->
                        angles.value.forEachIndexed { index, it ->
                            val (start, sweep) = it
                            val isOnArc = isPointOnArc(
                                x = tapOffset.x,
                                y = tapOffset.y,
                                cx = canvasSize.value.width / 2,
                                cy = canvasSize.value.height / 2,
                                radius = innerRadius,
                                startAngle = start,
                                sweepAngle = sweep,
                                arcWidth = stroke.width
                            )

                            if (isOnArc) {
                                selectedArc = index
                            }
                        }
                    }
                )
            }
            .onPlaced {
                canvasSize.value = Size(it.size.width.toFloat(), it.size.height.toFloat())
            }
        ) {
            innerRadius = (size.minDimension - stroke.width) / 2 // padding
            val halfSize = size / 2.0f
            val topLeft = Offset(
                halfSize.width - innerRadius,
                halfSize.height - innerRadius
            )
            val size = Size(innerRadius * 2, innerRadius * 2)

            // Start angle of first value in the graph. By the end of the animation, first value should
            // start from top center
            val startAngle = shift - 90f - proportions.first() * 360f // start from top center

            proportions.foldIndexed(startAngle) { index, currentValueStartAngle, proportion ->
                val sweep = proportion * angleOffset

                val valueStartAngle = currentValueStartAngle + DividerLengthInDegrees / 2
                val sweepAngle = sweep - DividerLengthInDegrees

                angles.value[index] = valueStartAngle to sweepAngle

                drawArc(
                    color = colors[index],
                    startAngle = valueStartAngle,
                    sweepAngle = sweepAngle,
                    topLeft = topLeft,
                    size = size,
                    useCenter = false,
                    style = if (selectedArc == index) selectedArcStroke else stroke
                )

                currentValueStartAngle + sweep
            }
        }

        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Selected Arc: $selectedArc",
            color = colors[selectedArc],
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp
        )
    }
}

fun isPointOnArc(
    x: Float,
    y: Float,
    cx: Float,
    cy: Float,
    radius: Float,
    startAngle: Float,
    sweepAngle: Float,
    arcWidth: Float
): Boolean {
    val distanceToCenter =
        sqrt((x - cx).toDouble().pow(2.0) + (y - cy).toDouble().pow(2.0)).toFloat()

    // Check if the point is within the ring defined by the inner radius (r - arcWidth/2) and the outer radius (r + arcWidth/2)
    val innerRadius = radius - arcWidth / 2
    val outerRadius = radius + arcWidth / 2

    if (distanceToCenter in innerRadius..outerRadius) {
        // Calculate the angle between the center of the arc and the given point (x, y)
        val angleToCenter = Math.toDegrees(atan2((y - cy).toDouble(), (x - cx).toDouble()))

        // Ensure the angle is within the range [0, 360)
        val normalizedAngleToCenter = if (angleToCenter < 0) angleToCenter + 360 else angleToCenter

        // Ensure the startAngle is within the range [0, 360)
        val normalizedStartAngle = if (startAngle < 0) startAngle + 360 else startAngle

        // Calculate the angle relative to the startAngle
        val angleFromStart = (normalizedAngleToCenter - normalizedStartAngle + 360) % 360

        // Check if the angle is within the sweep angle of the arc
        return angleFromStart <= sweepAngle
    }

    return false
}

private enum class AnimatedCircleProgress { START, END }
