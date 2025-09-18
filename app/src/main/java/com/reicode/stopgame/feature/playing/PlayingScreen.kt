package com.reicode.stopgame.feature.playing

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reicode.stopgame.realtime.dto.PlayerDto
import com.reicode.stopgame.realtime.dto.RoomDto
import com.reicode.stopgame.shared.GameScreenLayout
import kotlinx.coroutines.delay

@Composable
fun PlayingScreen(
    room: RoomDto?,
    currentPlayer: PlayerDto?,
    shouldSubmitAnswers: Boolean = false,
    onLeaveRoom: () -> Unit,
    onStopRound: () -> Unit = {},
    onSubmitAnswers: (Map<String, String>) -> Unit = {},
    onClearShouldSubmitAnswers: () -> Unit = {}
) {
    // State for user answers
    val answers = remember { mutableStateMapOf<String, String>() }
    
    // Timer state
    var timeRemaining by remember { mutableStateOf(room?.currentRound?.timeRemainingSeconds ?: room?.roundDurationSeconds ?: 60) }
    
    // Timer effect
    LaunchedEffect(room?.currentRound?.isActive) {
        if (room?.currentRound?.isActive == true) {
            while (timeRemaining > 0) {
                delay(1000)
                timeRemaining--
            }
            // Auto-stop for host when time reaches 0
            if (timeRemaining <= 0 && currentPlayer?.isHost == true) {
                onStopRound()
            }
        }
    }
    
    // Auto-submit answers when round is stopped
    LaunchedEffect(shouldSubmitAnswers) {
        if (shouldSubmitAnswers) {
            onSubmitAnswers(answers.toMap())
            onClearShouldSubmitAnswers()
        }
    }
    
    // Check if all topics are filled
    val allTopicsFilled = room?.topics?.all { topic ->
        answers[topic.id]?.isNotBlank() == true
    } ?: false
    
    val currentRound = room?.currentRound
    val currentLetter = currentRound?.letter ?: "?"
    val roundNumber = room?.rounds?.size ?: 1
    
    GameScreenLayout(
        room = room,
        currentPlayer = currentPlayer,
        onLeaveRoom = onLeaveRoom,
        statusText = "Round $roundNumber • Letter $currentLetter"
    ) {
        // Round Info and Timer Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Round $roundNumber: $currentLetter",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF1F2937)
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⏰",
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = formatTime(timeRemaining),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (timeRemaining <= 10) Color(0xFFEF4444) else Color(0xFF10B981)
                    )
                }
            }
        }
        
        // Topics Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                room?.topics?.forEach { topic ->
                    Column {
                        Text(
                            text = topic.name,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color(0xFF1F2937)
                        )
                        Spacer(Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = answers[topic.id] ?: "",
                            onValueChange = { newValue ->
                                answers[topic.id] = newValue
                            },
                            placeholder = { 
                                Text(
                                    text = "${currentLetter}...",
                                    color = Color(0xFF9CA3AF)
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF3B82F6),
                                unfocusedBorderColor = Color(0xFFD1D5DB)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        if (topic != room.topics.last()) {
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
        
        // Stop Round Button Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        onStopRound()
                    },
                    enabled = allTopicsFilled,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6B7280),
                        disabledContainerColor = Color(0xFFE5E7EB)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "⏹",
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Stop Round",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                
                if (!allTopicsFilled) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Fill in all topics to submit",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%d:%02d", minutes, remainingSeconds)
}