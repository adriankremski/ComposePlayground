package com.github.snuffix.composeplayground.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.github.snuffix.composeplayground.calculatePointAngleOnCircle
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

val registrationBackgroundColor = Color(0xFFD13B54)

@Composable
fun AuthScreen() {
    val screenWidth = with(LocalContext.current) { resources.displayMetrics.widthPixels }
    val screenHeight = with(LocalContext.current) { resources.displayMetrics.heightPixels }
    val cardWidth = 300.dp
    val cardWidthInPixels = with(LocalDensity.current) { cardWidth.toPx() }
    val cardHeight = 400.dp
    val cardHeightInPixels = with(LocalDensity.current) { cardHeight.toPx() }
    val registrationButtonSize = 100.dp
    val registrationButtonSizeInPixels =
        with(LocalDensity.current) { registrationButtonSize.toPx() }

    val registrationButtonInitialPosition = remember {
        Offset(
            x = ((screenWidth / 2) - registrationButtonSizeInPixels / 2).toInt() + cardWidthInPixels / 2,
            y = ((screenHeight / 2) - registrationButtonSizeInPixels / 2).toInt() - cardHeightInPixels / 4,
        )
    }

    val registrationButtonFinalPosition = remember {
        Offset(
            x = registrationButtonInitialPosition.x - registrationButtonSizeInPixels,
            y = registrationButtonInitialPosition.y + registrationButtonSizeInPixels / 2,
        )
    }

    var registerButtonPosition by remember { mutableStateOf(Offset(0f, 0f)) }

    var transitionToRegisterScreen by remember { mutableStateOf(false) }
    var registrationScreenVisible by remember { mutableStateOf(false) }
    var registrationFormVisible by remember { mutableStateOf(false) }
    var loginFormVisible by remember { mutableStateOf(true) }

    val registerButtonTransitionAnim = remember { Animatable(0f) }
    val closeButtonTransitionAnim = remember { Animatable(0f) }

    LaunchedEffect(key1 = transitionToRegisterScreen) {
        if (transitionToRegisterScreen) {
            loginFormVisible = false
            registerButtonTransitionAnim.animateTo(1f, tween(400, easing = LinearOutSlowInEasing))
            registrationScreenVisible = true
            closeButtonTransitionAnim.animateTo(1f, tween(300))
            registrationFormVisible = true
        } else {
            registrationFormVisible = false
            closeButtonTransitionAnim.animateTo(0f, tween(300))
            loginFormVisible = true
            registrationScreenVisible = false
            registerButtonTransitionAnim.animateTo(0f, tween(500))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray.copy(alpha = 0.5f))
    ) {
        val formPadding = 16.dp
        val formPaddingInPixels = with(LocalDensity.current) { formPadding.toPx() }

        val formTitleSize = 24.sp

        val offset = lerp(0.dp, 10.dp, closeButtonTransitionAnim.value)
        val offsetInPixels = with(LocalDensity.current) { offset.toPx() }

        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .width(cardWidth)
                .height(cardHeight)
                .offset {
                    IntOffset(
                        x = offsetInPixels.toInt(),
                        y = (-offsetInPixels).toInt()
                    )
                }
                .graphicsLayer {
                    alpha = (1.7 - closeButtonTransitionAnim.value).coerceAtMost(1.0).toFloat()
                },
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            AnimatedVisibility(
                visible = loginFormVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(formPadding)
                ) {
                    Text(
                        text = "LOGIN",
                        fontSize = formTitleSize,
                        fontFamily = FontFamily.SansSerif,
                        color = registrationBackgroundColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

        }

        if (registrationScreenVisible) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(cardWidth)
                    .height(cardHeight)
            ) {
                val registrationIconSize = 40.dp
                val registrationIconSizeInPixels =
                    with(LocalDensity.current) { registrationIconSize.toPx() }

                val backgroundSizeWidth =
                    lerp(registrationButtonSize, cardWidth, closeButtonTransitionAnim.value)
                val backgroundSizeHeight =
                    lerp(registrationButtonSize, cardHeight, closeButtonTransitionAnim.value)

                val cornerRadius = lerp(50.dp, 8.dp, closeButtonTransitionAnim.value)

                Card(
                    modifier = Modifier
                        .width(backgroundSizeWidth)
                        .height(backgroundSizeHeight)
                        .offset {
                            val x =
                                (registerButtonPosition.x - ((screenWidth - cardWidthInPixels) / 2)).toInt() - registrationButtonSizeInPixels / 2
                            val y =
                                registerButtonPosition.y - ((screenHeight - cardHeightInPixels) / 2) + registrationButtonSizeInPixels / 2

                            val initialPosition = IntOffset(x.toInt(), y.toInt() - 40)

                            val finalPosition = IntOffset(0, 0)
                            lerp(initialPosition, finalPosition, closeButtonTransitionAnim.value)
                        },
                    shape = RoundedCornerShape(cornerRadius),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = registrationBackgroundColor
                    )
                )
                {

                }

                AnimatedVisibility(
                    enter = fadeIn(),
                    exit = fadeOut(),
                    visible = registrationFormVisible
                ) {
                    Column(
                        modifier = Modifier.padding(formPadding)
                    ) {
                        Text(
                            text = "REGISTER",
                            fontSize = formTitleSize,
                            fontFamily = FontFamily.SansSerif,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .offset {
                            // TODO, Better way to calculate this
                            val x =
                                (registerButtonPosition.x - registrationIconSizeInPixels / 2 - ((screenWidth - cardWidthInPixels) / 2)).toInt()
                            val y =
                                registerButtonPosition.y - registrationIconSizeInPixels / 2 - ((screenHeight - cardHeightInPixels) / 2) + registrationButtonSizeInPixels / 2 + registrationIconSizeInPixels - 20

                            val initialPosition = IntOffset(x, y.toInt())

                            val finalPosition = IntOffset(
                                (cardWidthInPixels - registrationIconSizeInPixels.toInt() - formPaddingInPixels).toInt(),
                                (formPaddingInPixels).toInt()
                            )

                            lerp(initialPosition, finalPosition, closeButtonTransitionAnim.value)
                        }
                        .graphicsLayer {
                            rotationZ = 45f
                        }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            transitionToRegisterScreen = false
                        },
                    colorFilter = ColorFilter.tint(Color.White),
                    imageVector = Icons.Filled.Add,
                    contentDescription = ""
                )
            }
        }


        if (!registrationScreenVisible) {
            Box(
                modifier = Modifier
                    .size(registrationButtonSize)
                    .absoluteOffset {
                        val initialPosition = registrationButtonInitialPosition
                        val finalPosition = registrationButtonFinalPosition

                        val circleCenter = Offset(
                            (min(
                                initialPosition.x,
                                finalPosition.x
                            ) + abs(initialPosition.x - finalPosition.x) / 2),
                            (min(
                                initialPosition.y,
                                finalPosition.y
                            ) + abs(initialPosition.y - finalPosition.y) / 2)
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
                                initialPosition.x,
                                initialPosition.y
                            ),
                            circleCenter = circleCenter
                        )

                        val finalPositionAngle = calculatePointAngleOnCircle(
                            point = Offset(
                                finalPosition.x,
                                finalPosition.y
                            ),
                            circleCenter = circleCenter
                        )

                        val sweepAngle = initialPositionAngle - finalPositionAngle
                        val viewAngle =
                            initialPositionAngle + registerButtonTransitionAnim.value * sweepAngle

                        val circleX = circleCenter.x + radius * sin(Math.toRadians(90 - viewAngle))
                        val circleY = circleCenter.y + radius * cos(Math.toRadians(90 - viewAngle))

                        IntOffset(circleX.toInt(), circleY.toInt())
                    }
                    .graphicsLayer {
                        rotationZ = 45f * registerButtonTransitionAnim.value
                    }
                    .background(registrationBackgroundColor, RoundedCornerShape(50.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        transitionToRegisterScreen = true
                    }
                    .onGloballyPositioned {
                        registerButtonPosition = it.positionInParent()
                    }
            ) {
                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.Center),
                    colorFilter = ColorFilter.tint(Color.White),
                    imageVector = Icons.Filled.Add,
                    contentDescription = ""
                )
            }
        }
    }
}