package com.github.snuffix.composeplayground.process

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

val backgroundColor = Color(0xFF51495f)
val stepFinishedColor = Color(0xFF65e1d8)
val stepInProgressColor = Color(0xFFcfaf81)
val stepNotStartedColor = Color(0xFF6e6e7d)


enum class StepState {
    NOT_STARTED,
    IN_PROGRESS,
    FINISHED
}

data class Step(
    val title: String = "",
    val stateText: String = "",
    val state: StepState,
    val color: Color = Color.Transparent,
    val size: Size? = null,
    val icon: ImageVector? = null
)

@Composable
fun ProcessAnimationScreen() {
    val smallIconSize = with(LocalDensity.current) {
        with(14.dp.toPx()) {
            Size(this, this)
        }
    }

    val iconSize = with(LocalDensity.current) {
        with(16.dp.toPx()) {
            Size(this, this)
        }
    }

    var display by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.background(backgroundColor)
    ) {
        var itemsCount by remember { mutableIntStateOf(5) }
        var steps by remember {
            mutableStateOf(createSteps(smallIconSize, iconSize, itemsCount))
        }

        Slider(
            modifier = Modifier.padding(top = 16.dp),
            value = itemsCount.toFloat(),
            onValueChange = {
                display = false
                itemsCount = it.toInt()
                steps = createSteps(smallIconSize, iconSize, itemsCount)
            },
            valueRange = 3.0f..10f,
            colors = SliderDefaults.colors(
                thumbColor = stepFinishedColor,
                activeTrackColor = stepFinishedColor,
            )
        )

        Text(
            "Steps: $itemsCount",
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )

        Button(
            modifier = Modifier
                .padding(16.dp)
                .width(200.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {
                display = true
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = stepInProgressColor,
            )
        ) {
            Text("Start")
        }

        if (display) {
            ProcessWigdet(steps = steps)
        }
    }
}

