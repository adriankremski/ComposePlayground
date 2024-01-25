package com.github.snuffix.composeplayground.graphs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import java.time.LocalDate
import kotlin.math.cos
import kotlin.math.sin


data class DataSet(
    val name: String, val localSource: String,
    val remoteQuery: String, val gradients: Gradients
)

val MRNA = DataSet(
    name = "MRNA",
    localSource = "/wikipedia/mrna.json",
    remoteQuery = "Messenger_RNA",
    gradients = Gradients(
        0.0f to colorLow,
        0.1f to colorMid1,
        0.5f to colorMid2,
        1.0f to colorHigh,
    )
)
val SOURDOUGH = DataSet(
    name = "Sourdough",
    localSource = "/wikipedia/sourdough.json",
    remoteQuery = "Sourdough",
    gradients = Gradients(
        0.0f to colorLow,
        0.05f to colorMid1,
        0.2f to colorMid2,
        1.0f to colorHigh,
    )
)
val REBECCA_BLACK = DataSet(
    name = "Rebecca Black",
    localSource = "/wikipedia/rebecca_black.json",
    remoteQuery = "Rebecca_Black",
    gradients = Gradients(
        0.0f to colorLow,
        0.03f to colorMid1,
        0.08f to colorMid2,
        1.0f to colorHigh,
    )
)
val PRIME_NUMBER = DataSet(
    name = "Prime Number",
    localSource = "/wikipedia/prime_number.json",
    remoteQuery = "Prime_number",
    gradients = Gradients(
        0.0f to colorLow,
        0.03f to colorMid1,
        0.08f to colorMid2,
        1.0f to colorHigh,
    )
)

data class Stats(
     val items: List<QueryItem>
)

data class QueryItem(
     val project: String,
     val article: String,
     var granularity: String,
     val timestamp: String,
     val access: String,
     val agent: String,
     val views: Int
)

