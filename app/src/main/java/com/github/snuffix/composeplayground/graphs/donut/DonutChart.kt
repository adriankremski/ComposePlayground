package com.github.snuffix.composeplayground.graphs.donut

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.github.snuffix.composeplayground.isPointOnArc
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.math.abs


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

private val total = 9999f
private val userBudgetList = listOf(
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
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DonutChartScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackgroundColor)
    ) {
        // This boolean should be an enum probably
        var startTransitionAnimation by remember {
            mutableStateOf<Boolean?>(null)
        }

        var currentPage by remember { mutableStateOf(0) }
        val state = rememberPagerState(initialPage = currentPage) { 2 }
        var dragAmountSum by remember { mutableFloatStateOf(0f) }
        val scope = rememberCoroutineScope()

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
                        onDragStart = {
                            dragAmountSum = 0f
                        },
                        onDragEnd = {
                            if (dragAmountSum < 0) {
                                if (abs(dragAmountSum) >= 200) {
                                    startTransitionAnimation = true
                                    currentPage = 1
                                }
                            } else {
                                if (abs(dragAmountSum) >= 200) {
                                    startTransitionAnimation = false
                                    currentPage = 0
                                }
                            }
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        dragAmountSum += dragAmount
                    }
                }
        ) {
            ChartView(
                changeScreen = startTransitionAnimation,
                userBudgetList = userBudgetList
            )

            PageIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                count = 2,
                selectedPage = currentPage
            )
        }

        Text(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
            text = "Swipe to Animate",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
    }
}

fun donutChartGradient(
    vararg colorStops: Pair<Float, Color>,
) = Brush.linearGradient(colorStops = colorStops)

