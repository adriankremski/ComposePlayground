package com.github.snuffix.composeplayground

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.TextField
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt


@Composable
fun GraphicLayerExample() {
    var flipped by remember { mutableStateOf(false) }
    val rotationZ by animateFloatAsState(targetValue = if (flipped) 180f else 0f, label = "")

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier
                .graphicsLayer {
                    this.rotationX = rotationZ
                    cameraDistance = 12f * density
                    shadowElevation = if (flipped) 0f else 30f
                    alpha = if (flipped) 0.3f else 0.8f
                }
                .clickable { flipped = !flipped }
                .width(350.dp)
                .height(200.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.DarkGray,
            )
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Hey bro", color = Color.White, fontSize = 32.sp)
            }
        }
    }
}

@Composable
fun BarChartExample(dataPoints: List<Float>) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier
            .size(300.dp)
            .drawWithCache {
                onDrawWithContent {
                    drawContent()

                    // Draw axes
                    drawLine(
                        start = Offset(50f, size.height),
                        end = Offset(size.width, size.height),
                        color = Color.Green,
                        strokeWidth = 10f
                    )
                    drawLine(
                        start = Offset(50f, 50f),
                        end = Offset(50f, size.height),
                        color = Color.Red,
                        strokeWidth = 10f
                    )

                    // Draw bars for each data point
                    val barWidth = size.width / (dataPoints.size * 2)
                    dataPoints.forEachIndexed { index, value ->
                        val left = barWidth * (index * 2 + 1)
                        val top = size.height - (value / dataPoints.max() * size.height)
                        val right = left + barWidth
                        val bottom = size.height
                        drawRect(
                            Color.Blue,
                            topLeft = Offset(left, top),
                            size = Size(right - left, bottom - top)
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun InteractiveGridDemo() {
    val cellSize = 90.dp
    val numRows = 3
    val numColumns = 3
    val gridState = remember { mutableStateOf(Array(numRows * numColumns) { Offset.Zero }) }
    val selectedCell = remember { mutableStateOf(-1) }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (row in 0 until numRows) {
                Row {
                    for (column in 0 until numColumns) {
                        val cellIndex = row * numColumns + column
                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .onPlaced { layoutCoordinates ->
                                    gridState.value[cellIndex] = layoutCoordinates.positionInRoot()
                                }
                                .clickable { selectedCell.value = cellIndex }
                                .border(8.dp, Color.Black)
                        )
                    }
                }
            }
        }


        if (selectedCell.value >= 0) {
            val position = gridState.value[selectedCell.value]
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            position.x.roundToInt() - 35.dp
                                .toPx()
                                .toInt(),
                            position.y.roundToInt() - 80.dp
                                .toPx()
                                .toInt()
                        )
                    }
                    .size(width = 150.dp, height = 60.dp)
                    .background(Color.DarkGray.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cell Clicked: ${selectedCell.value}",
                    color = Color.Red,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun BaselinePaddingExample() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Text with baseline padding",
            modifier = Modifier
                .fillMaxWidth()
                .paddingFromBaseline(top = 32.dp),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Divider(color = Color.Gray, thickness = 1.dp)
        Text(
            "Another Text aligned to baseline",
            modifier = Modifier
                .fillMaxWidth()
                .paddingFromBaseline(top = 32.dp),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun InteractiveButtonExample() {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { },
            interactionSource = interactionSource,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPressed) Color.Gray else Color.Blue
            )
        ) {
            Text("Press Me")
        }
    }
}


@Composable
fun WeightedRowExample() {
    Column(modifier = Modifier.fillMaxHeight()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .background(Color.Green)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Weight 1F", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold)
        }
        Box(
            modifier = Modifier
                .weight(2f)
                .fillMaxSize()
                .background(Color.Cyan)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Weight 2F", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusRequesterExample() {
    val focusRequester = FocusRequester()
    val focusRequester2 = FocusRequester()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TextField(
            value = "Field 1",
            onValueChange = {},
            modifier = Modifier
                .focusRequester(focusRequester)
        )
        TextField(
            value = "Field 2",
            onValueChange = {},
            modifier = Modifier
                .focusRequester(focusRequester2)
        )
        Button(onClick = { focusRequester.requestFocus() }) {
            Text("Focus First Field")
        }
        Button(onClick = { focusRequester2.requestFocus() }) {
            Text("Focus Second Field")
        }
    }
}

@Composable
fun NestedScrollWithCollapsibleHeader() {
    // header height
    val headerHeight = remember { mutableStateOf(150.dp) }
    // adjust the header size based on scroll
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newHeight = headerHeight.value + delta.dp
                headerHeight.value = newHeight.coerceIn(50.dp, 150.dp)
                return Offset.Zero // Consuming no scroll
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(nestedScrollConnection)) {
        Column {
            Box(
                modifier = Modifier
                    .height(headerHeight.value)
                    .fillMaxWidth()
                    .background(Color.LightGray)
            ) {
                Text("Collapsible Header", Modifier.align(Alignment.Center))
            }
            LazyColumn {
                items(100) { index ->
                    Text("Item $index", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}