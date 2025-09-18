package com.reicode.stopgame.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reicode.stopgame.feature.home.HomeScreen
import com.reicode.stopgame.feature.lobby.LobbyScreen
import com.reicode.stopgame.realtime.SignalRService
import com.reicode.stopgame.realtime.dto.RoomDto
import com.reicode.stopgame.realtime.dto.RoomState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

sealed class ScreenState {
    object Home : ScreenState()
    object Lobby : ScreenState()
    object Playing : ScreenState()
    object Voting : ScreenState()
    object Results : ScreenState()
    object Finished : ScreenState()
}

fun RoomDto.toScreenState(): ScreenState {
    return when (RoomState.fromValue(state)) {
        RoomState.Waiting -> ScreenState.Lobby
        RoomState.Playing -> ScreenState.Playing
        RoomState.Voting -> ScreenState.Voting
        RoomState.Results -> ScreenState.Results
        RoomState.Finished -> ScreenState.Finished
    }
}

@Composable
fun AppNav(signalRService: SignalRService) {
    val screenState = signalRService.screenState.collectAsStateWithLifecycle().value
    val room        = signalRService.room.collectAsStateWithLifecycle().value
    val player      = signalRService.player.collectAsStateWithLifecycle().value
    val isUpdatingSettings = signalRService.isUpdatingSettings.collectAsStateWithLifecycle().value
    val error = signalRService.error.collectAsStateWithLifecycle().value

    when (screenState) {
        is ScreenState.Home -> HomeScreen(
            onCreateRoom = { name -> signalRService.createRoom(name) },
            onJoinRoom = { code, name -> signalRService.joinRoom(code, name) }
        )

        is ScreenState.Lobby -> LobbyScreen(
            room = room,
            currentPlayer = player,
            onUpdateRoomSettings = { roomSettings ->
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        signalRService.updateRoomSettings(roomSettings)
                    } catch (e: Exception) {
                        // Error is handled in SignalR service
                    }
                }
            },
            onStartRound = {
                // TODO
            },
            onLeaveRoom = {
                signalRService.leaveRoom()
            },
            isUpdatingSettings = isUpdatingSettings,
            updateError = error,
            onClearError = { signalRService.clearError() }
        )

        else -> {}
    }
}
