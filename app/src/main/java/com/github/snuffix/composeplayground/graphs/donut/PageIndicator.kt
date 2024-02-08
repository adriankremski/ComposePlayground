package com.github.snuffix.composeplayground.graphs.donut

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PageIndicator(
    count: Int,
    selectedPage: Int,
    modifier: Modifier = Modifier,
    spacing: Dp = 10.dp,
) {
    val dotWidth = 10.dp
    val dotHeight = 10.dp
    val inactiveColor = Color.Gray.copy(alpha = 0.1f)
    val activeColor = Color.White

    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(count) {
                Box(
                    modifier = Modifier
                        .size(width = dotWidth, height = dotHeight)
                        .background(
                            color = if (it == selectedPage) activeColor else inactiveColor,
                            shape = RoundedCornerShape(dotWidth / 2)
                        )
                )
            }
        }
    }
}