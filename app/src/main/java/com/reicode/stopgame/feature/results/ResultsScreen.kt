package com.reicode.stopgame.feature.results

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun ResultsScreen(
        room: RoomDto?,
        currentPlayer: PlayerDto?,
        onStartRound: () -> Unit,
        onLeaveRoom: () -> Unit
) {
    val currentRoundNumber = room?.rounds?.size ?: 0
    val maxRounds = room?.maxRounds ?: 0
    val sortedPlayers = room?.players?.sortedByDescending { it.score } ?: emptyList()

    GameScreenLayout(
            room = room,
            currentPlayer = currentPlayer,
            onLeaveRoom = onLeaveRoom,
            statusText = stringResource(R.string.round_completed_status, currentRoundNumber, maxRounds)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Round Summary Card
            RoundSummaryCard(currentRound = currentRoundNumber, maxRounds = maxRounds)

            // Leaderboard Card
            LeaderboardCard(players = sortedPlayers, currentPlayer = currentPlayer)

            // Next Round Button (only for host and if not final round)
            if (currentPlayer?.isHost == true && currentRoundNumber < maxRounds) {
                NextRoundCard(nextRoundNumber = currentRoundNumber + 1, onStartRound = onStartRound)
            }
        }
    }
}

@Composable
private fun RoundSummaryCard(currentRound: Int, maxRounds: Int) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                    Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color(0xFFFFB800),
                    modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                    text = stringResource(R.string.round_results, currentRound),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF1F2937)
            )
            Text(
                    text = if (currentRound >= maxRounds) stringResource(R.string.final_results) else stringResource(R.string.round_completed),
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
private fun LeaderboardCard(players: List<PlayerDto>, currentPlayer: PlayerDto?) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                    text = stringResource(R.string.leaderboard),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            players.forEachIndexed { index, player ->
                PlayerRankItem(
                        player = player,
                        rank = index + 1,
                        isCurrentPlayer = player.id == currentPlayer?.id
                )
                if (index < players.size - 1) {
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun PlayerRankItem(player: PlayerDto, rank: Int, isCurrentPlayer: Boolean) {
    val backgroundColor =
            when {
                isCurrentPlayer -> Color(0xFFEBF8FF)
                rank == 1 -> Color(0xFFFFF7ED)
                else -> Color(0xFFF9FAFB)
            }

    val rankColor =
            when (rank) {
                1 -> Color(0xFFFFB800) // Gold
                2 -> Color(0xFF9CA3AF) // Silver
                3 -> Color(0xFFCD7F32) // Bronze
                else -> Color(0xFF6B7280) // Default
            }

    Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            elevation =
                    CardDefaults.cardElevation(
                            defaultElevation = if (isCurrentPlayer) 4.dp else 2.dp
                    )
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Rank badge
                Box(
                        modifier = Modifier.size(32.dp).clip(CircleShape).background(rankColor),
                        contentAlignment = Alignment.Center
                ) {
                    Text(
                            text = rank.toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                    )
                }

                // Player info
                Column {
                    Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                                text = player.name,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
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
                                    modifier =
                                            Modifier.background(
                                                            Color(0xFFD1FAE5),
                                                            RoundedCornerShape(4.dp)
                                                    )
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (rank <= 3) {
                        Text(
                                text =
                                        when (rank) {
                                            1 -> stringResource(R.string.first_place)
                                            2 -> stringResource(R.string.second_place)
                                            3 -> stringResource(R.string.third_place)
                                            else -> ""
                                        },
                                fontSize = 12.sp,
                                color = rankColor
                        )
                    }
                }
            }

            // Score
            Text(
                    text = stringResource(R.string.points_short, player.score),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1F2937)
            )
        }
    }
}

@Composable
private fun NextRoundCard(nextRoundNumber: Int, onStartRound: () -> Unit) {
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
                    text = stringResource(R.string.host_controls),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Button(
                    onClick = onStartRound,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                        text = stringResource(R.string.start_round_number, nextRoundNumber),
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                )
            }
        }
    }
}
