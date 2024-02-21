package com.github.snuffix.composeplayground.bottom_menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.github.snuffix.composeplayground.ui.theme.ComposePlaygroundTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val widgetBackgroundColor = Color(0xFF313131)
val buttonColor = Color(0xFF5CAE9D)
private val screenBackgroundColor = Color.LightGray.copy(alpha = 0.5f)

@Composable
fun PhotoPickerScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackgroundColor)

    ) {
        var isBottomBarVisible by remember { mutableStateOf(true) }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                isBottomBarVisible = !isBottomBarVisible
            }
        ) {
            if (isBottomBarVisible) {
                Text("Hide")
            } else {
                Text("Show")
            }
        }

        ActionBottomBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            isVisible = isBottomBarVisible,
            hideBottomBar = {
                isBottomBarVisible = false
            }
        )
    }
}

@Composable
fun ActionBottomBar(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    hideBottomBar: () -> Unit,
) {
    var buttonsVisible by remember { mutableStateOf(false) }
    val actionButtonIconScalingAnimation = remember { Animatable(0f) }
    val actionButtonBackgroundRotationAnimation = remember { Animatable(1f) }
    var isBottomBarVisible by remember { mutableStateOf(false) }

    val buttonsAnimatedVisibilityDuration = 300L

    LaunchedEffect(isVisible) {
        if (!isVisible) {
            buttonsVisible = false

            // Wait for buttons to hide
            delay(buttonsAnimatedVisibilityDuration)

            // Animate action button size to size * 1.1f
            actionButtonIconScalingAnimation.animateTo(1.1f)

            // Rotate action button background from 0 to 180 degrees
            launch {
                delay(150)
                actionButtonBackgroundRotationAnimation.animateTo(
                    1f,
                    animationSpec = tween(
                        durationMillis = 400
                    )
                )
            }

            // Scale action button icon from 1.1f to 0f size
            actionButtonIconScalingAnimation.animateTo(
                0f,
                animationSpec = tween(
                    durationMillis = 300
                )
            )

            // Wait for action button icon scaling animation to finish
            delay(250)

            isBottomBarVisible = false
        } else {
            isBottomBarVisible = true

            // Wait for bottom bar background to show
            delay(250)

            // Scale action button icon from 0f to 1f size
            launch {
                delay(150)

                actionButtonIconScalingAnimation.animateTo(
                    1f,
                    animationSpec = tween(
                        durationMillis = 300
                    )
                )
            }

            // Rotate action button background from 180 to 0 degrees
            actionButtonBackgroundRotationAnimation.animateTo(
                0f,
                animationSpec = tween(
                    durationMillis = 400
                )
            )

            // Show buttons
            buttonsVisible = true
        }
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = isBottomBarVisible,
        enter = slideInVertically(
            initialOffsetY = { it }
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
        )
    ) {
        Column {
            val buttonSize = 130.dp
            val translation = with(LocalDensity.current) { buttonSize.toPx() / 2 }

            ActionButton(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(buttonSize)
                    .zIndex(1f)
                    .graphicsLayer {
                        translationY = translation
                        scaleX = 1f - actionButtonBackgroundRotationAnimation.value
                        scaleY = 1f - actionButtonBackgroundRotationAnimation.value
                        transformOrigin = TransformOrigin(0.5f, 0.5f)
                        rotationZ = 180f * actionButtonBackgroundRotationAnimation.value
                    },
                imageModifier = Modifier
                    .size(80.dp)
                    .graphicsLayer {
                        scaleX = actionButtonIconScalingAnimation.value
                        scaleY = actionButtonIconScalingAnimation.value
                    },
                onClick = {
                    hideBottomBar()
                }
            )

            Row(
                modifier = Modifier
                    .height(100.dp)
                    .background(widgetBackgroundColor)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                AnimatablePhotoPickerButton(
                    icon = Icons.Default.Close,
                    buttonsVisible = buttonsVisible,
                    buttonsVisibilityAnimDuration = buttonsAnimatedVisibilityDuration.toInt(),
                    direction = ButtonSlideDirection.LEFT,
                )

                AnimatablePhotoPickerButton(
                    icon = Icons.Default.Check,
                    buttonsVisible = buttonsVisible,
                    buttonsVisibilityAnimDuration = buttonsAnimatedVisibilityDuration.toInt(),
                    direction = ButtonSlideDirection.RIGHT,
                )
            }
        }
    }
}

@Preview
@Composable
fun PhotoPickerScreenPreview() {
    ComposePlaygroundTheme {
        PhotoPickerScreen()
    }
}