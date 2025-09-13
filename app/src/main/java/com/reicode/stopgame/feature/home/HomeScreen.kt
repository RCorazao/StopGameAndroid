package com.reicode.stopgame.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onCreateRoom: (String) -> Unit,
    onJoinRoom: (String, String) -> Unit,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val showCreateDialog by viewModel.showCreateDialog.collectAsState()
    val showJoinDialog by viewModel.showJoinDialog.collectAsState()
    var playerName by remember { mutableStateOf("") }

    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1E3A8A),
            Color(0xFF3B82F6),
            Color.White
        ),
        start = Offset(0f, 0f),
        end = Offset.Infinite
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Title
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Stop Game",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Tutti Frutti Multiplayer",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Card for Ready to Play
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
                    text = "▶ Ready to Play?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Enter your name and create or join a room",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = playerName,
                    onValueChange = { playerName = it },
                    label = { Text("Your Name") },
                    placeholder = { Text("Enter your name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { viewModel.onCreateRoomClicked() },
                        enabled = playerName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("+ Create Room")
                    }

                    Button(
                        onClick = { viewModel.onJoinRoomClicked() },
                        enabled = playerName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                    ) {
                        Text("# Join Room")
                    }
                }
            }
        }

        // --- Dialogs ---
        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissDialogs() },
                title = { Text("Create New Room") },
                text = { Text("You'll be the host and can choose topics.\nRoom code will be generated automatically.") },
                confirmButton = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = {
                            onCreateRoom(playerName)
                        }) {
                            Text("Create Room")
                        }
                    }
                }
            )
        }

        if (showJoinDialog) {
            var roomCode by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { viewModel.dismissDialogs() },
                title = { Text("Join Room") },
                text = {
                    OutlinedTextField(
                        value = roomCode,
                        onValueChange = { roomCode = it },
                        label = { Text("Room Code") },
                        placeholder = { Text("Enter room code") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = {
                            onJoinRoom(playerName, roomCode)
                        }) {
                            Text("Join Room")
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // How to Play Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "How to Play",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("• Each round starts with a random letter")
                Text("• Fill words for each topic starting with that letter")
                Text("• Vote on other players’ answers")
                Text("• Score points for unique and valid answers")
            }
        }
    }
}
