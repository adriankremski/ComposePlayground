package com.github.snuffix.composeplayground

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.gestures.verticalDrag
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.sqrt


data class DaySizeAndPosition(val size: Offset, val position: Offset)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FloHealthAnimation() {

    var selectedDay by remember { mutableIntStateOf(4) }
    val daysSizes by remember { mutableStateOf(Array(7) { 0 to 0 }) }
    val daysPositions = remember {
        mutableStateListOf(
            Offset.Zero,
            Offset.Zero,
            Offset.Zero,
            Offset.Zero,
            Offset.Zero,
            Offset.Zero,
            Offset.Zero
        )
    }

    val offset by animateOffsetAsState(targetValue = daysPositions[selectedDay])

    var changeOffset by remember { mutableStateOf(Offset.Zero) }
    runCatching {  }

    Column(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.Blue.copy(alpha = 0.1f),
                        Color.Blue.copy(alpha = 0.2f)
                    )
                )
            )
            .padding(top = 32.dp)
            .drawBehind {
                val (x, y) = offset
                val (width, height) = daysSizes[selectedDay]
                val indicatorColor = Color(0xFFf2f6fc)

                drawLine(
                    color = indicatorColor,
                    start = Offset(x + width / 2, y + height / 2),
                    end = this.center,
                    strokeWidth = 8.dp.toPx()
                )

                drawCircle(
                    color = indicatorColor,
                    radius = width.toFloat() * 1.2f,
                    center = Offset(x + width / 2, y + height / 2)
                )

                val angle = angleOf(
                    p1 = this.center,
                    p2 = changeOffset
                )

//                rotate(-angle.toFloat()) {
                scale(
                    scaleX = 1f + (changeOffset.x / 400f) * 0.1f,
                    scaleY = 1f + (changeOffset.y / 400f) * 0.1f,
                    pivot = Offset(
                        (this.center.x + changeOffset.x) / 2,
                        (this.center.y + changeOffset.y) / 2
                    )
                ) {
                    drawCircle(
                        color = Color.White,
                        radius = 400f,
                        center = this.center
                    )
                }
//                }
            }
            .dragGestureHandler(onChange = {
                changeOffset = it
            }, onDragEnd = {
                changeOffset = Offset.Zero
            })
    ) {
    }
}

private fun Modifier.test(): Modifier = composed {
    val offsetX = remember { Animatable(0f) } // Add this line
    pointerInput(Unit) {
        // Used to calculate a settling position of a fling animation.
        val decay = splineBasedDecay<Float>(this)
        // Wrap in a coroutine scope to use suspend functions for touch events and animation.
        coroutineScope {
            while (true) {
                // Wait for a touch down event.
                val pointerId = awaitPointerEventScope { awaitFirstDown().id }
                offsetX.stop() // Cancel any on-going animations
                // Prepare for drag events and record velocity of a fling.
                val velocityTracker = VelocityTracker()
                // Wait for drag events.
                awaitPointerEventScope {
                    verticalDrag(pointerId) { change ->
                        val verticalDragOffset = offsetX.value + change.positionChange().y
                        // Consume the gesture event, not passed to external
                        Log.i("AdrianTest", "verticalDragOffset: $verticalDragOffset")
                        if (change.positionChange() != Offset.Zero) change.consume()
                    }
                    horizontalDrag(pointerId) { change ->
                        // Get the drag amount change to offset the item with
                        val horizontalDragOffset = offsetX.value + change.positionChange().x
                        Log.i("AdrianTest", "horizontalDragOffset: $horizontalDragOffset")
                        if (change.positionChange() != Offset.Zero) change.consume()
                    }
                }
            }
        }
    }
}

fun Modifier.dragGestureHandler(
    onChange: (Offset) -> Unit = {},
    onDragEnd: () -> Unit = {},
): Modifier = this.pointerInput(Unit) {
    detectDragGesturesAfterLongPress(
        onDrag = { change, offset ->
            Log.i("AdrianTest", "offset: ${change.position}")
            onChange(change.position)
            change.consume()
        },
        onDragStart = { offset -> },
        onDragEnd = { onDragEnd() },
        onDragCancel = { }
    )
}

fun angleOf(p1: Offset, p2: Offset): Double {
    // NOTE: Remember that most math has the Y axis as positive above the X.
    // However, for screens we have Y as positive below. For this reason,
    // the Y values are inverted to get the expected results.
    val deltaY = (p1.y - p2.y).toDouble()
    val deltaX = (p2.x - p1.x).toDouble()
    val result = Math.toDegrees(atan2(deltaY, deltaX))
    return if (result < 0) 360.0 + result else result
}

