package com.github.snuffix.composeplayground

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
                            LazyListFooterExample()
                        }

                        Screen.Shader -> {
                            ChromaticAbberationExample(photo)
                        }

                        Screen.StepsList -> {
                            StepsList(0..10)
                        }

                        Screen.LazyListFooterExample -> {
                            LazyListFooterExample()
                        }

                        null -> {
                            Column {
                                Text("Select screen")
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
    LazyListFooterExample
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