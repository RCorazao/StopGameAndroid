package com.reicode.stopgame.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reicode.stopgame.realtime.dto.PlayerDto
import com.reicode.stopgame.realtime.dto.RoomDto

@Composable
fun RoomInfoCard(
    room: RoomDto?,
    statusText: String = "Waiting for players"
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
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
                text = "${room?.players?.size ?: 0} / ${room?.maxPlayers ?: 0} players • $statusText",
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
fun LeaveRoomCard(
    onLeaveRoom: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        OutlinedButton(
            onClick = onLeaveRoom,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
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

@Composable
fun GameScreenLayout(
    room: RoomDto?,
    currentPlayer: PlayerDto?,
    onLeaveRoom: () -> Unit,
    statusText: String,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RoomInfoCard(room = room, statusText = statusText)
            
            content()
            
            LeaveRoomCard(onLeaveRoom = onLeaveRoom)
        }
    }
}