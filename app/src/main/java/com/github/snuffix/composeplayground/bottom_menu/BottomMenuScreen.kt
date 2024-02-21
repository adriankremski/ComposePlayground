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
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
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
fun BottomMenuScreen(menuItems: IntRange = 0..4) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackgroundColor)

    ) {
        val menuItemsCount = menuItems.count()
        var selectedMenuItem by remember { mutableIntStateOf(0) }
        var previouslySelectedMenuItem by remember { mutableIntStateOf(0) }

        var selectedMenuItemColorAnimation =
            animateColorAsState(targetValue = selectedMenuItemColor)

        var menuItemAnimation = remember { Animatable(1f) }
        val scope = rememberCoroutineScope()

        Slider(
            modifier = Modifier.padding(16.dp),
            value = selectedMenuItem.toFloat(),
            onValueChange = {
                scope.launch {
                    if (selectedMenuItem != it.toInt()) {
                        previouslySelectedMenuItem = selectedMenuItem
                        selectedMenuItem = it.toInt()
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
            },
            valueRange = 0.0f..menuItemsCount.toFloat() - 1,
            colors = SliderDefaults.colors(
                thumbColor = stepFinishedColor,
                activeTrackColor = stepFinishedColor,
            )
        )

        val menuItems = remember {
            listOf(
                MenuItem(Icons.Default.Home),
                MenuItem(Icons.Default.List),
                MenuItem(Icons.Default.Call),
                MenuItem(Icons.Default.Email),
                MenuItem(Icons.Default.Person)
            )
        }

        val screenWidth =
            with(LocalContext.current) { resources.displayMetrics.widthPixels.toFloat() }

        val indentationWidth = screenWidth / (menuItemsCount - 2)

        val menuItemPosition =
            indentationWidth / 2 + selectedMenuItem * (screenWidth - indentationWidth / 2) / menuItemsCount

        val xTranslationAnim = animateFloatAsState(
            targetValue = menuItemPosition - indentationWidth / 2,
            label = "",
            animationSpec = tween(
                durationMillis = (600L - 100L * abs((selectedMenuItem - previouslySelectedMenuItem))).toInt(),
                easing = LinearOutSlowInEasing
            )
        )
        val xTranslation = xTranslationAnim.value

        val menuItemsBitmaps = menuItems.map { menuItem ->
            rememberVectorPainter(image = menuItem.imageBitmap)
        }

        val menuItemSize = 60f
        val menuItemBackgroundCircleRadius = menuItemSize * 1.2f
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
                                        if (selectedMenuItem != index) {
                                            previouslySelectedMenuItem = selectedMenuItem
                                            selectedMenuItem = index
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
            val path = Path()
            path.moveTo(0f, 0f)

            val startX = 0f
            val width = indentationWidth
            val bottomWidth = 0f

            path.lineTo(startX + xTranslation, 0f)

            val controlPointDistance = width / 5f
            val firstControlPoint = Offset(startX + controlPointDistance, 0f)
            val secondControlPoint = Offset(startX + controlPointDistance, indentationHeight)

            path.cubicTo(
                firstControlPoint.x + xTranslation,
                firstControlPoint.y,
                secondControlPoint.x + xTranslation,
                secondControlPoint.y,
                startX + width / 2f + bottomWidth / 2f + xTranslation,
                indentationHeight
            )

            val firstControlPoint2 =
                Offset(startX + width + bottomWidth - controlPointDistance, indentationHeight)
            val secondControlPoint2 =
                Offset(startX + width + bottomWidth - controlPointDistance, 0f)

            path.lineTo(startX + width / 2 + bottomWidth + xTranslation, indentationHeight)
            path.cubicTo(
                firstControlPoint2.x + xTranslation,
                firstControlPoint2.y,
                secondControlPoint2.x + xTranslation,
                secondControlPoint2.y,
                startX + width + bottomWidth + xTranslation,
                0f
            )

            path.lineTo(size.width, 0f)
            path.lineTo(size.width, size.height)
            path.lineTo(0f, size.height)
            path.close()
            drawPath(path, Color.White)

            menuItems.forEachIndexed { index, item ->
                val menuItemPosition =
                    indentationWidth / 2 + index * (screenWidth - indentationWidth / 2) / menuItemsCount

                with(menuItemsBitmaps[index]) {
                    val circleStartTop = if (index == previouslySelectedMenuItem) {
                        0f - menuItemBackgroundCircleRadius / 2
                    } else {
                        indentationHeight / 2
                    }

                    val circleTargetTop = if (index == selectedMenuItem) {
                        0f - menuItemBackgroundCircleRadius / 2
                    } else {
                        indentationHeight / 2
                    }

                    val circleTop =
                        circleStartTop + (circleTargetTop - circleStartTop) * menuItemAnimation.value

                    translate(
                        left = menuItemPosition - menuItemBackgroundCircleRadius / 2,
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

                    val startTop = if (index == previouslySelectedMenuItem) {
                        0f - menuItemSize / 2
                    } else {
                        indentationHeight / 2 - menuItemSize / 2
                    }

                    val targetTop = if (index == selectedMenuItem) {
                        0f - menuItemSize / 2
                    } else {
                        indentationHeight / 2 - menuItemSize / 2
                    }

                    val top = startTop + (targetTop - startTop) * menuItemAnimation.value

                    translate(
                        left = menuItemPosition - menuItemSize / 2,
                        top = top
                    ) {
                        draw(
                            size = Size(menuItemSize, menuItemSize),
                            colorFilter = ColorFilter.tint(
                                when (index) {
                                    selectedMenuItem -> {
                                        lerp(
                                            unselectedMenuItemColor,
                                            selectedMenuItemColor,
                                            menuItemAnimation.value
                                        )
                                    }

                                    previouslySelectedMenuItem -> {
                                        lerp(
                                            selectedMenuItemColor,
                                            unselectedMenuItemColor,
                                            menuItemAnimation.value
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
        }
    }
}