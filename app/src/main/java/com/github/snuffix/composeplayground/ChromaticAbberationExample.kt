package com.github.snuffix.composeplayground

import android.graphics.Bitmap
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp

const val ShaderSource = """
    uniform shader composable;
    uniform float2 size;
    uniform float amount;
    
    half4 main(float2 fragCoord) {
//      return half4(1.0, 0.0, 0.0, 1.0);
//        return half4(fragCoord.x/size.x, 0.0, 0.0, 1.0);
//      return composable.eval(fragCoord).bgra;
//      return composable.eval(fragCoord).ggga;
//      return composable.eval(fragCoord).1g0a;
      float distance = length(fragCoord - size * 0.5);
      float displacement = pow(distance / max(size.x, size.y), 2.0) * amount;
      half4 color = composable.eval(fragCoord);
      color.rgb = half3(
            composable.eval(fragCoord - displacement).r,
            color.g,
            composable.eval(fragCoord + displacement).b
      );
      color.rgb *= color.a;
      return color;
    }
    

"""

@Composable
fun ChromaticAberrationExample(photo: Bitmap) {
    val shader = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            RuntimeShader(ShaderSource)
        } else {
            TODO("VERSION.SDK_INT < TIRAMISU")
        }
    }

    var chromaticAmount by remember {
        mutableFloatStateOf(100.0f)
    }

    Column {
        Image(
            modifier = Modifier
                .onSizeChanged { size ->
                    shader.setFloatUniform("size", size.width.toFloat(), size.height.toFloat())
                }
                .graphicsLayer {
                    clip = true
                    shader.setFloatUniform("amount", chromaticAmount)
                    renderEffect = RenderEffect
                        .createRuntimeShaderEffect(shader, "composable")
                        .asComposeRenderEffect()
                },
            bitmap = photo.asImageBitmap(),
            contentDescription = ""
        )

        Slider(
            modifier = Modifier.padding(top = 16.dp),
            value = chromaticAmount,
            onValueChange = {
                chromaticAmount = it
            },
            valueRange = 1.0f..150f
        )
    }
}