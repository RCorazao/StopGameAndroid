package com.reicode.stopgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.reicode.stopgame.navigation.AppNav
import com.reicode.stopgame.realtime.SignalRService
import com.reicode.stopgame.ui.theme.StopGameTheme

class MainActivity : ComponentActivity() {

    private val signalRService = SignalRService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StopGameTheme {
                AppNav(signalRService)
            }
        }
    }
}
