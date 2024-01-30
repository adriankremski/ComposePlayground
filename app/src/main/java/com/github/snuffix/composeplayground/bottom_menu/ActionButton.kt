package com.github.snuffix.composeplayground.bottom_menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.github.snuffix.composeplayground.R

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .drawBehind {
                fun drawHalfCircle(
                    color: Color,
                    startAngle: Float,
                    sweepAngle: Float,
                ) {
                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, size.height)
                    )
                }

                // Top half circle
                drawHalfCircle(
                    color = screenBackgroundColor,
                    startAngle = 180f,
                    sweepAngle = 180f,
                )

                // Bottom half circle
                drawHalfCircle(
                    color = widgetBackgroundColor,
                    startAngle = 0f,
                    sweepAngle = 180f,
                )
            }
            .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(50))
            .clickable(interactionSource, null) {
                onClick()
            }
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.Center)
                .then(imageModifier)
                .drawBehind {
                    // Couldn't find icon with white background
                    drawCircle(
                        color = Color.White,
                        radius = 70f,
                        center = Offset(size.width / 2, size.height / 2)
                    )
                },
            painter = painterResource(id = R.drawable.camera),
            colorFilter = ColorFilter.tint(buttonColor),
            contentDescription = null,
        )
    }
}

