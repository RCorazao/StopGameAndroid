package com.reicode.stopgame.feature.results

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun ResultsScreen(
    room: RoomDto?,
    currentPlayer: PlayerDto?,
    onLeaveRoom: () -> Unit
) {
    GameScreenLayout(
        room = room,
        currentPlayer = currentPlayer,
        onLeaveRoom = onLeaveRoom,
        statusText = "Viewing results"
    ) {
        // Results-specific content
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Results Screen Content",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1F2937)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Results content will go here",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}