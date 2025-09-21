package com.reicode.stopgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.reicode.stopgame.navigation.AppNav
import com.reicode.stopgame.realtime.SignalRService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var signalRService: SignalRService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.sleep(2000)
        installSplashScreen()

        lifecycleScope.launch {
            try {
                signalRService.connect()
            } catch (e: Exception) {
                println("❌ Failed to connect: ${e.message}")
            }
        }

        setContent {
            val gradient = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF1E3A8A),
                    Color(0xFF3B82F6),
                    Color.White
                ),
            )

            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradient)
                        .padding(innerPadding)
                ) {
                    AppNav(signalRService)
                }
            }
        }
    }
}