@Composable
fun ChartView(
    userBudgetList: List<UserBudgetState>,
    modifier: Modifier = Modifier,
    changeScreen: Boolean?
) {
    // Initial donut graph animations launched after the screen is created
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
            userBudgetList.first().proportion * 360f
        }
    }

    var innerRadius by remember {
        mutableFloatStateOf(0f)
    }

    val circleFinalPositions = remember {
        Array(userBudgetList.size) { IntOffset.Zero }
    }

    val donutArcTransitionAnimatable = remember {
        Array(userBudgetList.size) { Animatable(0f) }
    }
    val circleTransitionAnimatable = remember {
        Array(userBudgetList.size) { Animatable(0f) }
    }
    val linearProgressAnimation = remember {
        Array(userBudgetList.size) { Animatable(0f) }
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
                            targetValue = userBudgetList[index].proportion,
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


    val collapsedCircleStartRadius = with(LocalDensity.current) { 60f.toDp() }
    val collapsedCircleEndRadiusInDp = with(LocalDensity.current) { 20f.toDp() }
    val containerHeight = 480.dp
    val donutChartHeight = 300.dp

    // The boundaries of each value in the donut chart (start and sweep angles)
    val valueAngleBoundaries = remember { mutableStateOf(Array(userBudgetList.size) { 0f to 0f }) }
    val donutCanvasSize = remember {
        mutableStateOf(Size.Zero)
    }
    val donutCanvasCenter = remember {
        mutableStateOf(Offset.Zero)
    }

    val donutArcStroke = with(LocalDensity.current) { Stroke(100.dp.toPx()) }
    val selectedDonutArcStroke = with(LocalDensity.current) { Stroke(120.dp.toPx()) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(containerHeight)
    ) {
        DonutChartHeaderView(
            modifier = Modifier
                .align(Alignment.TopStart),
            visible = screenState == ScreenState.DONUT,
            userBudgetState = userBudgetList[selectedValueIndex]
        )

        Canvas(
            modifier
                .align(Alignment.Center)
                .padding(top = 84.dp)
                .fillMaxWidth()
                .height(donutChartHeight)
                .pointerInput(userBudgetList) {
                    detectTapGestures { tapOffset ->
                        valueAngleBoundaries.value.forEachIndexed { index, it ->
                            val (start, sweep) = it
                            val isOnArc = isPointOnArc(
                                point = tapOffset,
                                arcCenter = Offset(
                                    donutCanvasSize.value.width / 2,
                                    donutCanvasSize.value.height / 2
                                ),
                                radius = innerRadius,
                                startAngle = start,
                                sweepAngle = sweep,
                                arcWidth = donutArcStroke.width
                            )

                            if (isOnArc) {
                                selectedValueIndex = index
                            }
                        }
                    }
                }
                .onPlaced {
                    donutCanvasSize.value = Size(it.size.width.toFloat(), it.size.height.toFloat())
                    donutCanvasCenter.value = Offset(
                        x = it.positionInParent().x + it.size.width / 2,
                        y = it.positionInParent().y + it.size.height / 2
                    )
                }
        ) {
            innerRadius = (size.minDimension - donutArcStroke.width) / 2f // padding
            val halfSize = size / 2.0f
            val topLeft = Offset(
                halfSize.width - innerRadius,
                halfSize.height - innerRadius
            )
            val size = Size(innerRadius * 2, innerRadius * 2)
            val strokeDiff = selectedDonutArcStroke.width - donutArcStroke.width

            val selectedArcSize = Size(innerRadius * 2 + strokeDiff, innerRadius * 2 + strokeDiff)
            val selectedArcTopLeft = Offset(
                halfSize.width - innerRadius - strokeDiff / 2,
                halfSize.height - innerRadius - strokeDiff / 2
            )

            // Start angle of first value in the graph. By the end of the animation, first value should
            // start from top center
            val startAngle =
                shift - 90f - userBudgetList.first().proportion * 360f // start from top center

            userBudgetList.map { it.proportion }
                .foldIndexed(startAngle) { index, currentValueStartAngle, proportion ->
                    val sweep = proportion * angleOffset

                    val valueStartAngle = currentValueStartAngle + DividerLengthInDegrees / 2
                    val sweepAngle = sweep - DividerLengthInDegrees

                    valueAngleBoundaries.value[index] = valueStartAngle to sweepAngle

                    val arcTransitionProgress = donutArcTransitionAnimatable[index].value

                    if (arcTransitionProgress == 0f) {
                        // Animation is not started
                        drawArc(
                            brush = userBudgetList[index].donutGraphBrush,
                            startAngle = valueStartAngle,
                            sweepAngle = sweepAngle + donutArcTransitionAnimatable[index].value * (360f - sweepAngle),
                            topLeft = if (selectedValueIndex == index) selectedArcTopLeft else topLeft,
                            size = if (selectedValueIndex == index) selectedArcSize else size,
                            useCenter = false,
                            style = if (selectedValueIndex == index) selectedDonutArcStroke else donutArcStroke
                        )
                    } else if (arcTransitionProgress < 1f) {
                        // Animation is running
                        val collapsedCircleStartRadiusInPx = collapsedCircleStartRadius.toPx()
                        val smallTopLeft = Offset(
                            halfSize.width - collapsedCircleStartRadiusInPx,
                            halfSize.height - collapsedCircleStartRadiusInPx
                        )

                        val smallSize =
                            Size(collapsedCircleStartRadiusInPx, collapsedCircleStartRadiusInPx)

                        val animatedTopLeft = lerp(topLeft, smallTopLeft, arcTransitionProgress)
                        val animatesSize = lerp(size, smallSize, arcTransitionProgress)
                        val animatedStroke =
                            Stroke(collapsedCircleStartRadiusInPx + (1f - arcTransitionProgress) * (donutArcStroke.width - collapsedCircleStartRadiusInPx))

                        drawArc(
                            brush = userBudgetList[index].donutGraphBrush,
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

        LinearProgressChartView(
            modifier = Modifier
                .align(Alignment.TopStart),
            userBudgetList = userBudgetList,
            collapsedCircleEndRadiusInDp = collapsedCircleEndRadiusInDp,
            getCircleTransitionAnimationValue = { index -> circleTransitionAnimatable[index].value },
            onCircleFinalPositionsCalculated = { index, finalPosition ->
                circleFinalPositions[index] = finalPosition
            },
            getLinearProgressAnimationValue = { index -> linearProgressAnimation[index].value },
        )

        // Transition from the collapsed donut chart to the individual progress positions
        userBudgetList.forEachIndexed { index, state ->
            val isDonutChartCollapsed = donutArcTransitionAnimatable[index].value == 1f

            // Donut chart collapse animation is finished
            // Animate individual circles from center of donut chart to the final position
            if (isDonutChartCollapsed) {
                val circleRadius = lerp(
                    collapsedCircleStartRadius,
                    collapsedCircleEndRadiusInDp,
                    circleTransitionAnimatable[index].value,
                )

                val initialPosition = with(LocalDensity.current) {
                    IntOffset(
                        x = donutCanvasCenter.value.x.toInt() - collapsedCircleStartRadius.toPx()
                            .toInt(),
                        y = donutCanvasCenter.value.y.toInt() - collapsedCircleStartRadius.toPx()
                            .toInt()
                    )
                }

                val isCircleTransitionAnimationRunning =
                    circleTransitionAnimatable[index].value > 0f && circleTransitionAnimatable[index].value < 1f

                // If circles transition is not running, don't draw them
                if (isCircleTransitionAnimationRunning) {
                    ValueIntermediateCircle(
                        brush = state.donutGraphBrush,
                        circleRadius = circleRadius,
                        initialPosition = initialPosition,
                        circleTransitionAnimationValue = circleTransitionAnimatable[index].value,
                        finalPosition = circleFinalPositions[index],
                    )
                }
            }
        }
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
