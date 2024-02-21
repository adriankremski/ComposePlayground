package com.github.snuffix.composeplayground.bottom_menu

import android.graphics.RectF
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.snuffix.composeplayground.process.stepFinishedColor
import kotlinx.coroutines.launch
import kotlin.math.abs

private val screenBackgroundColor = Color(0xFF6ccccb)

data class MenuItem(val imageBitmap: ImageVector)

val unselectedMenuItemColor = Color.LightGray
val selectedMenuItemColor = Color.Black


@Composable
fun BottomMenuScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackgroundColor)

    ) {
        val menuItems = remember {
            listOf(
                MenuItem(Icons.Default.Home),
                MenuItem(Icons.Default.List),
                MenuItem(Icons.Default.Call),
                MenuItem(Icons.Default.Email),
                MenuItem(Icons.Default.Person)
            )
        }

        val menuItemsCount = menuItems.count()
        var selectedItemIndex by remember { mutableIntStateOf(0) }
        var previouslySelectedItemIndex by remember { mutableIntStateOf(0) }

        val menuItemAnimation = remember { Animatable(1f) }
        val scope = rememberCoroutineScope()

        val screenWidth =
            with(LocalContext.current) { resources.displayMetrics.widthPixels.toFloat() }

        val indentationWidth = screenWidth / (menuItemsCount - 2)

        val menuItemPosition =
            indentationWidth / 2 + selectedItemIndex * (screenWidth - indentationWidth / 2) / menuItemsCount

        val xTranslationAnim = animateFloatAsState(
            targetValue = menuItemPosition - indentationWidth / 2,
            label = "",
            animationSpec = tween(
                durationMillis = (600L - 100L * abs((selectedItemIndex - previouslySelectedItemIndex))).toInt(),
                easing = LinearOutSlowInEasing
            )
        )
        val xTranslation = xTranslationAnim.value

        val menuItemsBitmaps = menuItems.map { menuItem ->
            rememberVectorPainter(image = menuItem.imageBitmap)
        }

        val menuIconSize = 60f
        val menuItemBackgroundCircleRadius = menuIconSize * 1.2f
        val indentationHeight = 120f

        Canvas(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(80.dp)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { offset ->
                            menuItems.forEachIndexed { index, _ ->
                                val menuItemPosition =
                                    indentationWidth / 2 + index * (screenWidth - indentationWidth / 2) / menuItemsCount

                                val menuItemRect = RectF(
                                    menuItemPosition - menuItemBackgroundCircleRadius,
                                    indentationHeight / 2 - menuItemBackgroundCircleRadius,
                                    menuItemPosition + menuItemBackgroundCircleRadius,
                                    indentationHeight / 2 + menuItemBackgroundCircleRadius
                                )

                                if (menuItemRect.contains(offset.x, offset.y)) {
                                    scope.launch {
                                        if (selectedItemIndex != index) {
                                            previouslySelectedItemIndex = selectedItemIndex
                                            selectedItemIndex = index
                                            menuItemAnimation.snapTo(0f)
                                            menuItemAnimation.animateTo(
                                                targetValue = 1f,
                                                animationSpec = tween(
                                                    durationMillis = 600,
                                                    easing = LinearOutSlowInEasing
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
        ) {
            drawMenuBackground(indentationWidth, xTranslation, indentationHeight)

            menuItems.forEachIndexed { index, item ->
                val menuItemXPosition =
                    indentationWidth / 2 + index * (screenWidth - indentationWidth / 2) / menuItemsCount

                val circleStartTop = if (index == previouslySelectedItemIndex) {
                    0f - menuItemBackgroundCircleRadius / 2
                } else {
                    indentationHeight / 2
                }

                val circleTargetTop = if (index == selectedItemIndex) {
                    0f - menuItemBackgroundCircleRadius / 2
                } else {
                    indentationHeight / 2
                }

                val circleTop =
                    circleStartTop + (circleTargetTop - circleStartTop) * menuItemAnimation.value

                translate(
                    left = menuItemXPosition - menuItemBackgroundCircleRadius / 2,
                    top = circleTop
                ) {
                    drawCircle(
                        Color.White,
                        menuItemBackgroundCircleRadius,
                        Offset(
                            menuItemBackgroundCircleRadius / 2,
                            menuItemBackgroundCircleRadius / 2
                        )
                    )
                }

                drawMenuIcon(
                    icon = menuItemsBitmaps[index],
                    itemIndex = index,
                    previouslySelectedItemIndex = previouslySelectedItemIndex,
                    iconSize = menuIconSize,
                    indentationHeight = indentationHeight,
                    selectedItemIndex = selectedItemIndex,
                    itemAnimationFraction = menuItemAnimation.value,
                    itemXPosition = menuItemXPosition
                )
            }
        }
    }
}

private fun DrawScope.drawMenuBackground(
    indentationWidth: Float,
    selectedItemIndentationXTranslation: Float,
    indentationHeight: Float
) {
    with(Path()) {
        moveTo(0f, 0f)

        lineTo(selectedItemIndentationXTranslation, 0f)

        val xDistanceToControlPoint = indentationWidth / 5f
        val startSegmentFirstControlPoint = Offset(xDistanceToControlPoint, 0f)
        val startSegmentSecondControlPoint = Offset(xDistanceToControlPoint, indentationHeight)

        cubicTo(
            x1 = startSegmentFirstControlPoint.x + selectedItemIndentationXTranslation,
            y1 = startSegmentFirstControlPoint.y,
            x2 = startSegmentSecondControlPoint.x + selectedItemIndentationXTranslation,
            y2 = startSegmentSecondControlPoint.y,
            x3 = indentationWidth / 2f + selectedItemIndentationXTranslation,
            y3 = indentationHeight
        )

        val endSegmentFirstControlPoint =
            Offset(indentationWidth - xDistanceToControlPoint, indentationHeight)
        val endSegmentSecondControlPoint =
            Offset(indentationWidth - xDistanceToControlPoint, 0f)

        cubicTo(
            x1 = endSegmentFirstControlPoint.x + selectedItemIndentationXTranslation,
            y1 = endSegmentFirstControlPoint.y,
            x2 = endSegmentSecondControlPoint.x + selectedItemIndentationXTranslation,
            y2 = endSegmentSecondControlPoint.y,
            x3 = indentationWidth + selectedItemIndentationXTranslation,
            y3 = 0f
        )

        lineTo(size.width, 0f)
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        close()

        drawPath(this, Color.White)
    }
}

private fun DrawScope.drawMenuIcon(
    icon: VectorPainter,
    itemIndex: Int,
    selectedItemIndex: Int,
    previouslySelectedItemIndex: Int,
    iconSize: Float,
    indentationHeight: Float,
    itemAnimationFraction: Float,
    itemXPosition: Float
) {
    with(icon) {
        val startTop = if (itemIndex == previouslySelectedItemIndex) {
            0f - iconSize / 2
        } else {
            indentationHeight / 2 - iconSize / 2
        }

        val targetTop = if (itemIndex == selectedItemIndex) {
            0f - iconSize / 2
        } else {
            indentationHeight / 2 - iconSize / 2
        }

        val top = startTop + (targetTop - startTop) * itemAnimationFraction

        translate(
            left = itemXPosition - iconSize / 2,
            top = top
        ) {
            draw(
                size = Size(iconSize, iconSize),
                colorFilter = ColorFilter.tint(
                    when (itemIndex) {
                        selectedItemIndex -> {
                            lerp(
                                unselectedMenuItemColor,
                                selectedMenuItemColor,
                                itemAnimationFraction
                            )
                        }

                        previouslySelectedItemIndex -> {
                            lerp(
                                selectedMenuItemColor,
                                unselectedMenuItemColor,
                                itemAnimationFraction
                            )
                        }

                        else -> {
                            unselectedMenuItemColor
                        }
                    }
                )
            )
        }
    }
}