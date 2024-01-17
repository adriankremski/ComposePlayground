package com.github.snuffix.composeplayground

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

@Composable
fun LazyListFooterExample() {
    Column {
        var itemsCount by remember { mutableIntStateOf(1) }
        var items by remember { mutableStateOf(0..itemsCount) }

        Slider(
            modifier = Modifier.padding(top = 16.dp),
            value = itemsCount.toFloat(),
            onValueChange = {
                items = 0..it.toInt()
                itemsCount = it.toInt()
            },
            valueRange = 1.0f..20f
        )

        ItemsList(items = items)
    }
}

@Composable
fun ItemsList(items: IntRange) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = TopWithFooter
    ) {
        items(
            items.count(),
            contentType = {
                when (it) {
                    0 -> {
                        "HEADER"
                    }
                    items.last -> {
                        "FOOTER"
                    }
                    else -> {
                        "ITEM #${it}"
                    }
                }
            },
            key = { it }) {

            Card(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (it) {
                        items.first -> {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = "Header"
                            )
                        }

                        items.last -> {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = "Footer"
                            )
                        }

                        else -> {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = "Item #${it}"
                            )
                        }
                    }
                }
            }
        }
    }
}

object TopWithFooter : Arrangement.Vertical {
    override fun Density.arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray) {
        var childrenHeight = 0
        sizes.forEachIndexed { index, size ->
            outPositions[index] = childrenHeight
            childrenHeight += size
        }

        if (childrenHeight < totalSize) {
            outPositions[0] = 0

            val headerSize = sizes.first()
            val halfOfRemainingSpace = (totalSize - childrenHeight) / 2

            var innerChildrenHeight = 0
            sizes.mapIndexed { index, size -> index to size }
                .filterNot { it.first == 0 || it.first == outPositions.lastIndex }
                .forEach {
                    val (index, size) = it
                    outPositions[index] = headerSize + innerChildrenHeight + halfOfRemainingSpace
                    innerChildrenHeight += size
                }

            outPositions[outPositions.lastIndex] = totalSize - sizes.last()
        }
    }
}