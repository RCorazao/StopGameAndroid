package com.reicode.stopgame.feature.lobby

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reicode.stopgame.realtime.dto.PlayerDto
import com.reicode.stopgame.realtime.dto.RoomDto
import com.reicode.stopgame.realtime.dto.TopicDto

@Composable
fun LobbyScreen(
        room: RoomDto?,
        currentPlayer: PlayerDto?,
        onEditSettings: () -> Unit = {},
        onStartRound: () -> Unit = {},
        onLeaveRoom: () -> Unit = {}
) {

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Room Info ---
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Room: ${room?.code ?: "..."}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF1F2937)
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text =
                            "${room?.players?.size ?: 0} / ${room?.maxPlayers ?: 0} players • Waiting for players",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            // --- Players ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors =
                    CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Players",
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
                                Text(text = "You", fontSize = 14.sp, color = Color(0xFF6B7280))
                            }
                            Spacer(Modifier.weight(1f))
                            if (player.isHost) {
                                AssistChip(
                                    onClick = {},
                                    label = {
                                        Text(
                                            "Host",
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
                colors =
                    CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
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
                                "Room Settings",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF1F2937)
                            )
                        }
                        TextButton(onClick = onEditSettings) {
                            Text("Edit", color = Color(0xFF3B82F6), fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Max Players",
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
                                "Round Duration",
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
                                "Max Rounds",
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
                                "Voting Duration",
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
                        "Topics",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1F2937)
                    )
                    Spacer(Modifier.height(12.dp))

                    // Topics in a flow layout
                    val topics = room?.topics ?: emptyList()
                    val chunkedTopics = topics.chunked(4) // 4 topics per row

                    chunkedTopics.forEach { rowTopics ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowTopics.forEach { topic ->
                                AssistChip(
                                    onClick = {},
                                    label = {
                                        Text(
                                            topic.name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    },
                                    colors =
                                        AssistChipDefaults.assistChipColors(
                                            containerColor = Color(0xFFF3F4F6),
                                            labelColor = Color(0xFF374151)
                                        ),
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                            }
                            // Fill remaining space if less than 4 topics in row
                            repeat(4 - rowTopics.size) { Spacer(modifier = Modifier.weight(1f)) }
                        }
                        if (rowTopics != chunkedTopics.last()) {
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }

            // --- Start Button (Host only) ---
            if (currentPlayer?.isHost == true) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.9f)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "You are the host. Start the first round when ready!",
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
                                ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF10B981)
                                ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                "Start Round 1",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        if ((room?.players?.size ?: 0) < 2) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Need at least 2 players to start",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // --- Leave Room Button ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors =
                    CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                OutlinedButton(
                    onClick = onLeaveRoom,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF6B7280)
                        ),
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Leave Room", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                }
            }
        }
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
                onEditSettings = {},
                onStartRound = {},
                onLeaveRoom = {}
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
                onEditSettings = {},
                onStartRound = {},
                onLeaveRoom = {}
        )
    }
}
