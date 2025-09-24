package com.reicode.stopgame

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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

            var showInternetDialog by remember { mutableStateOf(false) }
            val context = LocalContext.current

            // Check internet connection on app start
            LaunchedEffect(Unit) {
                if (!isInternetAvailable(context)) {
                    showInternetDialog = true
                }
            }

            Scaffold(contentWindowInsets = WindowInsets.safeDrawing) { innerPadding ->
                Box(modifier = Modifier.fillMaxSize().background(gradient).padding(innerPadding)) {
                    AppNav(signalRService)

                    // Internet connection modal
                    if (showInternetDialog) {
                        InternetConnectionDialog(
                                onRetry = {
                                    if (isInternetAvailable(context)) {
                                        showInternetDialog = false
                                        // Attempt to reconnect SignalR
                                        lifecycleScope.launch {
                                            try {
                                                signalRService.connect()
                                                println("🔌 SignalR reconnected after internet recovery")
                                            } catch (e: Exception) {
                                                println("❌ Failed to reconnect SignalR: ${e.message}")
                                            }
                                        }
                                    }
                                },
                                onExit = { moveTaskToBack(true) }
                        )
                    }
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
        val callback =
                object : OnBackPressedCallback(true) {
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
                                            getString(R.string.press_back_again_to_minimize),
                                            Toast.LENGTH_SHORT
                                    )
                                    .show()
                        }
                    }
                }

        onBackPressedDispatcher.addCallback(this, callback)
    }

    @Suppress("DEPRECATION")
    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // API 23 and above
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                    connectivityManager.getNetworkCapabilities(network) ?: return false

            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } else {
            // API 21-22 (deprecated but necessary for compatibility)
            val activeNetworkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            activeNetworkInfo?.isConnected == true
        }
    }
}

@Composable
fun InternetConnectionDialog(onRetry: () -> Unit, onExit: () -> Unit) {
    AlertDialog(
            onDismissRequest = { /* Prevent dismissing by clicking outside */},
            icon = {
                Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                        text = stringResource(R.string.no_internet_connection),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column {
                    Text(
                            text = stringResource(R.string.internet_required_message),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                            text = stringResource(R.string.check_connection_message),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onExit, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.exit))
                    }
                    Button(onClick = onRetry, modifier = Modifier.weight(1f)) { 
                        Text(stringResource(R.string.retry)) 
                    }
                }
            },
            shape = RoundedCornerShape(16.dp)
    )
}
