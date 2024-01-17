package com.github.snuffix.composeplayground

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.random.Random

val progressBackgroundColor = Color(0xFFBDC1C6)
val progressColor = Color(0xFFFF5A5A)

@Composable
fun StepsList(
    cards: IntRange
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 500.dp, end = 16.dp)
    ) {
        Row {
            var previouslySelectedCard by remember { mutableIntStateOf(0) }
            var selectedCard by remember { mutableIntStateOf(0) }
            val cardPositions by remember { mutableStateOf(Array(cards.count()) { Offset.Zero }) }
            val cardHeights by remember { mutableStateOf(Array(cards.count()) { 0 }) }

            val cardInitialHeightInPixels = with(LocalDensity.current) { 100.dp.toPx().toInt() }
            val cardVerticalSpacing = 16.dp
            val cardVerticalSpacingInPixels =
                with(LocalDensity.current) { cardVerticalSpacing.toPx().toInt() }

            val animation = remember {
                Animatable(0f)
            }

            LaunchedEffect(key1 = selectedCard) {
                animation.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(500)
                )
            }

            LaunchedEffect(key1 = selectedCard) {
                scrollState.animateScrollTo(
                    // DONE
                    value = if (previouslySelectedCard < selectedCard) {
                        cardPositions[selectedCard].y.toInt() - cardHeights[previouslySelectedCard] + cardInitialHeightInPixels - cardVerticalSpacingInPixels
                    } else {
                        cardPositions[selectedCard].y.toInt() - cardVerticalSpacingInPixels
                    },
                    animationSpec = tween(durationMillis = 500)
                )
            }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.1f)
            ) {
                val centerX = this.size.width / 2
                val circleRadius = 20f
                val strokeWidth = 4f

                fun calculateCardCenterOffset(cardIndex: Int): Offset {
                    val cardPosition = cardPositions[cardIndex]

                    val selectedCardHeight = cardHeights[cardIndex]
                    return Offset(
                        centerX,
                        cardPosition.y + selectedCardHeight / 2
                    )
                }

                this.drawLine(
                    color = progressBackgroundColor,
                    start = Offset(centerX, 0f),
                    end = calculateCardCenterOffset(cardPositions.lastIndex),
                    strokeWidth = strokeWidth
                )

                cardPositions.forEachIndexed { index, it ->
                    this.drawCircle(
                        progressBackgroundColor,
                        radius = circleRadius,
                        center = calculateCardCenterOffset(index)
                    )
                }


                val selectedCardPosition = cardPositions[selectedCard]
                val nextCardPosition = cardPositions[(selectedCard + 1).coerceAtMost(
                    cardPositions.size - 1
                )]

                clipRect(
                    bottom = circleRadius * 2 + selectedCardPosition.y + ((nextCardPosition.y - selectedCardPosition.y) / 2)
                            * (animation.value.takeIf { it in 0.0..1.0 } ?: 0f)
                ) {
                    cardPositions.forEachIndexed { index, _ ->
                        this.drawCircle(
                            if (index <= selectedCard) progressColor else progressBackgroundColor,
                            radius = circleRadius,
                            center = calculateCardCenterOffset(index)
                        )
                    }

                    this.drawLine(
                        color = progressColor,
                        start = Offset(centerX, 0f),
                        end = calculateCardCenterOffset(selectedCard),
                        strokeWidth = strokeWidth
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.9f)
            ) {
                for (card in cards) {
                    StepCard(
                        card = card,
                        selectedCard = selectedCard,
                        onCardSelected = { selectedCard = it },
                        onPreviouslySelectedCardChanged = { previouslySelectedCard = it },
                        onStartCardUpdateAnimation = {
                            scope.launch {
                                animation.snapTo(0f)
                            }
                        },
                        onCardHeightChanged = { height ->
                            cardHeights[card] = height
                        },
                        onCardPositionChanged = { position ->
                            cardPositions[card] = position
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

typealias Card = Int

@Composable
fun StepCard(
    card: Card,
    selectedCard: Card,
    onPreviouslySelectedCardChanged: (Card) -> Unit,
    onCardSelected: (Card) -> Unit,
    onStartCardUpdateAnimation: () -> Unit,
    onCardHeightChanged: (Int) -> Unit,
    onCardPositionChanged: (Offset) -> Unit,
) {
    val cardTextCount by remember { mutableIntStateOf(Random.nextInt(1, 4)) }
    var cardContentVisible by remember { mutableStateOf(card == selectedCard) }

    fun updateSelectedCard(contentVisible: Boolean, animate: Boolean) {
        cardContentVisible = contentVisible
        onPreviouslySelectedCardChanged(selectedCard)
        onCardSelected(card)

        if (animate) {
            onStartCardUpdateAnimation()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .defaultMinSize(minHeight = 100.dp)
            .clickable {
                if (card < selectedCard) {
                    updateSelectedCard(contentVisible = true, animate = false)
                } else if (selectedCard != card) {
                    updateSelectedCard(contentVisible = true, animate = true)
                } else {
                    updateSelectedCard(contentVisible = !cardContentVisible, animate = false)
                }
            }
            .onPlaced {
                onCardHeightChanged(it.size.height)
                onCardPositionChanged(it.positionInParent())
            }
    ) {
        CardContent(
            card = card,
            selectedCard = selectedCard,
            cardTextCount = cardTextCount,
            cardContentVisible
        )
    }
}

@Composable
fun ColumnScope.CardContent(
    card: Int,
    selectedCard: Int,
    cardTextCount: Int,
    cardContentVisible: Boolean
) {
    Text(
        modifier = Modifier
            .height(100.dp)
            .align(Alignment.CenterHorizontally),
        text = "Header"
    )
    AnimatedVisibility(
        visible = selectedCard == card && cardContentVisible
    ) {
        Column {
            repeat(cardTextCount) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                )
            }
        }
    }
}