private fun getAngle(centerX: Float, centerY: Float, touchX: Float, touchY: Float): Double {
    var angle: Double
    val x2 = touchX - centerX
    val y2 = touchY - centerY
    val d1 = Math.sqrt((centerY * centerY).toDouble())
    val d2 = Math.sqrt((x2 * x2 + y2 * y2).toDouble())
    if (touchX >= centerX) {
        angle = Math.toDegrees(Math.acos((-centerY * y2) / (d1 * d2)))
    } else
        angle = 360 - Math.toDegrees(Math.acos((-centerY * y2) / (d1 * d2)))
    return angle
}

@Composable
fun FloHealthAnim2() {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val width = size.width
        val height = size.height

        // Calculate the dimensions of the egg shape
        val centerX = width / 2f
        val bottomY = height / 2f
        val radiusX = width / 2f
        val radiusY = height * 0.75f // Adjust this value to control the shape

        val paint = Paint().apply {
            color = Color.Blue
        }

        val path = Path()

        drawCircle(
            color = Color.Blue,
            radius = 100f,
            center = Offset(centerX, bottomY)
        )

        path.moveTo(centerX, bottomY)

//        clipPath(
//
//        )

//        drawIntoCanvas { canvas ->
//            val paint = android.graphics.Paint()
//            paint.color = android.graphics.Color.BLUE
//
//            // Draw the egg shape using a Path
//            val path = android.graphics.Path().apply {
//                moveTo(centerX, bottomY)
//                addOval(
//                    centerX - radiusX - 40f,
//                    bottomY - radiusY,
//                    centerX + radiusX,
//                    bottomY,
//                    android.graphics.Path.Direction.CW
//                )
//            }
//
//            canvas.nativeCanvas.drawPath(path, paint)
//        }
    }
}

