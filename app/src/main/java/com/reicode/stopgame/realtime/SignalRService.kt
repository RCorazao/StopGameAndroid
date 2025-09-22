package com.reicode.stopgame.realtime

import com.google.gson.Gson
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.reicode.stopgame.domain.model.ConnectionState
import com.reicode.stopgame.feature.lobby.data.RoomSettings
import com.reicode.stopgame.navigation.ScreenState
import com.reicode.stopgame.navigation.toScreenState
import com.reicode.stopgame.realtime.dto.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(DelicateCoroutinesApi::class)
class SignalRService(hubUrl: String) {
    private val gson = Gson()

    // Connection state tracking
    private val _connectionState = MutableStateFlow(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    // Reconnection properties
    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 5

    // App lifecycle tracking
    private var isAppInForeground = true

    // Room recovery tracking
    private var isAttemptingRoomRecovery = false

    // Global session state
    private val _room = MutableStateFlow<RoomDto?>(null)
    val room: StateFlow<RoomDto?> = _room.asStateFlow()

    private val _player = MutableStateFlow<PlayerDto?>(null)
    val player: StateFlow<PlayerDto?> = _player.asStateFlow()

    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Home)
    val screenState: StateFlow<ScreenState> = _screenState.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isUpdatingSettings = MutableStateFlow(false)
    val isUpdatingSettings: StateFlow<Boolean> = _isUpdatingSettings.asStateFlow()

    private val _shouldSubmitAnswers = MutableStateFlow(false)
    val shouldSubmitAnswers: StateFlow<Boolean> = _shouldSubmitAnswers.asStateFlow()

    private val _voteAnswers = MutableStateFlow<List<VoteAnswerDto>>(emptyList())
    val voteAnswers: StateFlow<List<VoteAnswerDto>> = _voteAnswers.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessageDto>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessageDto>> = _chatMessages.asStateFlow()

    private val _unreadMessageCount = MutableStateFlow(0)
    val unreadMessageCount: StateFlow<Int> = _unreadMessageCount.asStateFlow()

    private val _isChatOpen = MutableStateFlow(false)

    private val connection: HubConnection = HubConnectionBuilder.create(hubUrl).build()

