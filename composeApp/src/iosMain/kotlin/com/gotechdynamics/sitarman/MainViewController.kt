package com.gotechdynamics.sitarman

import com.gotechdynamics.sitarman.di.initKoin
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }