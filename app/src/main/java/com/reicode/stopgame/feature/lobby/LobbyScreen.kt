package com.reicode.stopgame.feature.lobby

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reicode.stopgame.R
import com.reicode.stopgame.feature.lobby.data.RoomSettings
import com.reicode.stopgame.realtime.dto.PlayerDto
import com.reicode.stopgame.realtime.dto.RoomDto
import com.reicode.stopgame.realtime.dto.TopicDto
import com.reicode.stopgame.shared.GameScreenLayout

@Composable
fun LobbyScreen(
        room: RoomDto?,
        currentPlayer: PlayerDto?,
        onUpdateRoomSettings: (RoomSettings) -> Unit = {},
        onStartRound: () -> Unit = {},
        onLeaveRoom: () -> Unit = {},
        isUpdatingSettings: Boolean = false,
        updateError: String? = null,
        onClearError: () -> Unit = {}
) {
    // Dialog state management with remember and mutableStateOf for edit mode
    var showEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isUpdatingSettings, updateError) {
        if (!isUpdatingSettings && showEditDialog) {
            showEditDialog = false
        }
    }

    GameScreenLayout(
            room = room,
            currentPlayer = currentPlayer,
            onLeaveRoom = onLeaveRoom,
            statusText = stringResource(R.string.status_waiting_players)
    ) {
        // --- Players ---
        Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                        stringResource(R.string.players),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.height(16.dp))

                room?.players?.forEach { player ->
                    Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                                text = player.name,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1F2937),
                                fontSize = 15.sp
                        )
                        if (player.id == currentPlayer?.id) {
                            Spacer(Modifier.width(8.dp))
                            Text(text = stringResource(R.string.you), fontSize = 14.sp, color = Color(0xFF6B7280))
                        }
                        Spacer(Modifier.weight(1f))
                        if (player.isHost) {
                            AssistChip(
                                    onClick = {},
                                    label = {
                                        Text(
                                                stringResource(R.string.host),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                        )
                                    },
                                    colors =
                                            AssistChipDefaults.assistChipColors(
                                                    containerColor = Color(0xFFF59E0B),
                                                    labelColor = Color.White
                                            )
                            )
                        }
                    }
                }
            }
        }

        // --- Room Settings ---
        Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                                Icons.Default.Settings,
                                contentDescription = null,
                                tint = Color(0xFF6B7280),
                                modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                                stringResource(R.string.room_settings),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF1F2937)
                        )
                    }
                    // Only show edit button for host
                    if (currentPlayer?.isHost == true) {
                        TextButton(onClick = { showEditDialog = true }) {
                            Text(stringResource(R.string.edit_settings), color = Color(0xFF3B82F6), fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                                stringResource(R.string.max_players_label),
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280),
                                fontWeight = FontWeight.Medium
                        )
                        Text(
                                "${room?.maxPlayers ?: 0}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937),
                                fontSize = 16.sp
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                                stringResource(R.string.round_duration_label),
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280),
                                fontWeight = FontWeight.Medium
                        )
                        Text(
                                "${room?.roundDurationSeconds ?: 0}s",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937),
                                fontSize = 16.sp
                        )
                    }
                    Column {
                        Text(
                                stringResource(R.string.max_rounds_label),
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280),
                                fontWeight = FontWeight.Medium
                        )
                        Text(
                                "${room?.maxRounds ?: 0}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937),
                                fontSize = 16.sp
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                                stringResource(R.string.voting_duration_label),
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280),
                                fontWeight = FontWeight.Medium
                        )
                        Text(
                                "${room?.votingDurationSeconds ?: 0}s",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937),
                                fontSize = 16.sp
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                Text(
                        stringResource(R.string.topics_label),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1F2937)
                )
                Spacer(Modifier.height(12.dp))

                // Topics in a flow layout using the new TopicsFlowLayout composable
                val topics = room?.topics ?: emptyList()
                TopicsFlowLayout(topics = topics)
            }
        }

        // --- Start Button (Host only) ---
        if (currentPlayer?.isHost == true) {
            Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors =
                            CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                            stringResource(R.string.host_ready_message),
                            textAlign = TextAlign.Center,
                            color = Color(0xFF6B7280),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                            onClick = onStartRound,
                            enabled = (room?.players?.size ?: 0) >= 2,
                            modifier = Modifier.fillMaxWidth(),
                            colors =
                                    ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                                stringResource(R.string.start_game),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    if ((room?.players?.size ?: 0) < 2) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                                stringResource(R.string.need_players),
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280),
                                textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    // Room Settings Edit Dialog
    if (showEditDialog && room != null) {
        RoomSettingsEditDialog(
                currentSettings = RoomSettings.fromRoomDto(room),
                isLoading = isUpdatingSettings,
                error = updateError,
                onDismiss = {
                    if (!isUpdatingSettings) {
                        showEditDialog = false
                        onClearError()
                    }
                },
                onSave = { updatedSettings ->
                    onUpdateRoomSettings(updatedSettings)
                    // Dialog will be closed when update succeeds via real-time update
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LobbyScreenPreview() {
    val sampleTopics =
            listOf(
                    TopicDto(
                            id = "1",
                            name = "Animals",
                            isDefault = true,
                            createdByUserId = "host1",
                            createdAt = "2024-01-01T10:00:00Z"
                    ),
                    TopicDto(
                            id = "2",
                            name = "Countries",
                            isDefault = true,
                            createdByUserId = "host1",
                            createdAt = "2024-01-01T10:00:00Z"
                    ),
                    TopicDto(
                            id = "3",
                            name = "Movies",
                            isDefault = false,
                            createdByUserId = "host1",
                            createdAt = "2024-01-01T10:00:00Z"
                    )
            )

    val samplePlayers =
            listOf(
                    PlayerDto(
                            id = "host1",
                            connectionId = "conn1",
                            name = "Alice",
                            score = 0,
                            isConnected = true,
                            joinedAt = "2024-01-01T10:00:00Z",
                            isHost = true
                    ),
                    PlayerDto(
                            id = "player2",
                            connectionId = "conn2",
                            name = "Bob",
                            score = 0,
                            isConnected = true,
                            joinedAt = "2024-01-01T10:01:00Z",
                            isHost = false
                    ),
                    PlayerDto(
                            id = "player3",
                            connectionId = "conn3",
                            name = "Charlie",
                            score = 0,
                            isConnected = true,
                            joinedAt = "2024-01-01T10:02:00Z",
                            isHost = false
                    )
            )

    val sampleRoom =
            RoomDto(
                    id = "room1",
                    code = "ABC123",
                    hostUserId = "host1",
                    topics = sampleTopics,
                    players = samplePlayers,
                    state = 0, // Waiting
                    rounds = emptyList(),
                    createdAt = "2024-01-01T10:00:00Z",
                    expiresAt = null,
                    maxPlayers = 6,
                    roundDurationSeconds = 60,
                    votingDurationSeconds = 30,
                    maxRounds = 5,
                    currentRound = null,
                    hasPlayersSubmittedAnswers = false
            )

    val currentPlayer = samplePlayers.first() // Alice (host)

    MaterialTheme {
        LobbyScreen(
                room = sampleRoom,
                currentPlayer = currentPlayer,
                onStartRound = {},
                onLeaveRoom = {},
                isUpdatingSettings = false,
                updateError = null,
                onClearError = {}
        )
    }
}

@Preview(showBackground = true, name = "Non-Host Player View")
@Composable
fun LobbyScreenNonHostPreview() {
    val sampleTopics =
            listOf(
                    TopicDto(
                            id = "1",
                            name = "Food",
                            isDefault = true,
                            createdByUserId = "host1",
                            createdAt = "2024-01-01T10:00:00Z"
                    ),
                    TopicDto(
                            id = "2",
                            name = "Sports",
                            isDefault = false,
                            createdByUserId = "host1",
                            createdAt = "2024-01-01T10:00:00Z"
                    )
            )

    val samplePlayers =
            listOf(
                    PlayerDto(
                            id = "host1",
                            connectionId = "conn1",
                            name = "GameMaster",
                            score = 0,
                            isConnected = true,
                            joinedAt = "2024-01-01T10:00:00Z",
                            isHost = true
                    ),
                    PlayerDto(
                            id = "player2",
                            connectionId = "conn2",
                            name = "You",
                            score = 0,
                            isConnected = true,
                            joinedAt = "2024-01-01T10:01:00Z",
                            isHost = false
                    )
            )

    val sampleRoom =
            RoomDto(
                    id = "room1",
                    code = "XYZ789",
                    hostUserId = "host1",
                    topics = sampleTopics,
                    players = samplePlayers,
                    state = 0, // Waiting
                    rounds = emptyList(),
                    createdAt = "2024-01-01T10:00:00Z",
                    expiresAt = null,
                    maxPlayers = 4,
                    roundDurationSeconds = 90,
                    votingDurationSeconds = 45,
                    maxRounds = 3,
                    currentRound = null,
                    hasPlayersSubmittedAnswers = false
            )

    val currentPlayer = samplePlayers[1] // Non-host player

    MaterialTheme {
        LobbyScreen(
                room = sampleRoom,
                currentPlayer = currentPlayer,
                onStartRound = {},
                onLeaveRoom = {},
                isUpdatingSettings = false,
                updateError = null,
                onClearError = {}
        )
    }
}
