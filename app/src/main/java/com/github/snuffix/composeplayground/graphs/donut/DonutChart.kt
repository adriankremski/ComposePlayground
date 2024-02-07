package com.github.snuffix.composeplayground.graphs.donut

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.github.snuffix.composeplayground.calculatePointAngleOnCircle
import com.github.snuffix.composeplayground.isPointOnArc
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


// Spacing between each slice of the pie chart.
private const val DividerLengthInDegrees = 0f

/**
 * A donut chart that animates when loaded.
 */
data class UserBudgetState(
    val proportion: Float,
    val progressColor: Color,
    val donutGraphBrush: Brush,
    val value: Float,
    val total: Float,
    val userName: String
)

val screenBackgroundColor = Color(0xFF142057)

@Composable
fun DonutChartScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackgroundColor)
    ) {
        var startTransitionAnimation by remember {
            mutableStateOf<Boolean?>(null)
        }

        Box(
            modifier = Modifier
                .padding(16.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF26387f),
                            Color(0xFF1d2f63)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp),
                )
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            startTransitionAnimation = startTransitionAnimation?.let { !it } ?: true
                        }
                    ) { _, _ ->
                    }
                }
        ) {
            val total = 9999f
            ChartView(
                values = listOf(
                    UserBudgetState(
                        0.1f,
                        progressColor = Color(0xFF3F4DB1),
                        donutGraphBrush = donutChartGradient(
                            0.3f to Color(0xFF3F4DB1).copy(alpha = 0.85f),
                            0.8f to Color(0xFF353D94)
                        ),
                        value = 0.1f * total,
                        total = total,
                        userName = "Adrian Kremski"
                    ),
                    UserBudgetState(
                        0.2f,
                        progressColor = Color(0xFFed9b8c),
                        donutChartGradient(
                            0.3f to Color(0xFFed9b8c),
                            0.8f to Color(0xFFb55850)
                        ),
                        value = 0.2f * total,
                        total = total,
                        userName = "John Doe"
                    ),
                    UserBudgetState(
                        0.6f,
                        progressColor = Color(0xFF9AF2D8),
                        donutChartGradient(
                            0.3f to Color(0xFF9AF2D8),
                            0.8f to Color(0xFF5990AA)
                        ),
                        value = 0.6f * total,
                        total = total,
                        userName = "Jane Doe"
                    ),
                    UserBudgetState(
                        0.1f,
                        progressColor = Color(0xFFC05977),
                        donutChartGradient(
                            0.3f to Color(0xFFC05977),
                            0.8f to Color(0xFFA54270)
                        ),
                        value = 0.1f * total,
                        total = total,
                        userName = "John Smith"
                    ),
                ),
                changeScreen = startTransitionAnimation
            )
        }

        Button(
            onClick = {
                startTransitionAnimation = startTransitionAnimation?.let { !it } ?: true
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(
                text = "Animate",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}

fun donutChartGradient(
    vararg colorStops: Pair<Float, Color>,
) = Brush.linearGradient(colorStops = colorStops)

@Composable
fun ChartView(
    values: List<UserBudgetState>,
    modifier: Modifier = Modifier,
    changeScreen: Boolean?
) {
    val screenWidth = with(LocalDensity.current) {
        with(LocalConfiguration.current) { screenWidthDp.dp.toPx() }
    }

    val currentState = remember {
        MutableTransitionState(AnimatedCircleProgress.START)
            .apply { targetState = AnimatedCircleProgress.END }
    }
    val transition = updateTransition(currentState, label = "transition")
    val angleOffset by transition.animateFloat(
        transitionSpec = {
            tween(
                delayMillis = 500,
                durationMillis = 900,
                easing = LinearOutSlowInEasing
            )
        }, label = "angleAnim"
    ) { progress ->
        if (progress == AnimatedCircleProgress.START) {
            0f
        } else {
            360f
        }
    }
    val shift by transition.animateFloat(
        transitionSpec = {
            tween(
                delayMillis = 500,
                durationMillis = 900,
                easing = CubicBezierEasing(0f, 0.75f, 0.35f, 0.85f)
            )
        },
        label = "shiftAnim",
    ) { progress ->
        if (progress == AnimatedCircleProgress.START) {
            0f
        } else {
            values.first().proportion * 360f
        }
    }

    var innerRadius by remember {
        mutableFloatStateOf(0f)
    }

    val circleFinalPositions = remember {
        Array(values.size) { IntOffset.Zero }
    }
    val donutArcTransitionAnimatable = remember {
        Array(values.size) { Animatable(0f) }
    }
    val circleTransitionAnimatable = remember {
        Array(values.size) { Animatable(0f) }
    }
    val linearProgressAnimation = remember {
        Array(values.size) { Animatable(0f) }
    }

    var selectedValueIndex by remember { mutableIntStateOf(0) }
    var screenState by remember { mutableStateOf(ScreenState.DONUT) }

    changeScreen?.let {
        LaunchedEffect(changeScreen) {
            if (changeScreen) {
                screenState = ScreenState.LINEAR

                donutArcTransitionAnimatable.mapIndexed { index, animatable ->
                    launch {
                        animatable.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(
                                durationMillis = 500,
                                delayMillis = index * 300,
                                easing = LinearOutSlowInEasing
                            )
                        )

                        circleTransitionAnimatable[index].animateTo(
                            targetValue = 1f,
                            animationSpec = tween(
                                durationMillis = 1000,
                                easing = LinearOutSlowInEasing,
                            ),
                        )

                        linearProgressAnimation[index].animateTo(
                            targetValue = values[index].proportion,
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = LinearOutSlowInEasing,
                            ),
                        )
                    }
                }.joinAll()

            } else {
                donutArcTransitionAnimatable.mapIndexed { index, animatable ->
                    launch {
                        linearProgressAnimation[index].animateTo(
                            targetValue = 0f,
                            animationSpec = tween(
                                durationMillis = 500,
                                delayMillis = index * 300,
                                easing = LinearOutSlowInEasing,
                            ),
                        )

                        circleTransitionAnimatable[index].animateTo(
                            targetValue = 0f,
                            animationSpec = tween(
                                durationMillis = 1000,
                                easing = LinearOutSlowInEasing,
                            ),
                        )

                        animatable.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(
                                durationMillis = 1000,
                                easing = LinearOutSlowInEasing
                            )
                        )

                    }
                }.joinAll()

                screenState = ScreenState.DONUT
            }
        }
    }


    val collapsedCircleStartRadiusInPx = 60f
    val collapsedCircleStartRadius =
        with(LocalDensity.current) { collapsedCircleStartRadiusInPx.toDp() }
    val collapsedCircleEndRadiusInPx = 20f
    val collapsedCircleEndRadiusInDp =
        with(LocalDensity.current) { collapsedCircleEndRadiusInPx.toDp() }
    val containerHeight = 432.dp
    val donutChartHeight = 300.dp

