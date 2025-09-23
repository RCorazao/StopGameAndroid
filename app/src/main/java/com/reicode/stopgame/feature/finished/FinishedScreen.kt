package com.reicode.stopgame.feature.finished

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reicode.stopgame.R
import com.reicode.stopgame.realtime.dto.PlayerDto
import com.reicode.stopgame.realtime.dto.RoomDto
import com.reicode.stopgame.shared.GameScreenLayout

@Composable
fun FinishedScreen(
    room: RoomDto?,
    currentPlayer: PlayerDto?,
    onLeaveRoom: () -> Unit
) {
    val sortedPlayers = room?.players?.sortedByDescending { it.score } ?: emptyList()
    val winner = sortedPlayers.firstOrNull()
    val totalRounds = room?.rounds?.size ?: 0
    
    GameScreenLayout(
        room = room,
        currentPlayer = currentPlayer,
        onLeaveRoom = onLeaveRoom,
        statusText = stringResource(R.string.game_completed_status, totalRounds)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Winner Celebration Card
            if (winner != null) {
                WinnerCelebrationCard(
                    winner = winner,
                    isCurrentPlayer = winner.id == currentPlayer?.id
                )
            }
            
            // Final Leaderboard Card
            FinalLeaderboardCard(
                players = sortedPlayers,
                currentPlayer = currentPlayer
            )
            
            // Game Summary Card
            GameSummaryCard(
                totalRounds = totalRounds,
                totalPlayers = sortedPlayers.size
            )
        }
    }
}

@Composable
private fun WinnerCelebrationCard(
    winner: PlayerDto,
    isCurrentPlayer: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFF7ED),
                            Color(0xFFFFEDD5)
                        )
                    )
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Trophy and stars
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(24.dp)
                    )
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(48.dp)
                    )
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Text(
                    text = stringResource(R.string.winner_announcement),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color(0xFFD97706)
                )
                
                Text(
                    text = winner.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = Color(0xFF1F2937)
                )
                
                if (isCurrentPlayer) {
                    Text(
                        text = stringResource(R.string.congratulations_winner),
                        fontSize = 16.sp,
                        color = Color(0xFF059669),
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        text = stringResource(R.string.congratulations_other),
                        fontSize = 16.sp,
                        color = Color(0xFF6B7280),
                        textAlign = TextAlign.Center
                    )
                }
                
                // Winner's final score
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFB800)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.points_caps, winner.score),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FinalLeaderboardCard(
    players: List<PlayerDto>,
    currentPlayer: PlayerDto?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.final_standings),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF1F2937),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Spacer(Modifier.height(16.dp))
            
            players.forEachIndexed { index, player ->
                FinalPlayerRankItem(
                    player = player,
                    rank = index + 1,
                    isCurrentPlayer = player.id == currentPlayer?.id,
                    isWinner = index == 0
                )
                if (index < players.size - 1) {
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun FinalPlayerRankItem(
    player: PlayerDto,
    rank: Int,
    isCurrentPlayer: Boolean,
    isWinner: Boolean
) {
    val backgroundColor = when {
        isWinner -> Color(0xFFFFF7ED) // Gold background for winner
        isCurrentPlayer -> Color(0xFFEBF8FF)
        rank == 2 -> Color(0xFFF3F4F6) // Silver
        rank == 3 -> Color(0xFFFDF2F8) // Bronze
        else -> Color(0xFFF9FAFB)
    }
    
    val rankColor = when (rank) {
        1 -> Color(0xFFFFB800) // Gold
        2 -> Color(0xFF9CA3AF) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> Color(0xFF6B7280) // Default
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isWinner) 6.dp else if (isCurrentPlayer) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Rank badge with special winner styling
                Box(
                    modifier = Modifier
                        .size(if (isWinner) 40.dp else 32.dp)
                        .clip(CircleShape)
                        .background(
                            if (isWinner) 
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFFFFD700), Color(0xFFFFB800))
                                )
                            else 
                                Brush.radialGradient(
                                    colors = listOf(rankColor, rankColor.copy(alpha = 0.8f))
                                )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isWinner) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = rank.toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isWinner) 16.sp else 14.sp
                        )
                    }
                }
                
                // Player info
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = player.name,
                            fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Medium,
                            fontSize = if (isWinner) 18.sp else 16.sp,
                            color = Color(0xFF1F2937)
                        )
                        if (isCurrentPlayer) {
                            Text(
                                text = stringResource(R.string.you_parentheses),
                                fontSize = 12.sp,
                                color = Color(0xFF3B82F6),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        if (player.isHost) {
                            Text(
                                text = stringResource(R.string.host_label),
                                fontSize = 10.sp,
                                color = Color(0xFF059669),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(
                                        Color(0xFFD1FAE5),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    
                    Text(
                        text = when (rank) {
                            1 -> stringResource(R.string.champion)
                            2 -> stringResource(R.string.runner_up)
                            3 -> stringResource(R.string.third_place_final)
                            else -> stringResource(R.string.great_game)
                        },
                        fontSize = 12.sp,
                        color = rankColor,
                        fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
            
            // Score with winner emphasis
            Text(
                text = stringResource(R.string.points_short, player.score),
                fontWeight = if (isWinner) FontWeight.ExtraBold else FontWeight.Bold,
                fontSize = if (isWinner) 20.sp else 18.sp,
                color = if (isWinner) Color(0xFFD97706) else Color(0xFF1F2937)
            )
        }
    }
}

@Composable
private fun GameSummaryCard(
    totalRounds: Int,
    totalPlayers: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.game_summary),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1F2937),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Spacer(Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = totalRounds.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color(0xFF3B82F6)
                    )
                    Text(
                        text = stringResource(R.string.rounds_label),
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = totalPlayers.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color(0xFF10B981)
                    )
                    Text(
                        text = stringResource(R.string.players_label),
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            Text(
                text = stringResource(R.string.thanks_for_playing),
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )
        }
    }
}