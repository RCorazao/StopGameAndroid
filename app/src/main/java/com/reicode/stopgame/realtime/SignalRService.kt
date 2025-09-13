package com.reicode.stopgame.realtime

import com.reicode.stopgame.navigation.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SignalRService {
    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Home)
    val screenState: StateFlow<ScreenState> = _screenState

    fun createRoom(playerName: String) {
        // TODO: call server via SignalR
        // On server confirmation:
        // _screenState.value = ScreenState.Lobby
    }

    fun joinRoom(playerName: String, roomCode: String) {
        // TODO: call server via SignalR
        // On server confirmation:
        // _screenState.value = ScreenState.Lobby
    }

    fun onRoomStateChanged(state: Int) {
        _screenState.value = when (state) {
            0 -> ScreenState.Lobby
            1 -> ScreenState.Playing
            2 -> ScreenState.Voting
            3 -> ScreenState.Results
            4 -> ScreenState.Finished
            else -> ScreenState.Home
        }
    }
}