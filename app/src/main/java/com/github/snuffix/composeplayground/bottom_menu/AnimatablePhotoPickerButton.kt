package com.github.snuffix.composeplayground.bottom_menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

enum class ButtonSlideDirection {
    LEFT, RIGHT
}
@Composable
fun RowScope.AnimatablePhotoPickerButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    buttonsVisible: Boolean,
    buttonsVisibilityAnimDuration: Int,
    direction: ButtonSlideDirection,
) {
    AnimatedVisibility(
        modifier = modifier.align(Alignment.CenterVertically),
        visible = buttonsVisible,
        exit = slideOutHorizontally(
            targetOffsetX = {
                when (direction) {
                    ButtonSlideDirection.LEFT -> -2 * it
                    ButtonSlideDirection.RIGHT -> 2 * it
                }
            },
            animationSpec = tween(
                durationMillis = buttonsVisibilityAnimDuration,
                easing = FastOutLinearInEasing,
            )
        ),
        enter = slideInHorizontally(
            animationSpec = tween(
                durationMillis = buttonsVisibilityAnimDuration,
                easing = FastOutLinearInEasing,
            ),
            initialOffsetX = {
                when (direction) {
                    ButtonSlideDirection.LEFT -> -2 * it
                    ButtonSlideDirection.RIGHT -> 2 * it
                }
            }
        )
    ) {
        PhotoPickerButton(
            onClick = { /*TODO*/ },
            icon = icon
        )
    }
}

@Composable
fun PhotoPickerButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector,
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
        ),
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Image(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(30.dp),
            painter = rememberVectorPainter(image = icon),
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = null,
        )
    }
}
