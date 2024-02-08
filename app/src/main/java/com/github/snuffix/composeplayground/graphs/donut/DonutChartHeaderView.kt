package com.github.snuffix.composeplayground.graphs.donut

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DonutChartHeaderView(
    modifier: Modifier = Modifier,
    visible: Boolean,
    userBudgetState: UserBudgetState
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF26387f),
                            Color(0xFF1d2f63)
                        )
                    ),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                )
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                UserAvatar(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    color = userBudgetState.progressColor
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    text = userBudgetState.userName,
                    fontSize = 18.sp,
                    color = userBudgetColor
                )
            }

            val animatedBudget =
                animateFloatAsState(targetValue = userBudgetState.proportion * userBudgetState.total)

            UserBudgetText(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 16.dp),
                budget = animatedBudget.value,
                fontSize = 20.sp,
                textColor = Color.White
            )
        }
    }
}