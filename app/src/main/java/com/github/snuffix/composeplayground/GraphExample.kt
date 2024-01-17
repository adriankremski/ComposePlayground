package com.github.snuffix.composeplayground

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun GraphExample() {
    val values = remember {
        (List(5) {
            Random.nextInt(20) to 100 - Random.nextInt(20)
        } +
                List(5) {
                    20 + Random.nextInt(20) to 80 - Random.nextInt(20)
                } +
                List(5) {
                    40 + Random.nextInt(20) to 60 - Random.nextInt(20)
                } +
                List(5) {
                    60 + Random.nextInt(20) to 20 - Random.nextInt(20)
                } +
                List(5) {
                    80 + Random.nextInt(20) to 20 - Random.nextInt(20)
                })
            .sortedBy { it.first }
    }

    val gridColor = Color.Black
    val animation = remember {
        Animatable(0f)
    }

    LaunchedEffect(key1 = values) {
        animation.animateTo(
            targetValue = 1f,
            animationSpec = tween(3000)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Canvas(
            modifier = Modifier
                .padding(8.dp)
                .aspectRatio(3f / 2f)
                .fillMaxSize()
                .align(Alignment.Center)
        ) {
                val barWidthInPx = 1.dp.toPx()
                val graphWidthInPx = 1.dp.toPx()
                drawRect(gridColor, style = Stroke(barWidthInPx))

                val horizontalLines = 4
                val horizontalLineSpacing = size.height / horizontalLines

                repeat(horizontalLines) { i ->
                    val y = i * horizontalLineSpacing
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = barWidthInPx
                    )
                }

                val verticalLines = 5
                val verticalLinesSpacing = size.width / verticalLines
                repeat(verticalLines) { i ->
                    val x = i * verticalLinesSpacing
                    drawLine(
                        color = gridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = barWidthInPx
                    )
                }

            clipRect(right = size.width * animation.value) {
                val path = Path()
                path.moveTo(0f, size.height)

                values.forEachIndexed { index, value ->
                    val (x, y) = value

                    fun toXPixels(value: Int) = value.toFloat() / 100f * size.width
                    fun toYPixels(value: Int) = value.toFloat() / 100f * size.height

                    val xPixels = toXPixels(x)
                    val yPixels = toYPixels(y)

                    val conX1 = (toXPixels(values.getOrNull(index - 1)?.first ?: 0)  + xPixels) / 2
                    val conY1 = toYPixels(values.getOrNull(index - 1)?.second ?: 0)
                    val conX2 = conX1
                    val conY2 = yPixels

                    path.cubicTo(
                        conX1,
                        conY1,
                        conX2,
                        conY2,
                        xPixels,
                        yPixels
                    )
                }

                path.lineTo(size.width, 0f)

                val filledPath = Path()
                filledPath.addPath(path)
                filledPath.lineTo(size.width, size.height)
                filledPath.lineTo(0f, size.height)
                filledPath.close()

                val brush = Brush.verticalGradient(listOf(
                    Color.Green.copy(alpha = .4f),
                    Color.Transparent
                ))

                drawPath(
                    path = filledPath,
                    brush = brush,
                )

                drawPath(
                    path = path,
                    color = Color.Green,
                    style = Stroke(2.dp.toPx())
                )
            }
        }
    }
}