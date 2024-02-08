package com.github.snuffix.composeplayground.graphs.donut

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.github.snuffix.composeplayground.calculatePointAngleOnCircle
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Composable used for drawing a circle that is animated from the center of the donut chart to the final position
 */
@Composable
fun ValueIntermediateCircle(
    modifier: Modifier = Modifier,
    brush: Brush,
    circleRadius: Dp,
    circleTransitionAnimationValue: Float,
    initialPosition: IntOffset,
    finalPosition: IntOffset,
) {
    Box(
        modifier = modifier
            .size(circleRadius)
            .absoluteOffset {
                val circleCenter = Offset(
                    (min(
                        initialPosition.x,
                        finalPosition.x
                    ) + abs(initialPosition.x - finalPosition.x) / 2).toFloat(),
                    (min(
                        initialPosition.y,
                        finalPosition.y
                    ) + abs(initialPosition.y - finalPosition.y) / 2).toFloat()
                )

                val radius =
                    sqrt(
                        (initialPosition.x - circleCenter.x)
                            .toDouble()
                            .pow(2.0) + (initialPosition.y - circleCenter.y)
                            .toDouble()
                            .pow(2.0)
                    )

                val initialPositionAngle = calculatePointAngleOnCircle(
                    point = Offset(
                        initialPosition.x.toFloat(),
                        initialPosition.y.toFloat()
                    ),
                    circleCenter = circleCenter
                )

                val finalPositionAngle = calculatePointAngleOnCircle(
                    point = Offset(
                        finalPosition.x.toFloat(),
                        finalPosition.y.toFloat()
                    ),
                    circleCenter = circleCenter
                )

                val sweepAngle = finalPositionAngle - initialPositionAngle
                val viewAngle = initialPositionAngle + circleTransitionAnimationValue * sweepAngle

                val circleX = circleCenter.x + radius * sin(Math.toRadians(90 - viewAngle))
                val circleY = circleCenter.y + radius * cos(Math.toRadians(90 - viewAngle))

                IntOffset(circleX.toInt(), circleY.toInt())
            }
            .background(
                brush = brush,
                shape = RoundedCornerShape(50)
            )
    ) {

    }
}
