package com.github.snuffix.composeplayground.graphs.donut

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun LinearProgressChartView(
    modifier: Modifier,
    userBudgetList: List<UserBudgetState>,
    collapsedCircleEndRadiusInDp: Dp,
    getCircleTransitionAnimationValue: (Int) -> Float,
    getLinearProgressAnimationValue: (Int) -> Float,
    onCircleFinalPositionsCalculated: (Int, IntOffset) -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        userBudgetList.forEachIndexed { index, state ->
            val imageWidthWithPadding = with(LocalDensity.current) { 80.dp.toPx() }
            val progressTopPadding = with(LocalDensity.current) { 20.dp.toPx() }

            Column(
                modifier = Modifier
                    .padding(top = 16.dp, end = 16.dp)
                    .onPlaced {
                        onCircleFinalPositionsCalculated(
                            index,
                            IntOffset(
                                (it.positionInParent().x + imageWidthWithPadding).toInt(),
                                (it.positionInParent().y + progressTopPadding).toInt()
                            )
                        )
                    }
            ) {
                Row {
                    UserAvatar(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .graphicsLayer {
                                alpha = getCircleTransitionAnimationValue(index)
                            },
                        color = userBudgetList[index].progressColor
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    ) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp, end = 16.dp)
                                .height(collapsedCircleEndRadiusInDp)
                                .graphicsLayer {
                                    alpha = getCircleTransitionAnimationValue(index)
                                },
                            progress = getLinearProgressAnimationValue(index),
                            strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                            color = userBudgetList[index].progressColor,
                            trackColor = screenBackgroundColor,
                        )

                        UserBudgetText(
                            modifier = Modifier
                                .graphicsLayer {
                                    alpha = getCircleTransitionAnimationValue(index)
                                },
                            budget = getLinearProgressAnimationValue(index) * userBudgetList[index].total,
                        )
                    }
                }
            }
        }
    }
}