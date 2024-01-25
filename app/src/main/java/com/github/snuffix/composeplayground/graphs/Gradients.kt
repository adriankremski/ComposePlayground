package com.github.snuffix.composeplayground.graphs

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

class Gradients(vararg val colorStops: Pair<Float, Color>) {
    fun getColor(fraction: Float): Color {
        for (index in 0 until colorStops.size - 1) {
            if ((fraction >= colorStops[index].first) && (fraction <= colorStops[index + 1].first)) {
                val start = colorStops[index].second
                val end = colorStops[index + 1].second
                return lerp(
                    start, end, (fraction - colorStops[index].first) /
                            (colorStops[index + 1].first - colorStops[index].first)
                )
            }
        }
        throw IllegalArgumentException("Out of bounds")
    }

    fun getMidPoint(start: Int, end: Int): Int {
        val stops = colorStops.size
        return if (stops % 2 == 1) {
            val fraction = colorStops[stops / 2].first
            (start + (end - start) * fraction).toInt()
        } else {
            val fractionStart = colorStops[stops / 2 - 1].first
            val fractionEnd = colorStops[stops / 2].first
            (start + (end - start) * (fractionEnd + fractionStart) / 2.0f).toInt()
        }
    }
}
