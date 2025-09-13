package com.reicode.stopgame.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.reicode.stopgame.feature.home.HomeScreen
import com.reicode.stopgame.realtime.SignalRService

sealed class ScreenState {
    object Home : ScreenState()
    object Lobby : ScreenState()
    object Playing : ScreenState()
    object Voting : ScreenState()
    object Results : ScreenState()
    object Finished : ScreenState()
}

@Composable
fun AppNav(signalRService: SignalRService) {
    val screenState by signalRService.screenState.collectAsState()

    when (screenState) {
        is ScreenState.Home -> HomeScreen(
            onCreateRoom = { name -> signalRService.createRoom(name) },
            onJoinRoom = { name, code -> signalRService.joinRoom(name, code) }
        )
        else -> {}
    }
}
