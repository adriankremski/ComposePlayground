package com.github.snuffix.composeplayground.graphs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp

val colorLow = Color(254, 254, 230)
val colorMid1 = Color(247, 206, 167)
val colorMid2 = Color(183, 64, 138)
val colorHigh = Color(67, 20, 119)


@Composable
fun GradientLegend() {
    mapOf(
        0.0f to colorLow,
        0.03f to colorMid1,
        0.08f to colorMid2,
        1.0f to colorHigh
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .align(Alignment.Center)
                .width(100.dp)
                .height(200.dp)
        ) {
            drawGradientLegend(
                Gradients(
                    1.0f to colorHigh,
                    0.08f to colorMid2,
                    0.03f to colorMid1,
                    0.0f to colorLow,
                )
            )
        }
    }
}

fun DrawScope.drawGradientLegend(
    gradients: Gradients,
    colorLegendHeight: Float = size.height,
    colorLegendWidth: Float = size.width,
    colorLegendLeft: Float = 0.dp.toPx(),
) {
    val spanHeight = colorLegendHeight / (gradients.colorStops.size - 1)

    gradients.colorStops.toList().reduceIndexed { index, firstGradient, secondGradient ->
        drawRect(
            brush = Brush.verticalGradient(
                0.0f to firstGradient.second,
                1.0f to secondGradient.second,
                startY = index * spanHeight, endY = (index + 1) * spanHeight
            ),
            topLeft = Offset(x = colorLegendLeft, y = index * spanHeight),
            size = Size(width = colorLegendWidth, height = spanHeight),
            style = Fill
        )

        secondGradient
    }
}