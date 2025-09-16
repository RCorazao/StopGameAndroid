package com.reicode.stopgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

        lifecycleScope.launch {
            try {
                signalRService.connect()
            } catch (e: Exception) {
                println("❌ Failed to connect: ${e.message}")
            }
        }

        setContent {
            AppNav(signalRService)
        }
    }
}
