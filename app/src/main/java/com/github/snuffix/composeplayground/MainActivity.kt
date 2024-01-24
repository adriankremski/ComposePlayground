package com.github.snuffix.composeplayground

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.snuffix.composeplayground.calendar.CalendarScreen
import com.github.snuffix.composeplayground.ui.theme.ComposePlaygroundTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
    Calendar
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