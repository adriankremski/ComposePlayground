package com.github.snuffix.composeplayground.calendar

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class CalendarTab {
    Month, Year, Day
}

val tabsBackgroundColor = Color(0xFFd4d4d4)

@Composable
fun CalendarTabs(
    modifier: Modifier = Modifier,
    selectedTab: CalendarTab,
    onTabSelected: (CalendarTab) -> Unit,
    tabWidth: Dp = 100.dp
) {
    val indicatorOffset by animateDpAsState(
        targetValue = when (selectedTab) {
            CalendarTab.Month -> 0.dp
            CalendarTab.Year -> tabWidth
            CalendarTab.Day -> tabWidth * 2
        },
        animationSpec = tween(easing = LinearEasing),
        label = "indicatorAnim",
    )

    Box(
        modifier = modifier
            .wrapContentWidth()
            .height(intrinsicSize = IntrinsicSize.Min)
            .background(tabsBackgroundColor, shape = RoundedCornerShape(50))
            .padding(2.dp)
    ) {
        CalendarTabIndicator(
            indicatorWidth = tabWidth,
            indicatorOffset = indicatorOffset,
            indicatorColor = MaterialTheme.colorScheme.background
        )
        Row {
            CalendarTab.entries.forEachIndexed { index, tab ->
                val isSelected = tab == selectedTab

                CalendarTabItem(
                    isSelected = isSelected,
                    onClick = {
                        onTabSelected(CalendarTab.entries[index])
                    },
                    tabWidth = tabWidth,
                    text = tab.name,
                )
            }
        }
    }
}

@Composable
fun CalendarTabItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    onClick: () -> Unit,
    tabWidth: Dp,
    text: String,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Text(
        text = text,
        modifier = modifier
            .width(tabWidth)
            .padding(4.dp)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
    )
}

@Composable
internal fun CalendarTabIndicator(
    indicatorWidth: Dp,
    indicatorOffset: Dp,
    indicatorColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(
                width = indicatorWidth,
            )
            .offset(
                x = indicatorOffset,
            )
            .clip(
                shape = CircleShape,
            )
            .background(
                color = indicatorColor,
            ),
    )
}


@Preview
@Composable
fun CalendarTabPreview() {
    CalendarTabs(selectedTab = CalendarTab.Month, onTabSelected = {})
}