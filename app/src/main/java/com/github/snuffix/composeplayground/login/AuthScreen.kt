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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
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
val formBackgroundColor = Color(0xFFF4F4F4)
val loginInputUnfocusedColor = Color(0xFFB9B9B9)

@Composable
fun AuthScreen() {
    val screenWidth = with(LocalContext.current) { resources.displayMetrics.widthPixels }
    val screenHeight = with(LocalContext.current) { resources.displayMetrics.heightPixels }

    val cardWidth = 300.dp
    val cardWidthInPixels = with(LocalDensity.current) { cardWidth.toPx() }
    val cardHeight = 350.dp
    val cardHeightInPixels = with(LocalDensity.current) { cardHeight.toPx() }

    val registrationButtonSize = 80.dp
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

    // Position of dummy close button used to animate the real close button
    var closeDummyButtonPosition by remember { mutableStateOf(Offset(0f, 0f)) }

    var transitionToRegisterScreen by remember { mutableStateOf(false) }
    var registrationScreenVisible by remember { mutableStateOf(false) }
    var registrationFormVisible by remember { mutableStateOf(false) }
    var loginFormVisible by remember { mutableStateOf(true) }

    // Initial register button transition
    val registerButtonTransitionAnim = remember { Animatable(0f) }

    // Close button transition started after registration button is done animating
    val closeButtonTransitionAnim = remember { Animatable(0f) }

    val formPadding = 16.dp

    // Login screen offset is used when the form is moved slightly behind
    val loginScreenOffset = lerp(0.dp, 10.dp, closeButtonTransitionAnim.value)

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
    ) {
        val loginScreenOffsetInPixels = with(LocalDensity.current) { loginScreenOffset.toPx() }

        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .width(cardWidth)
                .height(cardHeight)
                .offset {
                    IntOffset(
                        x = loginScreenOffsetInPixels.toInt(),
                        y = (-loginScreenOffsetInPixels).toInt()
                    )
                }
                .graphicsLayer {
                    alpha = (1.7 - closeButtonTransitionAnim.value)
                        .coerceAtMost(1.0)
                        .toFloat()
                },
            colors = CardDefaults.cardColors(
                containerColor = formBackgroundColor
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
                    modifier = Modifier
                        .padding(formPadding)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FormTitleLabel(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = "LOGIN",
                            textColor = registrationBackgroundColor
                        )

                        Image(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(40.dp)
                                .graphicsLayer {
                                    rotationZ = 45f
                                }
                                .onGloballyPositioned {
                                    closeDummyButtonPosition = it.positionInRoot()
                                },
                            colorFilter = ColorFilter.tint(formBackgroundColor),
                            imageVector = Icons.Filled.Add,
                            contentDescription = ""
                        )
                    }

                    Column {
                        FormInputField(
                            text = "Email",
                            unfocusedColor = loginInputUnfocusedColor,
                            focusedColor = registrationBackgroundColor
                        )
                        FormInputField(
                            text = "Password",
                            unfocusedColor = loginInputUnfocusedColor,
                            focusedColor = registrationBackgroundColor
                        )
                    }

                    FormButton(
                        text = "GO",
                        textColor = registrationBackgroundColor,
                        outlineColor = registrationBackgroundColor
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
                                (registrationButtonFinalPosition.x - ((screenWidth - cardWidthInPixels) / 2)).toInt()
                            val y =
                                registrationButtonFinalPosition.y - ((screenHeight - cardHeightInPixels) / 2) + registrationButtonSizeInPixels / 6 // magic value, not sure what causes slight offset

                            val initialPosition = IntOffset(x, y.toInt())

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
                        modifier = Modifier
                            .padding(formPadding)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            FormTitleLabel(
                                modifier = Modifier.align(Alignment.CenterVertically),
                                text = "REGISTER",
                                textColor = Color.White
                            )

                            Image(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .size(40.dp),
                                colorFilter = ColorFilter.tint(registrationBackgroundColor),
                                imageVector = Icons.Filled.Add,
                                contentDescription = ""
                            )
                        }

                        Column {
                            FormInputField(
                                text = "Email",
                            )
                            FormInputField(text = "Password")
                            FormInputField(text = "Confirm Password")
                        }

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { /*TODO*/ },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = registrationBackgroundColor
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("NEXT")
                        }
                    }
                }

            }
        }


        val addIconSize = 40.dp
        val addIconSizeInPixels = with(LocalDensity.current) { addIconSize.toPx() }

        if (!registrationScreenVisible) {
            Box(
                modifier = Modifier
                    .size(registrationButtonSize)
                    .absoluteOffset {
                        val circleCenter = Offset(
                            (min(
                                registrationButtonInitialPosition.x,
                                registrationButtonFinalPosition.x
                            ) + abs(registrationButtonInitialPosition.x - registrationButtonFinalPosition.x) / 2),
                            (min(
                                registrationButtonInitialPosition.y,
                                registrationButtonFinalPosition.y
                            ) + abs(registrationButtonInitialPosition.y - registrationButtonFinalPosition.y) / 2)
                        )

                        val radius =
                            sqrt(
                                (registrationButtonInitialPosition.x - circleCenter.x)
                                    .toDouble()
                                    .pow(2.0) + (registrationButtonInitialPosition.y - circleCenter.y)
                                    .toDouble()
                                    .pow(2.0)
                            )

                        val initialPositionAngle = calculatePointAngleOnCircle(
                            point = Offset(
                                registrationButtonInitialPosition.x,
                                registrationButtonInitialPosition.y
                            ),
                            circleCenter = circleCenter
                        )

                        val finalPositionAngle = calculatePointAngleOnCircle(
                            point = Offset(
                                registrationButtonFinalPosition.x,
                                registrationButtonFinalPosition.y
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
            ) {
                Image(
                    modifier = Modifier
                        .size(addIconSize)
                        .align(Alignment.Center),
                    colorFilter = ColorFilter.tint(Color.White),
                    imageVector = Icons.Filled.Add,
                    contentDescription = ""
                )
            }
        } else {
            Image(
                modifier = Modifier
                    .size(addIconSize)
                    .offset {
                        val x =
                            registrationButtonFinalPosition.x + registrationButtonSizeInPixels / 2 - addIconSizeInPixels / 2
                        val y =
                            registrationButtonFinalPosition.y + registrationButtonSizeInPixels / 2 - addIconSizeInPixels / 2

                        val initialPosition = IntOffset(x.toInt(), y.toInt())

                        val finalPosition = IntOffset(
                            closeDummyButtonPosition.x.toInt() - addIconSizeInPixels.toInt() / 2,
                            closeDummyButtonPosition.y.toInt() + addIconSizeInPixels.toInt() / 4
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
}

@Composable
fun FormTitleLabel(modifier: Modifier, text: String, textColor: Color) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 24.sp,
        fontFamily = FontFamily.SansSerif,
        color = textColor,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun FormInputField(
    text: String,
    inputColor: Color = Color.White,
    focusedColor: Color = inputColor,
    unfocusedColor: Color = inputColor
) {
    OutlinedTextField(
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = focusedColor,
            focusedBorderColor = focusedColor,
            focusedLabelColor = focusedColor,
            cursorColor = focusedColor,
            unfocusedLabelColor = unfocusedColor,
            unfocusedTextColor = unfocusedColor,
            unfocusedBorderColor = unfocusedColor,
            unfocusedLeadingIconColor = Color.White
        ),
        value = "",
        label = {
            Text(text)
        },
        onValueChange = {

        })
}

@Composable
fun FormButton(
    text: String,
    textColor: Color,
    outlineColor: Color,
) {
    OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /*TODO*/ },
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = textColor
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 2.dp,
            brush = Brush.linearGradient(
                listOf(
                    outlineColor,
                    outlineColor
                )
            )
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text)
    }
}