package com.github.snuffix.composeplayground.notebook

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutDebugFlags
import androidx.constraintlayout.compose.MotionScene
import com.github.snuffix.composeplayground.R
import java.util.EnumSet


val greenColor = Color(0xFF65db78)
val lightGrey = Color(0xFFb3b3b3).copy(alpha = 0.7f)
val darkGrey = Color(0xFF8f8f8f)

@OptIn(ExperimentalMotionApi::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun NotebookScreen2() {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxSize()
    ) {
        var isWeightSelected by remember { mutableStateOf(false) }
        var isBackgroundVisible by remember { mutableStateOf(true) }
        var progress = animateFloatAsState(
            targetValue = if (isWeightSelected) 1f else 0f,
            animationSpec = spring(
                stiffness = Spring.StiffnessLow,
                dampingRatio = Spring.DampingRatioLowBouncy
            ),
            finishedListener = {
                if (isWeightSelected) {
                    isBackgroundVisible = false
                }
            }
        )

        var backgroundShape = animateFloatAsState(
            targetValue = if (isBackgroundVisible) 1f else 0f,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            finishedListener = {
                if (isBackgroundVisible) {

                }
            }
        )

        val context = LocalContext.current
        val motionSceneContent = remember {
            context.resources
                .openRawResource(R.raw.notebook_scene)
                .readBytes()
                .decodeToString()
        }


        val scrollState = rememberLazyListState()

        val firstVisibleItemIndex = scrollState.firstVisibleItemIndex
        val visibleItemsCount = scrollState.layoutInfo.visibleItemsInfo.size
        Log.i("AdrianTest", "first: $firstVisibleItemIndex, count: $visibleItemsCount")

        val percent = ((firstVisibleItemIndex + visibleItemsCount / 2f) / 1500f) * 150f
        Log.i("AdrianTest", "visible position $percent")

        MotionLayout(
            motionScene = MotionScene(motionSceneContent),
            progress = progress.value,
            modifier = Modifier
                .fillMaxSize(),
//            debug = EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL)
        ) {
            Canvas(
                modifier = Modifier
                    .size(100.dp)
                    .layoutId("weight_background")
                    .clickable {
                        isBackgroundVisible = true
                        isWeightSelected = !isWeightSelected
                    }
            ) {
                drawArc(
                    color = greenColor,
                    startAngle = 180f,
                    sweepAngle = 180f * backgroundShape.value,
                    useCenter = true,
                    size = size,
                    topLeft = Offset(0f, 0f)
                )
                drawArc(
                    color = greenColor,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = true,
                    size = size,
                    topLeft = Offset(0f, 0f)
                )
            }

            LazyRow(
                state = scrollState,
                modifier = Modifier
                    .layoutId("weight_scroll")
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = if (backgroundShape.value == 0f) {
                            1f
                        } else {
                            0f
                        }
                    },
                verticalAlignment = Alignment.Bottom
            ) {
                items(1500) {
                    val width = 8.dp
                    if (it % 10 == 0) {
                        Box(
                            modifier = Modifier
                                .padding(end = width)
                                .width(width)
                                .height(50.dp)
                                .background(
                                    darkGrey,
                                    shape = RoundedCornerShape(
                                        topStart = width / 2,
                                        topEnd = width / 2
                                    )
                                )
                                .animateItemPlacement()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .padding(end = width)
                                .width(width)
                                .height(40.dp)
                                .background(
                                    lightGrey,
                                    shape = RoundedCornerShape(
                                        topStart = width / 2,
                                        topEnd = width / 2
                                    )
                                )
                                .animateItemPlacement()
                        )
                    }
                }
            }

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, lightGrey),
                onClick = {

                },
                modifier = Modifier
                    .width(200.dp)
                    .layoutId("selected_weight")
                    .graphicsLayer {
                        alpha = if (backgroundShape.value == 0f) {
                            1f
                        } else {
                            0f
                        }
                    }
                    .pointerInteropFilter {
                        // Return false here to pass down the MotionEvent
                        return@pointerInteropFilter false
                    },
            ) {
                Text(
                    text = "$percent lbs",
                    color = greenColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .layoutId("horizontal_guideline")
            ) {

            }

            Text(
                text = "17",
                modifier = Modifier
                    .layoutId("lbs_value"),
                color = Color.White,
                fontSize = 30.sp
            )

            Text(
                text = "lbs",
                modifier = Modifier
                    .layoutId("lbs_label"),
                color = Color.White,
                fontSize = 20.sp
            )

            Text(
                text = "Total weight loss",
                modifier = Modifier.layoutId("lbs_hint"),
                color = Color.White,
                fontSize = 20.sp
            )

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(50),
                onClick = {

                },
                modifier = Modifier
                    .width(120.dp)
                    .layoutId("submitButton"),
            ) {
                Text(
                    text = "Submit",
                    color = greenColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }
//
//        Slider(
//            value = progress,
//            onValueChange = {
//                progress = it
//            },
//            modifier = Modifier
//                .padding(horizontal = 32.dp)
//        )
    }
}

