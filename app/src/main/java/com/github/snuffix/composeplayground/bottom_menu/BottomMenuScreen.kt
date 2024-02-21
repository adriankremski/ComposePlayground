package com.github.snuffix.composeplayground.bottom_menu

import android.graphics.RectF
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
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

        val menuItemYPositionAnimation = remember { Animatable(1f) }
        val menuItemXPositionAnimation = remember { Animatable(1f) }

        val indentationHeightFraction = remember { Animatable(0f) }
        val scope = rememberCoroutineScope()

        val screenWidth =
            with(LocalContext.current) { resources.displayMetrics.widthPixels.toFloat() }

        val indentationWidth = screenWidth / (menuItemsCount - 2) - with(
            LocalDensity.current
        ) { 20.dp.toPx() }

        val menuItemPosition =
            indentationWidth / 2 + selectedItemIndex * (screenWidth - indentationWidth / 2) / menuItemsCount

        // Check if the selected item is on the opposite edge of the previously selected item
        val indexDifference = abs(selectedItemIndex - previouslySelectedItemIndex)
        val animatingFromOpposedEdge = indexDifference == menuItems.size - 1

        val xTranslationAnim = animateFloatAsState(
            targetValue = menuItemPosition - indentationWidth / 2,
            label = "",
            animationSpec = tween(
                durationMillis = if (animatingFromOpposedEdge) {
                    400
                } else {
                    200 + indexDifference * 50
                },
                easing = LinearOutSlowInEasing
            )
        )
        val selectedItemIndentationXTranslation = xTranslationAnim.value

        val menuItemsBitmaps = menuItems.map { menuItem ->
            rememberVectorPainter(image = menuItem.imageBitmap)
        }
        val menuIconSize = with(LocalDensity.current) { 24.dp.toPx() }
        val menuItemBackgroundCircleRadius = with(LocalDensity.current) { 28.dp.toPx() }
        val indentationHeight = with(LocalDensity.current) { 35.dp.toPx() }
        val menuHeight = 60.dp

        Canvas(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(menuHeight)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    fun getTouchedItemIndex(offset: Offset): Int? {
                        return menuItems
                            .filterIndexed { index, _ ->
                                val menuItemPosition =
                                    indentationWidth / 2 + index * (screenWidth - indentationWidth / 2) / menuItemsCount

                                val menuItemRect = RectF(
                                    menuItemPosition - menuItemBackgroundCircleRadius,
                                    indentationHeight / 2 - menuItemBackgroundCircleRadius,
                                    menuItemPosition + menuItemBackgroundCircleRadius,
                                    indentationHeight / 2 + menuItemBackgroundCircleRadius
                                )

                                menuItemRect.contains(offset.x, offset.y)
                            }
                            .firstOrNull()
                            ?.let {
                                menuItems.indexOf(it)
                            }
                    }

                    detectTapGestures(
                        onTap = { offset ->
                            getTouchedItemIndex(offset)?.let { index ->
                                if (selectedItemIndex != index) {
                                    scope.launch {
                                        previouslySelectedItemIndex = selectedItemIndex
                                        selectedItemIndex = index

                                        if (abs(selectedItemIndex - previouslySelectedItemIndex) == menuItemsCount - 1) {
                                            launch {
                                                menuItemYPositionAnimation.snapTo(0f)
                                                menuItemYPositionAnimation.animateTo(
                                                    targetValue = 1f,
                                                    animationSpec = tween(
                                                        delayMillis = 200,
                                                        durationMillis = 400,
                                                        easing = LinearOutSlowInEasing
                                                    )
                                                )
                                            }
                                            launch {
                                                menuItemXPositionAnimation.snapTo(0f)
                                                menuItemXPositionAnimation.animateTo(
                                                    targetValue = 1f,
                                                    animationSpec = tween(
                                                        delayMillis = 200,
                                                        durationMillis = 700,
                                                    )
                                                )
                                            }
                                            indentationHeightFraction.snapTo(0f)
                                            indentationHeightFraction.animateTo(
                                                targetValue = 1.5f,
                                                animationSpec = tween(
                                                    durationMillis = 200,
                                                )
                                            )
                                            indentationHeightFraction.animateTo(
                                                targetValue = 0f,
                                                animationSpec = tween(
                                                    delayMillis = 200,
                                                    durationMillis = 200,
                                                )
                                            )
                                        } else {
                                            launch {
                                                menuItemYPositionAnimation.snapTo(0f)
                                                menuItemYPositionAnimation.animateTo(
                                                    targetValue = 1f,
                                                    animationSpec = tween(
                                                        durationMillis = 400,
                                                        easing = LinearOutSlowInEasing
                                                    )
                                                )
                                            }
                                            launch {
                                                menuItemXPositionAnimation.snapTo(0f)
                                                menuItemXPositionAnimation.animateTo(
                                                    targetValue = 1f,
                                                    animationSpec = tween(
                                                        durationMillis = 700,
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
        ) {
            drawMenuBackground(
                indentationWidth = indentationWidth,
                selectedItemIndentationXTranslation = selectedItemIndentationXTranslation,
                indentationHeight = indentationHeight,
                indentationHeightFraction = indentationHeightFraction.value
            )

            menuItems.forEachIndexed { index, _ ->
                val menuItemXPosition =
                    indentationWidth / 2 + index * (screenWidth - indentationWidth / 2) / menuItemsCount

                drawItemBackgroundCircle(
                    index = index,
                    previouslySelectedItemIndex = previouslySelectedItemIndex,
                    menuItemBackgroundCircleRadius = menuItemBackgroundCircleRadius,
                    indentationHeight = indentationHeight,
                    selectedItemIndex = selectedItemIndex,
                    menuItemYPositionAnimationFraction = menuItemYPositionAnimation.value,
                    itemsCount = menuItemsCount,
                    menuItemXPosition = menuItemXPosition,
                    menuItemXPositionAnimationFraction = menuItemXPositionAnimation.value
                )

                drawMenuIcon(
                    icon = menuItemsBitmaps[index],
                    itemIndex = index,
                    previouslySelectedItemIndex = previouslySelectedItemIndex,
                    iconSize = menuIconSize,
                    indentationHeight = indentationHeight,
                    selectedItemIndex = selectedItemIndex,
                    itemAnimationFraction = menuItemYPositionAnimation.value,
                    itemXPosition = menuItemXPosition
                )
            }
        }
    }
}

private fun DrawScope.drawItemBackgroundCircle(
    index: Int,
    selectedItemIndex: Int,
    previouslySelectedItemIndex: Int,
    itemsCount: Int,
    menuItemBackgroundCircleRadius: Float,
    indentationHeight: Float,
    menuItemYPositionAnimationFraction: Float,
    menuItemXPositionAnimationFraction: Float,
    menuItemXPosition: Float,
) {
    val startTop = if (index == previouslySelectedItemIndex) {
        0f - menuItemBackgroundCircleRadius / 2
    } else {
        indentationHeight / 2
    }

    val targetTop = if (index == selectedItemIndex) {
        0f - menuItemBackgroundCircleRadius / 2
    } else {
        indentationHeight / 2
    }

    val top =
        startTop + (targetTop - startTop) * menuItemYPositionAnimationFraction

    val isOddItemCount = itemsCount % 2 != 0

    val center = menuItemXPosition - menuItemBackgroundCircleRadius / 2
    val startLeft =
        if (isOddItemCount && index == itemsCount / 2 || selectedItemIndex == previouslySelectedItemIndex) {
            center
        } else if (index == selectedItemIndex) {
            if (selectedItemIndex > previouslySelectedItemIndex) {
                if (abs(previouslySelectedItemIndex - selectedItemIndex) == itemsCount - 1) {
                    // Animate enter translation from left to right
                    menuItemXPosition - menuItemBackgroundCircleRadius
                } else {
                    // Animate enter translation from right to left
                    menuItemXPosition
                }
            } else {
                if (abs(previouslySelectedItemIndex - selectedItemIndex) == itemsCount - 1) {
                    // Animate enter translation from right to left
                    menuItemXPosition
                } else {
                    // Animate enter translation from left to right
                    menuItemXPosition - menuItemBackgroundCircleRadius
                }
            }
        } else {
            center
        }

    val targetLeft =
        if (isOddItemCount && index == itemsCount / 2 || selectedItemIndex == previouslySelectedItemIndex) {
            center
        } else if (index == previouslySelectedItemIndex) {
            if (selectedItemIndex > previouslySelectedItemIndex) {
                // Animate exit translation to left
                menuItemXPosition - menuItemBackgroundCircleRadius
            } else {
                // Animate exit translation to right
                menuItemXPosition + menuItemBackgroundCircleRadius / 2
            }
        } else {
            center
        }

    val left = startLeft + (targetLeft - startLeft) * menuItemXPositionAnimationFraction

    if (index == selectedItemIndex || index == previouslySelectedItemIndex) {
        translate(
            left = left,
            top = top
        ) {
            drawCircle(
                Color.White,
                menuItemBackgroundCircleRadius,
                Offset(
                    x = menuItemBackgroundCircleRadius / 2,
                    y = menuItemBackgroundCircleRadius / 2
                )
            )
        }
    }
}

private fun DrawScope.drawMenuBackground(
    indentationWidth: Float,
    selectedItemIndentationXTranslation: Float,
    indentationHeight: Float,
    indentationHeightFraction: Float = 0f
) {
    with(Path()) {
        moveTo(0f, 0f)

        lineTo(selectedItemIndentationXTranslation, 0f)

        val xDistanceToControlPoint = indentationWidth / 5f
        val startSegmentFirstControlPoint = Offset(xDistanceToControlPoint, 0f)
        val startSegmentSecondControlPoint = Offset(xDistanceToControlPoint, indentationHeight)

        cubicTo(
            x1 = startSegmentFirstControlPoint.x + selectedItemIndentationXTranslation,
            y1 = startSegmentFirstControlPoint.y - startSegmentFirstControlPoint.y * indentationHeightFraction,
            x2 = startSegmentSecondControlPoint.x + selectedItemIndentationXTranslation,
            y2 = startSegmentSecondControlPoint.y - startSegmentSecondControlPoint.y * indentationHeightFraction,
            x3 = indentationWidth / 2f + selectedItemIndentationXTranslation,
            y3 = indentationHeight - indentationHeight * indentationHeightFraction
        )

        val endSegmentFirstControlPoint =
            Offset(indentationWidth - xDistanceToControlPoint, indentationHeight)
        val endSegmentSecondControlPoint =
            Offset(indentationWidth - xDistanceToControlPoint, 0f)

        cubicTo(
            x1 = endSegmentFirstControlPoint.x + selectedItemIndentationXTranslation,
            y1 = endSegmentFirstControlPoint.y - endSegmentFirstControlPoint.y * indentationHeightFraction,
            x2 = endSegmentSecondControlPoint.x + selectedItemIndentationXTranslation,
            y2 = endSegmentSecondControlPoint.y - endSegmentSecondControlPoint.y * indentationHeightFraction,
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