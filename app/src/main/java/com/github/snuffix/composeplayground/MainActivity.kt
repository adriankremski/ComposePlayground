package com.github.snuffix.composeplayground

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.snuffix.composeplayground.ui.theme.ComposePlaygroundTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.concurrent.Executors
import kotlin.coroutines.resume


//val threadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
//val threadDispatcher2 = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
val threadDispatcher = Dispatchers.IO
val threadDispatcher2 = threadDispatcher
fun test() {
    GlobalScope.launch(threadDispatcher) {
        launch {
            repeat(10) {
                emptySuspend(1, it)
            }
        }
        launch {
            repeat(10) {
                emptySuspend(2, it)
            }
        }
    }
}

suspend fun emptySuspend(num: Int, num2: Int) = coroutineScope {
    Log.i("AdrianTest", "$num. Hello $num2")
    val result = async {
        withContext(threadDispatcher2) {
            calculateSomething(100000)
        }
    }

    result.await()
}


suspend fun calculateSomething(num: Int) {
    (0..num)
        .toList()
        .onEach {
            point()
        }
        .sortedDescending()
        .onEach {
            point()
        }
        .sorted()
        .onEach {
            point()
        }
        .sortedDescending()
        .onEach {
            point()
        }
}

suspend fun point() {}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var data = List(11) {
            val startOfMonth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate.now().withMonth(it + 1).with(TemporalAdjusters.firstDayOfMonth())
            } else {
                TODO("VERSION.SDK_INT < O")
            }

            val endOfMonth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate.now().withMonth(it + 1).with(TemporalAdjusters.lastDayOfMonth())
            } else {
                TODO("VERSION.SDK_INT < O")
            }

            CalendarData(
                name = startOfMonth.month.name,
                firstDayOfMonth = startOfMonth,
                lastDateOfMonth = endOfMonth,
            )
        } + List(11) {
            val startOfMonth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate.now().withMonth(it + 1).with(TemporalAdjusters.firstDayOfMonth())
            } else {
                TODO("VERSION.SDK_INT < O")
            }

            val endOfMonth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate.now().withMonth(it + 1).with(TemporalAdjusters.lastDayOfMonth())
            } else {
                TODO("VERSION.SDK_INT < O")
            }

            CalendarData(
                name = startOfMonth.month.name,
                firstDayOfMonth = startOfMonth,
                lastDateOfMonth = endOfMonth,
            )
        } + List(11) {
            val startOfMonth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate.now().plusYears(1).withMonth(it + 1)
                    .with(TemporalAdjusters.firstDayOfMonth())
            } else {
                TODO()
            }

            val endOfMonth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate.now().plusYears(1).withMonth(it + 1)
                    .with(TemporalAdjusters.lastDayOfMonth())
            } else {
                TODO()
            }

            CalendarData(
                name = startOfMonth.month.name,
                firstDayOfMonth = startOfMonth,
                lastDateOfMonth = endOfMonth,
            )
        }

        data = data.mapIndexed { index, data ->
            Log.i("AdrianTest", "index: ${data.index}")
            data.copy(index = index)
        }

        val photo = BitmapFactory.decodeResource(resources, R.drawable.landscape)
        setContent {
            ComposePlaygroundTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val selectedScreen = remember { mutableStateOf<Screen?>(null) }

                    BackHandler {
                        selectedScreen.value = null
                    }

                    when (selectedScreen.value) {
                        Screen.ListAnimation -> {
                            AndroidListAnimation()
                        }

                        Screen.Graph -> {
                            GraphExample()
                        }

                        Screen.GraphicLayer -> {
                            GraphicLayerExample()
                        }

                        Screen.LazyListFooterHeader -> {
                            LazyListFooterExample()
                        }

                        Screen.Shader -> {
                            ChromaticAberrationExample(photo)
                        }

                        Screen.StepsList -> {
                            StepsList(0..10)
                        }

                        Screen.FloHealthAnimation -> {
                            val mode = remember { mutableStateOf(0) }

                            Column {
                                var pivotFractionX by remember { mutableStateOf(0f) }
                                var pivotFractionY by remember { mutableStateOf(0f) }
                                var selectedDateNumber by remember { mutableStateOf(0) }

                                Row(
                                ) {
                                    Text(
                                        text = "Month",
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(if (mode.value == 0) Color.Red else Color.Transparent)
                                            .padding(16.dp)
                                            .clickable {
                                                pivotFractionX = 0f
                                                pivotFractionY = 0f
                                                mode.value = 0
                                            }
                                    )

                                    Text(
                                        text = "Year",
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(if (mode.value == 1) Color.Red else Color.Transparent)
                                            .padding(16.dp)
                                            .clickable {
                                                mode.value = 1
                                            }
                                    )
                                }

                                if (mode.value == 0) {
                                    Calendar(
                                        calendarData = CalendarList(data),
                                        calendarModifier = Modifier.fillMaxWidth(),
                                        pivotFractionX = pivotFractionX,
                                        pivotFractionY = pivotFractionY,
                                        selectedDateNumber = selectedDateNumber
                                    )
                                } else {
                                    YearCalendar(
                                        calendarData = CalendarList(data),
                                        calendarModifier = Modifier.fillMaxWidth(),
                                        onMonthSelected = { data, x, y ->
                                            selectedDateNumber = data.index
                                            pivotFractionX = x.toFloat()
                                            pivotFractionY = y.toFloat()
                                            mode.value = 0
                                        }
                                    )
                                }
                            }
                        }

                        null -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Screen.values().forEach { screen ->
                                    Button(
                                        onClick = {
                                            selectedScreen.value = screen
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(screen.name)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class Screen {
    ListAnimation,
    Graph,
    GraphicLayer,
    LazyListFooterHeader,
    Shader,
    StepsList,
    FloHealthAnimation
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposePlaygroundTheme {
        Greeting("Android")
    }
}