@OptIn(ExperimentalMotionApi::class)
@Composable
fun NotebookScreen() {

    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxSize()
    ) {
        var progress by remember {
            mutableStateOf(0f)
        }
        val context = LocalContext.current
        val motionSceneContent = remember {
            context.resources
                .openRawResource(R.raw.option_scene)
                .readBytes()
                .decodeToString()
        }

        MotionLayout(
            motionScene = MotionScene(motionSceneContent),
            progress = progress,
            modifier = Modifier
                .fillMaxWidth(),
            debug = EnumSet.of(MotionLayoutDebugFlags.SHOW_ALL)
        ) {
            Image(
                painter = rememberVectorPainter(image = Icons.Default.AccountCircle),
                contentDescription = "desc",
                modifier = Modifier
                    .layoutId("my_image")
            )
            Divider(
                color = Color.Gray,
                thickness = 2.dp,
                modifier = Modifier
                    .layoutId("my_divider")
            )
        }

        Slider(
            value = progress,
            onValueChange = {
                progress = it
            },
            modifier = Modifier
                .padding(horizontal = 32.dp)
        )
    }

    //    var backgroundCenter1 by remember { mutableStateOf(Offset(0f, 0f)) }
//    var backgroundCenterAnimated by remember { mutableStateOf(Offset(0f, 0f)) }
//    var translation = animateOffsetAsState(targetValue = backgroundCenterAnimated)
//    var screenSize by remember { mutableStateOf(Size(0f, 0f)) }
//
//    Box(modifier = Modifier
//        .fillMaxSize()
//        .onPlaced {
//            screenSize = Size(it.size.width.toFloat(), it.size.height.toFloat())
//        }) {
//        Box(
//            modifier = Modifier
//                .padding(top = 200.dp, start = 50.dp)
//                .wrapContentSize()
//        ) {
//            Box(
//                modifier = Modifier
//                    .graphicsLayer {
//
//                    }
//                    .absoluteOffset {
//                        Log.i("AdrianTest", "translation: $translation")
//                        if (translation.value.x != 0f) {
//                            IntOffset(
//                                translation.value.x.toInt() - backgroundCenter1.x.toInt(),
//                                translation.value.y.toInt() - backgroundCenter1.y.toInt()
//                            )
//                        } else {
//                            IntOffset(0, 0)
//                        }
//                    }
//                    .align(Alignment.Center)
//                    .size(100.dp)
//                    .background(greenColor, RoundedCornerShape(50))
//                    .onPlaced {
//                        if (translation.value.x == 0f) {
//                            backgroundCenter1 = it.positionInWindow()
//                        }
//                        Log.i("AdrianTest", "onPlaced: ${it.positionInWindow()}")
//                    }
//            ) {
//
//            }
//
//            Column(
//                modifier =
//                Modifier
//                    .align(Alignment.Center)
//                    .clickable {
//                        Log.i("AdrianTest", "start anim")
//                        backgroundCenterAnimated =
//                            Offset(screenSize.width / 2, screenSize.height / 2)
//                    }
//            ) {
//                Text(
//                    modifier = Modifier.align(Alignment.CenterHorizontally),
//                    text = "17"
//                )
//                Text(
//                    modifier = Modifier.align(Alignment.CenterHorizontally),
//                    text = "lbs"
//                )
//            }
//        }
//
//    }
}

@Preview
@Composable
fun NotebookScreenPreview() {
    NotebookScreen()
}