    init {
        connection.on(
                "RoomCreated",
                { room: RoomDto, me: PlayerDto ->
                    println("Room created: ${room}")
                    applyRoomAndPlayer(room, me)
                },
                RoomDto::class.java,
                PlayerDto::class.java
        )

        connection.on(
                "RoomJoined",
                { room: RoomDto, me: PlayerDto ->
                    println("Room joined: ${room}")
                    applyRoomAndPlayer(room, me)
                },
                RoomDto::class.java,
                PlayerDto::class.java
        )

        connection.on(
                "RoomUpdated",
                { room: RoomDto ->
                    println("Room updated: ${room}")
                    applyRoom(room)
                    refreshSelfFrom(room)

                    kotlinx.coroutines.GlobalScope.launch {
                        delay(500)
                        _isUpdatingSettings.value = false
                    }
                },
                RoomDto::class.java
        )

        connection.on(
                "RoundStarted",
                { room: RoomDto ->
                    println("Round started: ${room}")
                    applyRoom(room)
                },
                RoomDto::class.java
        )

        connection.on(
                "RoundStopped",
                {
                    println("Round stopped")
                    // applyRoom(room)
                    // Trigger automatic answer submission
                    _shouldSubmitAnswers.value = true
                }
        )

        connection.on(
                "VoteStarted",
                { voteAnswers: Array<VoteAnswerDto> ->
                    println("Vote started with ${voteAnswers.size} topics")
                    _voteAnswers.value = voteAnswers.toList()
                },
                Array<VoteAnswerDto>::class.java
        )

        connection.on(
                "VoteUpdate",
                { voteAnswers: Array<VoteAnswerDto> ->
                    println("Vote updated")
                    _voteAnswers.value = voteAnswers.toList()
                },
                Array<VoteAnswerDto>::class.java
        )

        connection.on(
                "ChatNotification",
                { chatMessage: ChatMessageDto ->
                    println("Chat message received: ${chatMessage.message} from ${chatMessage.source}")
                    val currentMessages = _chatMessages.value.toMutableList()
                    currentMessages.add(chatMessage)
                    _chatMessages.value = currentMessages
                    
                    // Only increment unread count when chat is closed
                    if (!_isChatOpen.value) {
                        _unreadMessageCount.value = _unreadMessageCount.value + 1
                    }
                },
                ChatMessageDto::class.java
        )

        // Room reconnection response handler
        connection.on(
                "RoomReconnected",
                { room: RoomDto, me: PlayerDto ->
                    println("Room reconnected successfully: ${room.code}")
                    applyRoomAndPlayer(room, me)
                    // Clear room recovery flag on successful reconnection
                    isAttemptingRoomRecovery = false
                },
                RoomDto::class.java,
                PlayerDto::class.java
        )

        // Error channel
        connection.on(
                "Error",
                { errorMsg: String ->
                    println("❌ SignalR Error received: $errorMsg")

                    // Check if this error is related to room recovery
                    if (isAttemptingRoomRecovery && (errorMsg.contains("Room not found", ignoreCase = true))
                    ) {
                        println("🔄 Room recovery failed - room no longer exists, clearing room data")
                        clearRoomData()
                        isAttemptingRoomRecovery = false
                        // Don't set this as a user-facing error since it's expected behavior
                    } else {
                        // Regular error handling
                        _error.value = errorMsg
                        _isUpdatingSettings.value = false
                    }
                },
                String::class.java
        )

        // Connection closed handler
        connection.onClosed { exception ->
            println("🔌 SignalR connection closed: ${exception?.message ?: "Unknown reason"}")
            println("🔌 App in foreground: $isAppInForeground")

            // Only attempt reconnection if we were previously connected (not during initial
            // connection)
            if (_connectionState.value == ConnectionState.Connected) {
                if (isAppInForeground) {
                    // App is in foreground - attempt reconnection immediately
                    updateConnectionState(ConnectionState.Reconnecting)
                    kotlinx.coroutines.GlobalScope.launch { attemptReconnection() }
                } else {
                    // App is in background - don't reconnect to save battery
                    println("🔌 App in background, skipping reconnection to save battery")
                    updateConnectionState(ConnectionState.Disconnected)
                }
            } else if (_connectionState.value == ConnectionState.Connecting) {
                // If connection was lost during initial connection attempt, set to Failed
                updateConnectionState(ConnectionState.Failed)
            }
        }
    }

    // --- Public API ---

