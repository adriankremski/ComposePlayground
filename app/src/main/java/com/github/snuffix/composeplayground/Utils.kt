package com.github.snuffix.composeplayground

import androidx.compose.ui.geometry.Offset
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

fun isPointOnArc(
    point: Offset,
    arcCenter: Offset,
    radius: Float,
    startAngle: Float,
    sweepAngle: Float,
    arcWidth: Float
): Boolean {
    val distanceToCenter =
        sqrt((point.x - arcCenter.x).toDouble().pow(2.0) + (point.y - arcCenter.y).toDouble().pow(2.0)).toFloat()

    // Check if the point is within the ring defined by the inner radius (r - arcWidth/2) and the outer radius (r + arcWidth/2)
    val innerRadius = radius - arcWidth / 2
    val outerRadius = radius + arcWidth / 2

    if (distanceToCenter in innerRadius..outerRadius) {
        // Calculate the angle between the center of the arc and the given point (x, y)
        val angleToCenter = Math.toDegrees(atan2((point.y - arcCenter.y).toDouble(), (point.x - arcCenter.x).toDouble()))

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

fun calculatePointAngleOnCircle(
    circleCenter: Offset,
    point: Offset,
): Double {
    // Calculate the differences in coordinates
    val dx = point.x - circleCenter.x
    val dy = point.y - circleCenter.y

    // Calculate the angle using arctangent function (atan2)
    // atan2 returns the angle in radians in the range (-π, π]
    val angleRadians = atan2(dy, dx)

    // Convert the angle from radians to degrees
    val angleDegrees = Math.toDegrees(angleRadians.toDouble())

    // Adjust the angle to be positive
    return if (angleDegrees < 0) angleDegrees + 360 else angleDegrees
}
