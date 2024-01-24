package com.github.snuffix.composeplayground

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule

class LazyListFooterScreenTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        renderingMode = SessionParams.RenderingMode.NORMAL,
        showSystemUi = false,
        maxPercentDifference = 1.0,
    )

    @Test
    fun launchWelcomeLazyListFooterScreen() {
        paparazzi.snapshot {
            LazyListFooterScreen()
        }
    }
}