@Composable
fun ProcessWigdet(steps: List<Step>) {
    Box(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
    ) {
        val iconAnimations = steps.map {
            remember { androidx.compose.animation.core.Animatable(0f) }
        }
        val pathAnimations = steps.map {
            remember { androidx.compose.animation.core.Animatable(0f) }
        }
        val vectorIcons = steps.map { step ->
            step.icon?.let { imageVector ->
                rememberVectorPainter(imageVector)
            }
        }

        var titleVisibility by remember { mutableStateOf(false) }
        var subtitleVisibility by remember { mutableStateOf(false) }

        LaunchedEffect(steps) {
            delay(500) //

            with(steps.zip(iconAnimations)) {
                startAnimationsForSteps(StepState.FINISHED)
                firstOrNull { it.first.state == StepState.NOT_STARTED }
                    ?.second?.animateTo(1f)?.let { listOf(it) } ?: listOf()
                startAnimationsForSteps(StepState.IN_PROGRESS)
                startAnimationsForSteps(StepState.NOT_STARTED)
            }

            titleVisibility = true

            launch {
                delay(300)
                subtitleVisibility = true
            }

            pathAnimations.forEachIndexed { index, animation ->
                launch {
                    delay(100 * index.toLong())
                    animation.animateTo(1f, animationSpec = tween(200))
                }
            }
        }

        Box(
            modifier = Modifier
                .align(alignment = Alignment.Center)
                .padding(20.dp)
                .fillMaxWidth()
                .height(400.dp)
                .drawBehind {
                    val radius = minOf(size.width, size.height) / 2

                    val dashesCount = 150f
                    val dashPortion = 0.5f
                    val gapPortion = 0.5f
                    val circumference = 2f * Math.PI * size.width / 2f
                    val dashPlusGapSize = (circumference / dashesCount).toFloat()

                    val topLeft = Offset(size.width - radius * 2, size.height - radius * 2)

                    val stepAngle = 360 / steps.size
                    val stepAngles = List(steps.size) { (it + 1) * stepAngle }
                    val paddingAngle = 10f

                    steps.forEachIndexed { index, step ->
                        val angle = stepAngles[index]

                        val startAngle = 270f + angle - stepAngle + paddingAngle
                        val sweepAngle = stepAngle - paddingAngle * 2

                        val strokeWidth = 8f
                        val (color, stroke) = if (step.state == StepState.FINISHED) {
                            stepFinishedColor to Stroke(
                                width = strokeWidth,
                                cap = Stroke.DefaultCap,
                                join = StrokeJoin.Round
                            )
                        } else {
                            stepNotStartedColor to Stroke(
                                width = strokeWidth,
                                cap = Stroke.DefaultCap,
                                pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(
                                        dashPlusGapSize * dashPortion,
                                        dashPlusGapSize * gapPortion
                                    ), 0f
                                )
                            )
                        }

                        drawArc(
                            startAngle = startAngle,
                            sweepAngle = sweepAngle * pathAnimations[index].value,
                            color = color,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(radius * 2, radius * 2),
                            style = stroke
                        )


                        val centerCircleX = topLeft.x + radius
                        val centerCircleY = topLeft.y + radius

                        // TODO Fix the calculation
                        val circleStartAngle = 180 - index * stepAngle.toDouble()
                        val circleX = centerCircleX + radius * sin(Math.toRadians(circleStartAngle))
                        val circleY = centerCircleY + radius * cos(Math.toRadians(circleStartAngle))

                        drawCircle(
                            color = step.color,
                            radius = 36f * iconAnimations[index].value,
                            center = Offset(circleX.toFloat(), circleY.toFloat()),
                            style = Stroke(
                                width = 6f,
                            )
                        )

                        val stepSize = step.size ?: Size(0f, 0f)

                        vectorIcons[index]?.let { painter ->
                            translate(
                                circleX.toFloat() - stepSize.width / 2,
                                circleY.toFloat() - stepSize.height / 2
                            ) {
                                if (iconAnimations[index].value == 1f) {
                                    with(painter) {
                                        draw(
                                            size = stepSize,
                                            colorFilter = ColorFilter.tint(step.color)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .align(alignment = Alignment.Center)
                    .wrapContentWidth()
            ) {
                val (title, subtitle) = createRefs()

                steps.lastOrNull { it.state == StepState.IN_PROGRESS || it.state == StepState.FINISHED }
                    ?.let { step ->
                        AnimatedVisibility(
                            modifier = Modifier.constrainAs(title) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            },
                            visible = titleVisibility,
                            enter = fadeIn(
                                animationSpec = tween(
                                    durationMillis = 500
                                )
                            )
                        ) {
                            Text(
                                text = step.title,
                                textAlign = TextAlign.Center,
                                color = Color.White,
                                fontSize = 60.sp,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Light,
                                letterSpacing = 2.sp
                            )
                        }
                        AnimatedVisibility(
                            modifier = Modifier.constrainAs(subtitle) {
                                top.linkTo(title.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            },
                            visible = subtitleVisibility,
                            enter = fadeIn(
                                animationSpec = tween(
                                    durationMillis = 500
                                )
                            ) + slideInVertically(
                                animationSpec = tween(
                                    durationMillis = 500
                                )
                            )
                        ) {
                            Text(
                                text = step.stateText,
                                textAlign = TextAlign.Center,
                                color = step.color,
                                fontSize = 24.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }
            }
        }
    }
}

private fun createSteps(smallIconSize: Size, iconSize: Size, stepsCount: Int): List<Step> =
    listOf(
        Step(
            state = StepState.FINISHED,
            color = stepFinishedColor,
            size = smallIconSize,
            icon = Icons.Filled.Done
        ),
        Step(
            state = StepState.FINISHED,
            color = stepFinishedColor,
            size = smallIconSize,
            icon = Icons.Filled.Done
        ),
        Step(
            state = StepState.IN_PROGRESS,
            color = stepInProgressColor,
            size = iconSize,
            icon = Icons.Filled.Edit,
            title = "Cleaning",
            stateText = "IN PROGRESS"
        ),
    ) + List(stepsCount - 3) {
        Step(state = StepState.NOT_STARTED, color = stepNotStartedColor)
    }

private suspend fun List<Pair<Step, androidx.compose.animation.core.Animatable<Float, AnimationVector1D>>>.startAnimationsForSteps(
    step: StepState
) {
    filter { it.first.state == step }.forEach {
        it.second.animateTo(1f)
    }
}