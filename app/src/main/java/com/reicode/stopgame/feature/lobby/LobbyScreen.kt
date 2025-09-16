package com.reicode.stopgame.feature.lobby

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reicode.stopgame.realtime.dto.PlayerDto
import com.reicode.stopgame.realtime.dto.RoomDto

@Composable
fun LobbyScreen(
    room: RoomDto?,
    currentPlayer: PlayerDto?,
    onEditSettings: () -> Unit = {},
    onStartRound: () -> Unit = {}
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF193CB9), Color(0xFF87CEFA))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Room Info ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Room: ${room?.code ?: "..."}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "${room?.players?.size ?: 0} / ${room?.maxPlayers ?: 0} players • Waiting for players",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        // --- Players ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Players",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                room?.players?.forEach { player ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = player.name +
                                    if (player.id == currentPlayer?.id) " (You)" else "",
                            fontWeight = if (player.id == currentPlayer?.id) FontWeight.Bold else FontWeight.Normal
                        )
                        Spacer(Modifier.weight(1f))
                        if (player.isHost) {
                            AssistChip(
                                onClick = {},
                                label = { Text("Host") },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = Color(0xFFFFD54F)
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
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Room Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    IconButton(onClick = onEditSettings) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Max Players: ${room?.maxPlayers ?: 0}")
                        Text("Round Duration: ${room?.roundDurationSeconds ?: 0}s")
                    }
                    Column {
                        Text("Max Rounds: ${room?.maxRounds ?: 0}")
                        Text("Voting Duration: ${room?.votingDurationSeconds ?: 0}s")
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text("Custom Topics:", fontWeight = FontWeight.SemiBold)

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(room?.topics ?: emptyList()) { topic ->
                        AssistChip(
                            onClick = {},
                            label = { Text(topic.name) }
                        )
                    }
                }
            }
        }

        // --- Start Button (Host only) ---
        if (currentPlayer?.isHost == true) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("You are the host. Start the first round when ready!")

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = onStartRound,
                        enabled = (room?.players?.size ?: 0) >= 2,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("Start Round 1")
                    }

                    if ((room?.players?.size ?: 0) < 2) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Need at least 2 players to start",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}
