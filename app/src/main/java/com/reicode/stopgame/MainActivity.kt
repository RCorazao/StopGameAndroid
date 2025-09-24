package com.reicode.stopgame

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var signalRService: SignalRService
    
    private var backPressedTime: Long = 0
    private val backPressInterval: Long = 2000 // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.sleep(2000)
        installSplashScreen()
        
        // Setup double back press to minimize
        setupDoubleBackPressToMinimize()

        lifecycleScope.launch {
            try {
                signalRService.connect()
            } catch (e: Exception) {
                println("❌ Failed to connect: ${e.message}")
            }
        }

        setContent {
            val gradient =
                    Brush.verticalGradient(
                            colors = listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6), Color.White),
                    )

            Scaffold(contentWindowInsets = WindowInsets.safeDrawing) { innerPadding ->
                Box(modifier = Modifier.fillMaxSize().background(gradient).padding(innerPadding)) {
                    AppNav(signalRService)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Notify SignalR service that app is in foreground
        signalRService.setAppInForeground(true)
    }

    override fun onStop() {
        super.onStop()
        // Notify SignalR service that app is in background
        signalRService.setAppInForeground(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Backup disconnect when activity is being destroyed
        lifecycleScope.launch {
            try {
                signalRService.disconnect()
                println("🔌 Disconnected due to activity destroy")
            } catch (e: Exception) {
                println("❌ Failed to disconnect on app destroy: ${e.message}")
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Handle configuration changes (like screen rotation) without disconnecting
        println("🔄 Configuration changed - SignalR connection maintained")
    }
    
    private fun setupDoubleBackPressToMinimize() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentTime = System.currentTimeMillis()
                
                if (currentTime - backPressedTime < backPressInterval) {
                    // Second press within interval - minimize the app
                    moveTaskToBack(true)
                } else {
                    // First press - show toast and record time
                    backPressedTime = currentTime
                    Toast.makeText(
                        this@MainActivity,
                        "Press back again to minimize",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        
        onBackPressedDispatcher.addCallback(this, callback)
    }
}