// The boundaries of each value in the donut chart (start and sweep angles)
    val valueAngleBoundaries = remember { mutableStateOf(Array(values.size) { 0f to 0f }) }
    val canvasSize = remember {
        mutableStateOf(Size.Zero)
    }
    val canvasCenter = remember {
        mutableStateOf(Offset.Zero)
    }

    val stroke = with(LocalDensity.current) { Stroke(100.dp.toPx()) }
    val selectedArcStroke = with(LocalDensity.current) { Stroke(120.dp.toPx()) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(containerHeight)
    ) {
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.TopStart),
            visible = screenState == ScreenState.DONUT,
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
                val userBudgetState = values[selectedValueIndex]
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

                val animatedBudget = animateFloatAsState(targetValue = userBudgetState.proportion * userBudgetState.total)

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

        Canvas(
            modifier
                .align(Alignment.Center)
                .padding(top = 84.dp)
                .fillMaxWidth()
                .height(donutChartHeight)
                .pointerInput(values) {
                    detectTapGestures { tapOffset ->
                        valueAngleBoundaries.value.forEachIndexed { index, it ->
                            val (start, sweep) = it
                            val isOnArc = isPointOnArc(
                                point = tapOffset,
                                arcCenter = Offset(
                                    canvasSize.value.width / 2,
                                    canvasSize.value.height / 2
                                ),
                                radius = innerRadius,
                                startAngle = start,
                                sweepAngle = sweep,
                                arcWidth = stroke.width
                            )

                            if (isOnArc) {
                                selectedValueIndex = index
                            }
                        }
                    }
                }
                .onPlaced {
                    canvasSize.value = Size(it.size.width.toFloat(), it.size.height.toFloat())
                    canvasCenter.value = it.positionInParent()
                }
        ) {
            innerRadius = (size.minDimension - stroke.width) / 2f // padding
            val halfSize = size / 2.0f
            val topLeft = Offset(
                halfSize.width - innerRadius,
                halfSize.height - innerRadius
            )
            val size = Size(innerRadius * 2, innerRadius * 2)
            val strokeDiff = selectedArcStroke.width - stroke.width

            val selectedArcSize = Size(innerRadius * 2 + strokeDiff, innerRadius * 2 + strokeDiff)
            val selectedArcTopLeft = Offset(
                halfSize.width - innerRadius - strokeDiff / 2,
                halfSize.height - innerRadius - strokeDiff / 2
            )

            // Start angle of first value in the graph. By the end of the animation, first value should
            // start from top center
            val startAngle = shift - 90f - values.first().proportion * 360f // start from top center

            values.map { it.proportion }
                .foldIndexed(startAngle) { index, currentValueStartAngle, proportion ->
                    val sweep = proportion * angleOffset

                    val valueStartAngle = currentValueStartAngle + DividerLengthInDegrees / 2
                    val sweepAngle = sweep - DividerLengthInDegrees

                    valueAngleBoundaries.value[index] = valueStartAngle to sweepAngle

                    val arcTransitionProgress = donutArcTransitionAnimatable[index].value

                    if (arcTransitionProgress == 0f) {
                        drawArc(
                            brush = values[index].donutGraphBrush,
                            startAngle = valueStartAngle,
                            sweepAngle = sweepAngle + donutArcTransitionAnimatable[index].value * (360f - sweepAngle),
                            topLeft = if (selectedValueIndex == index) selectedArcTopLeft else topLeft,
                            size = if (selectedValueIndex == index) selectedArcSize else size,
                            useCenter = false,
                            style = if (selectedValueIndex == index) selectedArcStroke else stroke
                        )
                    } else if (arcTransitionProgress < 1f) {
                        val smallTopLeft = Offset(
                            halfSize.width - collapsedCircleStartRadiusInPx,
                            halfSize.height - collapsedCircleStartRadiusInPx
                        )

                        val smallSize =
                            Size(collapsedCircleStartRadiusInPx, collapsedCircleStartRadiusInPx)

                        val animatedTopLeft = lerp(topLeft, smallTopLeft, arcTransitionProgress)
                        val animatesSize = lerp(size, smallSize, arcTransitionProgress)
                        val animatedStroke =
                            Stroke(collapsedCircleStartRadiusInPx + (1f - arcTransitionProgress) * (stroke.width - collapsedCircleStartRadiusInPx))

                        drawArc(
                            brush = values[index].donutGraphBrush,
                            startAngle = valueStartAngle,
                            sweepAngle = sweepAngle + donutArcTransitionAnimatable[index].value * (360f - sweepAngle),
                            topLeft = animatedTopLeft,
                            size = animatesSize,
                            useCenter = false,
                            style = animatedStroke
                        )
                    }

                    currentValueStartAngle + sweep
                }
        }

        val brushes = remember { values.map { it.donutGraphBrush } }

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
        ) {
            brushes.forEachIndexed { index, color ->
                val arcTransitionProgress = donutArcTransitionAnimatable[index].value

                var outerColumnPosition by remember { mutableStateOf(Offset.Zero) }

                val imageWidthWithPadding = with(LocalDensity.current) { 80.dp.toPx() }
                val progressTopPadding = with(LocalDensity.current) { 20.dp.toPx() }

                Column(
                    modifier = Modifier
                        .padding(top = 16.dp, end = 16.dp)
                        .onPlaced {
                            circleFinalPositions[index] = IntOffset(
                                (it.positionInParent().x + imageWidthWithPadding).toInt(),
                                (it.positionInParent().y + progressTopPadding).toInt()
                            )
                        }
                ) {
                    Row {
                        UserAvatar(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .graphicsLayer {
                                    alpha = circleTransitionAnimatable[index].value
                                },
                            color = values[index].progressColor
                        )

                        Column(
                            modifier = Modifier
                                .onPlaced {
                                    outerColumnPosition += it.positionInParent()
                                }
                                .align(Alignment.CenterVertically)
                        ) {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 20.dp, end = 16.dp)
                                    .height(collapsedCircleEndRadiusInDp)
                                    .graphicsLayer {
                                        alpha = circleTransitionAnimatable[index].value
                                    },
                                progress = linearProgressAnimation[index].value,
                                strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                                color = values[index].progressColor,
                                trackColor = screenBackgroundColor,
                            )

                            UserBudgetText(
                                modifier = Modifier
                                    .graphicsLayer {
                                        alpha = circleTransitionAnimatable[index].value
                                    },
                                budget = linearProgressAnimation[index].value * values[index].total,
                            )
                        }
                    }
                }
            }
        }

        // Transition from the collapsed donut chart to the individual progress positions
        brushes.forEachIndexed { index, brush ->
            if (donutArcTransitionAnimatable[index].value == 1f) {
                val circleRadius = lerp(
                    collapsedCircleStartRadius,
                    collapsedCircleEndRadiusInDp,
                    circleTransitionAnimatable[index].value,
                )

                val initialPosition = with(LocalDensity.current) {
                    IntOffset(
                        x = (screenWidth / 2 - collapsedCircleStartRadius.toPx()).toInt(),
                        y = (200.dp.toPx() - collapsedCircleStartRadius.toPx() / 2).toInt()
                    )
                }

                if (circleTransitionAnimatable[index].value < 1f && circleTransitionAnimatable[index].value > 0f) {
                    GraphValueIntermediateView(
                        brush = brush,
                        circleRadius = circleRadius,
                        initialPosition = initialPosition,
//                            x = canvasCenter.value.x.toInt(),
//                            y = canvasCenter.value.y.toInt()
//                        ),
//                        initialPosition = IntOffset(
//                            x = canvasCenter.value.x.toInt(),
//                            y = canvasCenter.value.y.toInt()
//                        ),
                        circleTransitionAnimationValue = circleTransitionAnimatable[index].value,
                        finalPosition = circleFinalPositions[index],
                    )
                }
            }
        }
    }
}

@Composable
fun GraphValueIntermediateView(
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

internal enum class AnimatedCircleProgress { START, END }
internal enum class ScreenState { DONUT, LINEAR }

@Composable
fun UserAvatar(
    modifier: Modifier = Modifier,
    color: Color
) {
    Image(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .size(48.dp),
        painter = rememberVectorPainter(image = Icons.Rounded.AccountCircle),
        contentDescription = "",
        colorFilter = ColorFilter.tint(color),
    )
}

val userBudgetColor = Color(0xFF848dad)

@Composable
fun UserBudgetText(
    modifier: Modifier = Modifier,
    budget: Float,
    textColor: Color = userBudgetColor,
    fontSize: TextUnit = 16.sp
) {
    Text(
        modifier = modifier,
        text = "$ ${
            String.format(
                "%.2f",
                budget
            )
        }",
        fontSize = fontSize,
        color = textColor
    )
}

@Composable
fun CustomLinearProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
    brush: Brush,
    backgroundColor: Color,
    clipShape: Shape = RoundedCornerShape(16.dp)
) {
    Box(
        modifier = modifier
            .clip(clipShape)
            .background(backgroundColor)
            .height(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(brush)
                .fillMaxHeight()
                .fillMaxWidth(progress)
        )
    }
}
