package com.github.snuffix.composeplayground

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.snuffix.composeplayground.bottom_menu.PhotoPickerScreen
import com.github.snuffix.composeplayground.calendar.CalendarScreen
import com.github.snuffix.composeplayground.graphs.GradientLegend
import com.github.snuffix.composeplayground.graphs.DonutChart
import com.github.snuffix.composeplayground.graphs.SpiralGraph
import com.github.snuffix.composeplayground.graphs.colorHigh
import com.github.snuffix.composeplayground.graphs.colorLow
import com.github.snuffix.composeplayground.graphs.colorMid1
import com.github.snuffix.composeplayground.graphs.colorMid2
import com.github.snuffix.composeplayground.notebook.NotebookScreen
import com.github.snuffix.composeplayground.notebook.NotebookScreen2
import com.github.snuffix.composeplayground.process.ProcessAnimationScreen
import com.github.snuffix.composeplayground.ui.theme.ComposePlaygroundTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                            LazyListFooterScreen()
                        }

                        Screen.Shader -> {
                            ChromaticAberrationExample(photo)
                        }

                        Screen.StepsList -> {
                            StepsList(0..10)
                        }

                        Screen.Calendar -> {
                            CalendarScreen()
                        }

                        Screen.SpiralGraph -> {
                            SpiralGraph()
                        }

                        Screen.GradientLegend -> {
                            GradientLegend()
                        }

                        Screen.PieChart -> {
                            DonutChart(
                                proportions = listOf(0.1f, 0.2f, 0.6f, 0.1f), colors = listOf(
                                    colorLow, colorMid1, colorMid2, colorHigh
                                )
                            )
                        }

                        Screen.ProcessAnimation -> {
                            ProcessAnimationScreen()
                        }

                        Screen.PhotoPickerScreen -> {
                            PhotoPickerScreen()
                        }

                        null -> {
//                            NotebookScreen2()
//                            return@Surface
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
    Calendar,
    SpiralGraph,
    GradientLegend,
    PieChart,
    ProcessAnimation,
    PhotoPickerScreen
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