@Composable
fun EggShapeWithClip() {
    var changeOffset by remember { mutableStateOf(Offset.Zero) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .dragGestureHandler(
                onChange = {
                    changeOffset = it
                }, onDragEnd = {
                    changeOffset = Offset.Zero
                })
    ) {
        val width = size.width
        val height = size.height

        // Calculate the dimensions of the egg shape
//        clipPath(path, ClipOp.Difference) {
//            drawCircle(
//                color = Color.Blue,
//                radius = width/2f,
//                center = Offset(centerX, centerY)
//            )
//        }

//        val angle = getAngle(
//            this.center.x, this.center.y, changeOffset.x, changeOffset.y
//        )
//
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = 100f

        fun createGradient(
            startY: Float,
            endY: Float,
        ): Brush {
            return Brush.verticalGradient(
                colors = listOf(
                    Color.Blue,
                    Color.Green
                ),
                startY = startY,
                endY = endY,
            )
        }

        val vector1 = Vector(centerX, centerY - radius)
        val vector2 = Vector(changeOffset.x, changeOffset.y)
        val angle = if (changeOffset.y != 0f && changeOffset.x != 0f) {
            if (changeOffset.x < centerX) {
                360f - calculateAngle(
                    centerX,
                    centerY,
                    vector1,
                    vector2
                )
            } else
                calculateAngle(
                    centerX,
                    centerY,
                    vector1,
                    vector2
                )
        } else 0f

        val result = rotatePoint(
            point = Offset(50f, 50f),
            origin = Offset(50f, 100f),
            angleDegrees = 90.toDouble()
        )

        Log.i("AdrianTest" , "center: ${Offset(centerX, centerY)}")
        Log.i("AdrianTest" , "start: ${Offset(centerX, centerY - radius)}")
        Log.i("AdrianTest" , "end: ${result}")


//            val topLeft = Offset(centerX - radius, startY),
//            val offsetY = rotatePoint(changeOffset, Offset(centerX, centerY), angle.toDouble()).y
            val offsetY = changeOffset.y

            if (offsetY < centerY - radius && offsetY > 0) {
                val startY = changeOffset.y
                val rectHeight = radius + centerY - changeOffset.y
                val endY = startY + rectHeight
//                val topLeftRotated = rotatePoint(topLeft, Offset(centerX, centerY), angle.toDouble())
//                drawCircle(
//                    color = Color.Black,
//                    radius = 10f,
//                    center = topLeftRotated)
//
                val gradient = createGradient(startY = startY, endY = endY)

                val topLeft = Offset(centerX - radius, startY)
                rotate(angle.toFloat(), Offset(centerX, centerY)) {
                    drawRect(
                        brush = gradient,
                        topLeft = topLeft,
                        size = Size(radius * 2, rectHeight)
                    )
                }
            } else if (offsetY > centerY + radius) {
                val startY = centerY - radius
                val rectHeight = radius + changeOffset.y - centerY
                val endY = startY + rectHeight
                val topLeft = Offset(centerX - radius, startY)
                val topLeftRotated = rotatePoint(topLeft, Offset(centerX, centerY), angle.toDouble())
                drawCircle(
                    color = Color.Black,
                    radius = 10f,
                    center = topLeftRotated)

                val gradient = createGradient(startY = startY, endY = endY)

                rotate(angle.toFloat(), Offset(centerX, centerY)) {
                    drawRect(
                        brush = gradient,
                        topLeft = topLeft,
                        size = Size(radius * 2, rectHeight)
                    )
                }
            } else {
                val topLeft = Offset(centerX - radius, centerY - radius)
                val topLeftRotated = rotatePoint( topLeft, Offset(centerX, centerY), angle.toDouble())
                drawCircle(
                    color = Color.Black,
                    radius = 10f,
                    center = topLeftRotated)

                val gradient = createGradient(startY = centerY - radius, endY = centerY + radius)

                rotate(angle.toFloat(), Offset(centerX, centerY)) {
                    drawRect(
                        brush = gradient,
                        topLeft = topLeft,
                        size = Size(radius * 2, radius * 2),
                    )
                }
            }
        }
//        }

//        rotate(rotationAngle.toFloat()) {
//            if (changeOffset.y != 0f) {
//                if (changeOffset.y < centerY) {
//                    Log.i("AdrianTest", "centerY: ${centerY}")
//                    Log.i("AdrianTest", "changeOffset: ${changeOffset.y}")
//                    Log.i("AdrianTest", "radius: ${radius * 0.8f}")
//
//                    Log.i("AdrianTest", ": ${(centerY - changeOffset.y / (radius * 0.8f))}")
//                    topPathValue =
//                        (1.35f * ((centerY - changeOffset.y) / (radius * 0.8f))).coerceAtLeast(1.35f)
//                            .coerceAtMost(1.6f)
//                    bottomPathValue = 1.35f - topPathValue / 10f
//                } else {
//                    bottomPathValue =
//                        (1.35f * ((changeOffset.y - centerY) / (radius * 0.8f))).coerceAtLeast(1.35f)
//                            .coerceAtMost(1.6f)
//                    topPathValue = 1.35f - bottomPathValue /  10f
//                }
//            }
//            val path = Path()
//            path.moveTo(centerX - radius, centerY)
//            path.cubicTo(
//                centerX - radius,
//                centerY - radius * topPathValue,
//                centerX + radius,
//                centerY - radius * topPathValue,
//                centerX + radius,
//                centerY
//            )
//            path.close()
//
//            drawPath(path, Color.Red)
//
//            val path2 = Path()
//            path.moveTo(centerX - radius, centerY)
//            path.cubicTo(
//                centerX - radius,
//                centerY + radius * bottomPathValue,
//                centerX + radius,
//                centerY + radius * bottomPathValue,
//                centerX + radius,
//                centerY
//            )
//            path.close()
//            drawPath(path, Color.Red)
//            drawPath(path2, Color.Red)
//        }

//        drawOval(
//            color = Color.Blue,
//            topLeft = Offset(centerX - radius, centerY - radius),
//            size = Size(radius*2, radius*2),
//        )
//    }
}

data class Vector(val x: Float, val y: Float)

fun calculateAngle(centerX: Float, centerY: Float, vector1: Vector, vector2: Vector): Double {
    // Calculate vectors AB and CD
    val vectorAB = Vector(vector1.x - centerX, vector1.y - centerY)
    val vectorCD = Vector(vector2.x - centerX, vector2.y - centerY)

    // Calculate dot product of AB and CD
    val dotProduct = vectorAB.x * vectorCD.x + vectorAB.y * vectorCD.y

    // Calculate magnitudes of vectors AB and CD
    val magnitudeAB = Math.sqrt((vectorAB.x * vectorAB.x + vectorAB.y * vectorAB.y).toDouble())
    val magnitudeCD = sqrt(vectorCD.x * vectorCD.x + vectorCD.y * vectorCD.y)

    // Calculate the cosine of the angle
    val cosine = dotProduct / (magnitudeAB * magnitudeCD)

    // Calculate the angle in radians
    val angleInRadians = acos(cosine)

    // Convert radians to degrees
    val angleInDegrees = Math.toDegrees(angleInRadians)

    return angleInDegrees
}

fun rotatePoint(point: Offset, origin: Offset, angleDegrees: Double): Offset {
    val angleRadians = Math.toRadians(angleDegrees)
    val angle = angleRadians
    val x = Math.cos(angle) * (point.x - origin.x) - Math.sin(angle) * (point.y - origin.y) + origin.x
    val y = Math.sin(angle) * (point.x - origin.x) + Math.cos(angle) * (point.y - origin.y) + origin.y
    return Offset(x.toFloat(), y.toFloat())
}

