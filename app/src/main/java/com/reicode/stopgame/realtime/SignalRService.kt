package com.reicode.stopgame.realtime

import com.google.gson.Gson
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.reicode.stopgame.realtime.dto.*
import com.reicode.stopgame.navigation.ScreenState
import com.reicode.stopgame.navigation.toScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SignalRService(
    hubUrl: String
) {
    private val gson = Gson()

    // Global session state
    private val _room = MutableStateFlow<RoomDto?>(null)
    val room: StateFlow<RoomDto?> = _room.asStateFlow()

    private val _player = MutableStateFlow<PlayerDto?>(null)
    val player: StateFlow<PlayerDto?> = _player.asStateFlow()

    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Home)
    val screenState: StateFlow<ScreenState> = _screenState.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val connection: HubConnection =
        HubConnectionBuilder.create(hubUrl).build()

    init {
        connection.on("RoomCreated", { room: RoomDto, me: PlayerDto ->
            println("Room created: ${room}")
            applyRoomAndPlayer(room, me)
        }, RoomDto::class.java, PlayerDto::class.java)

        connection.on("RoomJoined", { room: RoomDto, me: PlayerDto ->
            println("Room joined: ${room}")
            applyRoomAndPlayer(room, me)
        }, RoomDto::class.java, PlayerDto::class.java)

        connection.on("RoomUpdated", { room: RoomDto ->
            println("Room updated: ${room}")
            applyRoom(room)
            refreshSelfFrom(room)
        }, RoomDto::class.java)

        // Error channel
        connection.on("Error", { errorMsg: String ->
            _error.value = errorMsg
        }, String::class.java)
    }

    // --- Public API ---

    suspend fun connect() {
        withContext(Dispatchers.IO) {
            try {
                connection.start().blockingAwait()
                println("✅ SignalR connected")
            } catch (e: Exception) {
                println("❌ Failed to connect: ${e.message}")
                throw e
            }
        }
    }

    fun createRoom(hostName: String) {
        connection.send("CreateRoom", CreateRoomRequest(hostName))
    }

    fun joinRoom(roomCode: String, playerName: String) {
        connection.send("JoinRoom", JoinRoomRequest(roomCode, playerName))
    }

    // --- Internals ---

    private fun applyRoomAndPlayer(room: RoomDto, me: PlayerDto) {
        _room.value = room
        _player.value = me
        _screenState.value = room.toScreenState()
    }

    private fun applyRoom(room: RoomDto) {
        _room.value = room
        _screenState.value = room.toScreenState()
    }

    /**
     * Keep the same player identity and refresh its fields from the latest room snapshot.
     * Safe no-op if we don't yet know who we are.
     */
    private fun refreshSelfFrom(room: RoomDto) {
        val current = _player.value ?: return
        val updated = room.players.firstOrNull { it.id == current.id }
        if (updated != null) _player.value = updated
    }
}