private val dailyStats = Gson().fromJson(json, Stats::class.java)
@Composable
fun SpiralGraph() {
    val dataSet = SOURDOUGH

    val mapped = dailyStats!!.items.associate { it.timestamp to it.views }
    val lowestHitCount = mapped.values.minOf { it }
    val highestHitCount = mapped.values.maxOf { it }

    Canvas(Modifier.fillMaxSize()) {
        // The visuals of the spiral are from
        // https://observablehq.com/@yurivish/seasonal-spirals
        val fillColor = Color(255, 254, 240)
        val shadowColor = Color(10, 10, 18)

        drawRect(color = fillColor)

        val gap = 4.dp.toPx()
        val centerX = size.center.x
        val centerY = size.center.y
        var radiusStart = 40.dp.toPx()
        var thicknessStart = 20.dp.toPx()
        val thicknessIncrement = 5.dp.toPx()

        var date = LocalDate.of(2018, 1, 1)
        for (year in 2018 until 2022) {
            val thicknessEnd = thicknessStart + thicknessIncrement

            val radiusInnerStart = radiusStart
            val radiusInnerEnd = radiusInnerStart + thicknessStart + gap
            val radiusOuterStart = radiusStart + thicknessStart
            val radiusOuterEnd = radiusInnerEnd + thicknessEnd


            val segments = 52
            for (segment in 0 until segments) {
                val startAngle = segment * 360.0f / segments
                val endAngle = startAngle + 360.0f / segments

                val radiusA =
                    radiusInnerStart + (radiusInnerEnd - radiusInnerStart) * startAngle / 360.0f
                val radiusB =
                    radiusOuterStart + (radiusOuterEnd - radiusOuterStart) * startAngle / 360.0f
                val radiusC =
                    radiusOuterStart + (radiusOuterEnd - radiusOuterStart) * endAngle / 360.0f
                val radiusD =
                    radiusInnerStart + (radiusInnerEnd - radiusInnerStart) * endAngle / 360.0f

                for (weekday in 0 until 7) {
                    val radiusDayA = radiusA + (radiusB - radiusA) * weekday / 7.0f
                    val radiusDayB = radiusDayA + (radiusB - radiusA) / 7.0f
                    val radiusDayD = radiusD + (radiusC - radiusD) * weekday / 7.0f
                    val radiusDayC = radiusDayD + (radiusC - radiusD) / 7.0f

                    val A = Offset(
                        x = centerX + radiusDayA * sin(startAngle.toRad()),
                        y = centerY - radiusDayA * cos(startAngle.toRad())
                    )
                    val B = Offset(
                        x = centerX + radiusDayB * sin(startAngle.toRad()),
                        y = centerY - radiusDayB * cos(startAngle.toRad())
                    )
                    val C = Offset(
                        x = centerX + radiusDayC * sin(endAngle.toRad()),
                        y = centerY - radiusDayC * cos(endAngle.toRad())
                    )
                    val D = Offset(
                        x = centerX + radiusDayD * sin(endAngle.toRad()),
                        y = centerY - radiusDayD * cos(endAngle.toRad())
                    )

                    val path = androidx.compose.ui.graphics.Path()
                    path.moveTo(A.x, A.y)
                    path.lineTo(B.x, B.y)
                    path.lineTo(C.x, C.y)
                    path.lineTo(D.x, D.y)
                    path.close()

                    // Convert the current date to timestamp that matches our source data
                    val dateKey = "${date.year}${
                        date.monthValue.toString().padStart(2, '0')
                    }${date.dayOfMonth.toString().padStart(2, '0')}00"
                    val hits = mapped[dateKey]!!.toFloat()

                    // Compute where the number of hits that corresponds to the current date
                    // falls within the min-max range of the whole data set
                    val percentage =
                        (hits - lowestHitCount) / (highestHitCount - lowestHitCount)

                    // And get the matching color for filling the chart segment that corresponds
                    // to the current date
                    val color = dataSet.gradients.getColor(percentage)

                    drawPath(
                        path = path,
                        color = color,
                        style = Fill
                    )

                    // Go to the next day. Note that 52 weeks is 364 days, so technically
                    // speaking, we're accumulating one-day off every year. But since we're
                    // only displaying data for 3-4 years in this graph, the total
                    // accumulated deviation doesn't really matter
                    date = date.plusDays(1)
                }
            }

            val yearString = year.toString()
            val radiusLetter = (radiusInnerStart + radiusOuterStart) / 2.0f - 4.dp.toPx()

            val yearPath = Path()
            yearPath.addArc(
                oval = Rect(
                    left = centerX - radiusLetter * 1.2f,
                    top = centerY - radiusLetter,
                    right = centerX + radiusLetter * 1.2f,
                    bottom = centerY + radiusLetter
                ),
                startAngleDegrees = -90.0f,
                sweepAngleDegrees = 90.0f
            )
//            this.drawTextOnPath(
//                text = yearString,
//                textSize = 12.dp,
//                path = yearPath,
//                offset = Offset(2.dp.toPx(), 0.0f),
//                textAlign = TextAlign.Left,
//                paint = Paint().also {
//                    it.color = fillColor
//                    it.style = PaintingStyle.Fill
//                },
//                shadow = Shadow(
//                    color = shadowColor,
//                    offset = Offset.Zero,
//                    blurRadius = 2.5f
//                )
//            )

            thicknessStart = thicknessEnd
            radiusStart = radiusInnerEnd
        }

        drawGradientLegend(gradients = dataSet.gradients)

//        this.drawIntoCanvas {
//            val nativeCanvas = it.nativeCanvas
//
//            val textPaint = org.jetbrains.skia.Paint()
//            textPaint.color4f = Color4f(0.2f, 0.2f, 0.2f, 1.0f)
//            val font = Font(Typeface.makeDefault(), 24.0f)
//
//            val legendTextX = colorLegendLeft + colorLegendWidth + 4.dp.toPx()
//            nativeCanvas.drawString(
//                (lowestHitCount / 1000).toString() + "K",
//                legendTextX,
//                size.height - 36.dp.toPx(),
//                font,
//                textPaint
//            )
//            nativeCanvas.drawString(
//                (dataSet.gradients.getMidPoint(
//                    lowestHitCount,
//                    highestHitCount
//                ) / 1000).toString() + "K",
//                legendTextX,
//                size.height - 36.dp.toPx() - colorLegendHeight / 2.0f,
//                font,
//                textPaint
//            )
//            nativeCanvas.drawString(
//                (highestHitCount / 1000).toString() + "K",
//                legendTextX,
//                size.height - 36.dp.toPx() - colorLegendHeight,
//                font,
//                textPaint
//            )
//        }

        // Months
        val months = listOf(
            "jan",
            "feb",
            "mar",
            "apr",
            "may",
            "jun",
            "jul",
            "aug",
            "sep",
            "oct",
            "nov",
            "dec"
        )
//
//        for ((index, month) in months.withIndex()) {
//            val monthName = month.uppercase()
//            val radius = radiusStart + thicknessStart * index / 12.0f + 6.dp.toPx()
//            val monthPath = Path()
//            monthPath.addArc(
//                oval = Rect(center = Offset(x = centerX, y = centerY), radius = radius),
//                startAngleDegrees = 30.0f * index - 90.0f,
//                sweepAngleDegrees = 30.0f
//            )
//            this.drawTextOnPath(
//                text = monthName,
//                textSize = 12.dp,
//                path = monthPath,
//                offset = Offset.Zero,
//                textAlign = TextAlign.Center,
//                paint = Paint().also {
//                    it.color = fillColor
//                    it.style = PaintingStyle.Fill
//                },
//                shadow = Shadow(
//                    color = shadowColor,
//                    offset = Offset.Zero,
//                    blurRadius = 2.5f
//                )
//            )
//        }
    }
}

fun Float.toRad() = this * Math.PI.toFloat() / 180.0f

//import org.jetbrains.skia.*