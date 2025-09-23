package com.reicode.stopgame.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reicode.stopgame.R

@Composable
fun HomeScreen(
        onCreateRoom: (String) -> Unit,
        onJoinRoom: (String, String) -> Unit,
        viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val showCreateDialog by viewModel.showCreateDialog.collectAsState()
    val showJoinDialog by viewModel.showJoinDialog.collectAsState()
    var playerName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.dismissDialogs() }

    Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Title
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                    text = stringResource(R.string.home_title),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
            )
            Text(
                    text = stringResource(R.string.home_subtitle),
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
                        text = stringResource(R.string.ready_to_play),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                )
                Text(
                        text = stringResource(R.string.enter_name_instruction),
                        fontSize = 14.sp,
                        color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                        value = playerName,
                        onValueChange = { playerName = it },
                        label = { Text(stringResource(R.string.player_name)) },
                        placeholder = { Text(stringResource(R.string.player_name)) },
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
                    ) { Text("+ ${stringResource(R.string.home_create_room)}") }

                    Button(
                            onClick = { viewModel.onJoinRoomClicked() },
                            enabled = playerName.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                    ) { Text("# ${stringResource(R.string.home_join_room)}") }
                }
            }
        }

        // --- Dialogs ---
        if (showCreateDialog) {
            AlertDialog(
                    onDismissRequest = { viewModel.dismissDialogs() },
                    title = { Text(stringResource(R.string.create_room_title)) },
                    text = { Text(stringResource(R.string.create_room_description)) },
                    confirmButton = {
                        Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                        ) {
                            Button(onClick = { onCreateRoom(playerName) }) {
                                Text(stringResource(R.string.create))
                            }
                        }
                    }
            )
        }

        if (showJoinDialog) {
            var roomCode by remember { mutableStateOf("") }

            AlertDialog(
                    onDismissRequest = { viewModel.dismissDialogs() },
                    title = { Text(stringResource(R.string.join_room_title)) },
                    text = {
                        OutlinedTextField(
                                value = roomCode,
                                onValueChange = { roomCode = it },
                                label = { Text(stringResource(R.string.room_code)) },
                                placeholder = { Text(stringResource(R.string.room_code)) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                        ) {
                            Button(onClick = { onJoinRoom(roomCode, playerName) }) {
                                Text(stringResource(R.string.join))
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
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                        text = stringResource(R.string.how_to_play),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(stringResource(R.string.rule_1))
                Text(stringResource(R.string.rule_2))
                Text(stringResource(R.string.rule_3))
                Text(stringResource(R.string.rule_4))
            }
        }
    }
}
