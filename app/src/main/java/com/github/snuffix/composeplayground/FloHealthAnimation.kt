//package com.github.snuffix.composeplayground
//
//import android.graphics.PointF
//import android.util.Log
//import androidx.compose.animation.core.Animatable
//import androidx.compose.animation.core.animateOffsetAsState
//import androidx.compose.animation.splineBasedDecay
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.gestures.awaitFirstDown
//import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
//import androidx.compose.foundation.gestures.horizontalDrag
//import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
//import androidx.compose.foundation.gestures.verticalDrag
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.composed
//import androidx.compose.ui.draw.drawBehind
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.drawscope.rotate
//import androidx.compose.ui.graphics.drawscope.scale
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.input.pointer.positionChange
//import androidx.compose.ui.input.pointer.util.VelocityTracker
//import androidx.compose.ui.layout.onPlaced
//import androidx.compose.ui.layout.onSizeChanged
//import androidx.compose.ui.layout.positionInParent
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import kotlinx.coroutines.coroutineScope
//import kotlin.math.atan2
//
//
//data class DaySizeAndPosition(val size: Offset, val position: Offset)
//
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun FloHealthAnimation() {
//    var selectedDay by remember { mutableIntStateOf(4) }
//    val daysSizes by remember { mutableStateOf(Array(7) { 0 to 0 }) }
//    val daysPositions = remember {
//        mutableStateListOf(
//            Offset.Zero,
//            Offset.Zero,
//            Offset.Zero,
//            Offset.Zero,
//            Offset.Zero,
//            Offset.Zero,
//            Offset.Zero
//        )
//    }
//
//    val offset by animateOffsetAsState(targetValue = daysPositions[selectedDay])
//
//    var changeOffset by remember { mutableStateOf(Offset.Zero) }
//
//    Column(
//        modifier = Modifier
//            .background(
//                brush = Brush.verticalGradient(
//                    listOf(
//                        Color.Blue.copy(alpha = 0.1f),
//                        Color.Blue.copy(alpha = 0.2f)
//                    )
//                )
//            )
//            .padding(top = 32.dp)
//            .drawBehind {
//                val (x, y) = offset
//                val (width, height) = daysSizes[selectedDay]
//                val indicatorColor = Color(0xFFf2f6fc)
//
//                drawLine(
//                    color = indicatorColor,
//                    start = Offset(x + width / 2, y + height / 2),
//                    end = this.center,
//                    strokeWidth = 8.dp.toPx()
//                )
//
//                drawCircle(
//                    color = indicatorColor,
//                    radius = width.toFloat() * 1.2f,
//                    center = Offset(x + width / 2, y + height / 2)
//                )
//
//                val angle = angleOf(
//                    p1 = this.center,
//                    p2 = changeOffset
//                )
//
////                rotate(-angle.toFloat()) {
//                    scale(
//                        scaleX = 1f + (changeOffset.x / 400f) * 0.1f,
//                        scaleY = 1f + (changeOffset.y / 400f) * 0.1f,
//                        pivot = Offset(
//                            (this.center.x + changeOffset.x) / 2,
//                            (this.center.y + changeOffset.y) / 2
//                        )
//                    ) {
//                        drawCircle(
//                            color = Color.White,
//                            radius = 400f,
//                            center = this.center
//                        )
//                    }
////                }
//            }
//            .dragGestureHandler(onChange = {
//                changeOffset = it
//            }, onDragEnd = {
//                changeOffset = Offset.Zero
//            }
//            })
//    ) {
//        val state = rememberLazyListState()
//
//        LazyRow(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            state = state,
//            flingBehavior = rememberSnapFlingBehavior(lazyListState = state),
//        ) {
//            //item content
//            items(3) {
//                Row(
//                    modifier = Modifier.fillParentMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    repeat(7) { number ->
//                        val fontWeight = if (number == selectedDay) {
//                            FontWeight.Bold
//                        } else {
//                            FontWeight.Normal
//                        }
//
//                        Text(
//                            text = "${(number + 1) + (it * 7)}",
//                            fontWeight = fontWeight,
//                            textAlign = TextAlign.Center,
//                            modifier = Modifier
//                                .size(20.dp)
//                                .clickable(
//                                    interactionSource = remember { MutableInteractionSource() },
//                                    indication = null,
//                                ) {
//                                    selectedDay = number
//                                }
//                                .onSizeChanged {
//                                    daysSizes[number] = it.width to it.height
//                                }
//                                .onPlaced {
//                                    daysPositions[number] = it.positionInParent()
//                                }
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//private fun Modifier.test(): Modifier = composed {
//    val offsetX = remember { Animatable(0f) } // Add this line
//    pointerInput(Unit) {
//        // Used to calculate a settling position of a fling animation.
//        val decay = splineBasedDecay<Float>(this)
//        // Wrap in a coroutine scope to use suspend functions for touch events and animation.
//        coroutineScope {
//            while (true) {
//                // Wait for a touch down event.
//                val pointerId = awaitPointerEventScope { awaitFirstDown().id }
//                offsetX.stop() // Cancel any on-going animations
//                // Prepare for drag events and record velocity of a fling.
//                val velocityTracker = VelocityTracker()
//                // Wait for drag events.
//                awaitPointerEventScope {
//                    verticalDrag(pointerId) { change ->
//                        val verticalDragOffset = offsetX.value + change.positionChange().y
//                        // Consume the gesture event, not passed to external
//                        Log.i("AdrianTest", "verticalDragOffset: $verticalDragOffset")
//                        if (change.positionChange() != Offset.Zero) change.consume()
//                    }
//                    horizontalDrag(pointerId) { change ->
//                        // Get the drag amount change to offset the item with
//                        val horizontalDragOffset = offsetX.value + change.positionChange().x
//                        Log.i("AdrianTest", "horizontalDragOffset: $horizontalDragOffset")
//                        if (change.positionChange() != Offset.Zero) change.consume()
//                    }
//                }
//            }
//        }
//    }
//}
//
//fun Modifier.dragGestureHandler(
//    onChange: (Offset) -> Unit = {},
//    onDragEnd: () -> Unit = {},
//): Modifier = this.pointerInput(Unit) {
//    detectDragGesturesAfterLongPress(
//        onDrag = { change, offset ->
//            Log.i("AdrianTest", "offset: ${change.position}")
//            onChange(change.position)
//            change.consume()
//        },
//        onDragStart = { offset -> },
//        onDragEnd = { onDragEnd() },
//        onDragCancel = { }
//    )
//}
//
//fun angleOf(p1: Offset, p2: Offset): Double {
//    // NOTE: Remember that most math has the Y axis as positive above the X.
//    // However, for screens we have Y as positive below. For this reason,
//    // the Y values are inverted to get the expected results.
//    val deltaY = (p1.y - p2.y).toDouble()
//    val deltaX = (p2.x - p1.x).toDouble()
//    val result = Math.toDegrees(atan2(deltaY, deltaX))
//    return if (result < 0) 360.0 + result else result
//}