    suspend fun connect() {
        withContext(Dispatchers.IO) {
            try {
                // Set state to Connecting before attempting connection
                updateConnectionState(ConnectionState.Connecting)

                connection.start().blockingAwait()

                // Reset reconnection attempts on successful initial connection
                reconnectAttempts = 0

                // Set state to Connected on successful connection
                updateConnectionState(ConnectionState.Connected)
                println("✅ SignalR connected")
                handleRoomRecovery()
            } catch (e: Exception) {
                // Set state to Failed on connection failure
                updateConnectionState(ConnectionState.Failed)
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

    fun updateRoomSettings(roomSettings: RoomSettings) {
        try {
            val currentRoom = _room.value

            if (currentRoom == null) return

            _isUpdatingSettings.value = true
            _error.value = null

            val updateRequest =
                    UpdateRoomSettingsRequest(
                            maxPlayers = roomSettings.maxPlayers,
                            maxRounds = roomSettings.maxRounds,
                            roundDurationSeconds = roomSettings.roundDurationSeconds,
                            votingDurationSeconds = roomSettings.votingDurationSeconds,
                            topics = roomSettings.topics.map { it.name }
                    )
            connection.send("UpdateRoomSettings", currentRoom.code, updateRequest)
        } catch (e: Exception) {
            println("❌ Failed to update room settings: ${e.message}")
            _error.value = "Failed to update room settings: ${e.message}"
            _isUpdatingSettings.value = false
            throw e
        }
    }

    fun startRound() {
        try {
            connection.send("StartRound")
        } catch (e: Exception) {
            println("❌ Failed to leave room: ${e.message}")
        }
    }

    fun stopRound() {
        try {
            connection.send("Stop")
        } catch (e: Exception) {
            println("❌ Failed to leave room: ${e.message}")
        }
    }

    fun submitAnswers(answers: Map<String, String>) {
        try {
            val request = SubmitAnswersRequest(answers)
            connection.send("SubmitAnswers", request)
            println("✅ Answers submitted: $answers")
        } catch (e: Exception) {
            println("❌ Failed to submit answers: ${e.message}")
            _error.value = "Failed to submit answers: ${e.message}"
        }
    }

    fun clearShouldSubmitAnswers() {
        _shouldSubmitAnswers.value = false
    }

    fun leaveRoom() {
        try {
            connection.send("LeaveRoom")
            clearRoomData()
        } catch (e: Exception) {
            println("❌ Failed to leave room: ${e.message}")
            clearRoomData()
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun vote(answerId: String, isValid: Boolean) {
        try {
            val request = VoteRequest(answerId, isValid)
            connection.send("Vote", request)
            println("✅ Vote submitted: answerId=$answerId, isValid=$isValid")
        } catch (e: Exception) {
            println("❌ Failed to submit vote: ${e.message}")
            _error.value = "Failed to submit vote: ${e.message}"
        }
    }

    fun finishVotingPhase() {
        try {
            connection.send("FinishVotingPhase")
            println("✅ Finish voting phase requested")
        } catch (e: Exception) {
            println("❌ Failed to finish voting phase: ${e.message}")
            _error.value = "Failed to finish voting phase: ${e.message}"
        }
    }

    fun sendChat(message: String) {
        try {
            connection.send("SendChat", message)
            println("✅ Chat message sent: $message")
        } catch (e: Exception) {
            println("❌ Failed to send chat message: ${e.message}")
            _error.value = "Failed to send chat message: ${e.message}"
        }
    }

    fun markMessagesAsRead() {
        _unreadMessageCount.value = 0
    }

    fun setChatOpen(isOpen: Boolean) {
        _isChatOpen.value = isOpen
        if (isOpen) {
            // Clear unread count when chat is opened
            _unreadMessageCount.value = 0
        }
    }

    suspend fun disconnect() {
        withContext(Dispatchers.IO) {
            try {
                println("🔌 Disconnecting SignalR connection...")

                // Stop the SignalR connection
                connection.stop().blockingAwait()

                // Clear all room data on explicit disconnect
                clearRoomData()

                // Set connection state to Disconnected after successful disconnect
                updateConnectionState(ConnectionState.Disconnected)

                println("✅ SignalR disconnected successfully")
            } catch (e: Exception) {
                println("❌ Failed to disconnect: ${e.message}")
                _error.value = "Failed to disconnect: ${e.message}"

                // Even if disconnect fails, still clear room data and set state
                clearRoomData()
                updateConnectionState(ConnectionState.Disconnected)

                throw e
            }
        }
    }

    fun setAppInForeground(inForeground: Boolean) {
        isAppInForeground = inForeground
        println("🔌 App lifecycle changed: ${if (inForeground) "FOREGROUND" else "BACKGROUND"}")

        if (inForeground) {
            // App came back to foreground
            when (_connectionState.value) {
                ConnectionState.Disconnected -> {
                    // Try to reconnect when app returns to foreground
                    println("🔌 App returned to foreground, attempting to reconnect")
                    kotlinx.coroutines.GlobalScope.launch {
                        try {
                            connect()
                        } catch (e: Exception) {
                            println("❌ Failed to reconnect on foreground: ${e.message}")
                        }
                    }
                }
                ConnectionState.Failed -> {
                    println(
                            "🔌 App returned to foreground, connection was failed - ready for manual retry"
                    )
                }
                else -> {
                    println(
                            "🔌 App returned to foreground, connection state: ${_connectionState.value}"
                    )
                }
            }
        }
    }

    // --- Internals ---

    private fun updateConnectionState(state: ConnectionState) {
        _connectionState.value = state
        println("🔌 Connection state updated to: $state")
    }

    private suspend fun attemptReconnection() {
        withContext(Dispatchers.IO) {
            while (reconnectAttempts < maxReconnectAttempts && isAppInForeground) {
                try {
                    reconnectAttempts++

                    println("🔄 Attempting reconnection #$reconnectAttempts/$maxReconnectAttempts")

                    // Calculate exponential backoff delay (2000ms * attempts)
                    val delayMs = 2000L * reconnectAttempts
                    delay(delayMs)

                    // Check again if app is still in foreground after delay
                    if (!isAppInForeground) {
                        println("🔌 App went to background during reconnection, stopping attempts")
                        updateConnectionState(ConnectionState.Disconnected)
                        return@withContext
                    }

                    // Attempt to reconnect
                    connection.start().blockingAwait()

                    // Reset attempt counter on successful reconnection
                    reconnectAttempts = 0
                    updateConnectionState(ConnectionState.Connected)
                    println("✅ Reconnection successful")

                    // Attempt room recovery if we have active room data
                    handleRoomRecovery()

                    return@withContext
                } catch (e: Exception) {
                    println("❌ Reconnection attempt #$reconnectAttempts failed: ${e.message}")

                    if (reconnectAttempts >= maxReconnectAttempts) {
                        updateConnectionState(ConnectionState.Failed)
                        println("❌ Max reconnection attempts exceeded, connection failed")
                        return@withContext
                    }
                }
            }
        }
    }

    private suspend fun handleRoomRecovery() {
        withContext(Dispatchers.IO) {
            try {
                val currentRoom = _room.value
                val currentPlayer = _player.value

                // Check if we have room and player data to recover
                if (currentRoom == null || currentPlayer == null) {
                    println("🔄 No room data to recover")
                    return@withContext
                }

                // Check if room state is not Finished before attempting recovery
                val roomState = RoomState.fromValue(currentRoom.state)
                if (roomState == RoomState.Finished) {
                    println("🔄 Room is finished, skipping recovery")
                    clearRoomData()
                    return@withContext
                }

                println(
                        "🔄 Attempting room recovery for room: ${currentRoom.code}, player: ${currentPlayer.id}"
                )

                // Set flag to track room recovery attempt
                isAttemptingRoomRecovery = true

                // Create reconnection request with current room code and player ID
                val reconnectRequest =
                        ReconnectRoomRequest(
                                roomCode = currentRoom.code,
                                playerId = currentPlayer.id
                        )

                // Invoke ReconnectRoom server method
                connection.send("ReconnectRoom", reconnectRequest)
                println("✅ Room recovery request sent")
            } catch (e: Exception) {
                println("❌ Room recovery failed: ${e.message}")
                // Clear room data and return to home screen on recovery failure
                clearRoomData()
                isAttemptingRoomRecovery = false
                _error.value = "Failed to recover room session: ${e.message}"
            }
        }
    }

    private fun clearRoomData() {
        _room.value = null
        _player.value = null
        _screenState.value = ScreenState.Home
        _error.value = null
        _isUpdatingSettings.value = false
        _shouldSubmitAnswers.value = false
        _voteAnswers.value = emptyList()
        _chatMessages.value = emptyList()
        _unreadMessageCount.value = 0
        _isChatOpen.value = false
        println("✅ Room data cleared, returning to home screen")
    }

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
     * Keep the same player identity and refresh its fields from the latest room snapshot. Safe
     * no-op if we don't yet know who we are.
     */
    private fun refreshSelfFrom(room: RoomDto) {
        val current = _player.value ?: return
        val updated = room.players.firstOrNull { it.id == current.id }
        if (updated != null) _player.value = updated
    }
}
