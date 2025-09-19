package com.reicode.stopgame.feature.voting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reicode.stopgame.realtime.dto.AnswerDto
import com.reicode.stopgame.realtime.dto.PlayerDto
import com.reicode.stopgame.realtime.dto.RoomDto
import com.reicode.stopgame.realtime.dto.VoteAnswerDto
import com.reicode.stopgame.shared.GameScreenLayout

@Composable
fun VotingScreen(
        room: RoomDto?,
        currentPlayer: PlayerDto?,
        voteAnswers: List<VoteAnswerDto>,
        onVote: (String, Boolean) -> Unit,
        onFinishVotingPhase: () -> Unit,
        onLeaveRoom: () -> Unit
) {
    GameScreenLayout(
            room = room,
            currentPlayer = currentPlayer,
            onLeaveRoom = onLeaveRoom,
            statusText = "Vote on answers"
    ) {
        if (voteAnswers.isEmpty()) {
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
                    CircularProgressIndicator(color = Color(0xFF3B82F6))
                    Spacer(Modifier.height(16.dp))
                    Text(
                            text = "Loading voting data...",
                            fontSize = 16.sp,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                voteAnswers.forEach { voteAnswer ->
                    TopicVotingCard(
                            voteAnswer = voteAnswer,
                            currentPlayer = currentPlayer,
                            onVote = onVote
                    )
                }

                // Finish voting phase button (only for host)
                if (currentPlayer?.isHost == true) {
                    FinishVotingPhaseCard(onFinishVotingPhase = onFinishVotingPhase)
                }
            }
        }
    }
}

@Composable
private fun TopicVotingCard(
        voteAnswer: VoteAnswerDto,
        currentPlayer: PlayerDto?,
        onVote: (String, Boolean) -> Unit
) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Topic header
            Text(
                    text = voteAnswer.topicName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            // Answers
            voteAnswer.answers.forEach { answer ->
                AnswerVotingItem(answer = answer, currentPlayer = currentPlayer, onVote = onVote)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun AnswerVotingItem(
        answer: AnswerDto,
        currentPlayer: PlayerDto?,
        onVote: (String, Boolean) -> Unit
) {
    val isOwnAnswer = currentPlayer?.id == answer.playerId
    val currentPlayerVote = answer.votes.find { it.voterId == currentPlayer?.id }
    val validVotes = answer.votes.count { it.isValid }
    val invalidVotes = answer.votes.count { !it.isValid }

    Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors =
                    CardDefaults.cardColors(
                            containerColor = if (isOwnAnswer) Color(0xFFF3F4F6) else Color.White
                    ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Answer content
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                            text = answer.value,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color(0xFF1F2937)
                    )
                    Text(
                            text = "by ${answer.playerName}",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                    )
                }

                if (isOwnAnswer) {
                    Text(
                            text = "Your answer",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280),
                            fontWeight = FontWeight.Medium
                    )
                }
            }

            if (!isOwnAnswer) {
                Spacer(Modifier.height(12.dp))

                // Voting buttons and counts
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    // Vote counts
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                    Icons.Default.ThumbUp,
                                    contentDescription = "Valid votes",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(16.dp)
                            )
                            Text(
                                    text = validVotes.toString(),
                                    fontSize = 14.sp,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Medium
                            )
                        }

                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                    Icons.Default.ThumbDown,
                                    contentDescription = "Invalid votes",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(16.dp)
                            )
                            Text(
                                    text = invalidVotes.toString(),
                                    fontSize = 14.sp,
                                    color = Color(0xFFEF4444),
                                    fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Voting buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                                onClick = { onVote(answer.id, true) },
                                modifier =
                                        Modifier.size(40.dp)
                                                .background(
                                                        color =
                                                                if (currentPlayerVote?.isValid ==
                                                                                true
                                                                )
                                                                        Color(0xFF10B981)
                                                                else Color(0xFFF3F4F6),
                                                        shape = RoundedCornerShape(8.dp)
                                                )
                        ) {
                            Icon(
                                    Icons.Default.ThumbUp,
                                    contentDescription = "Vote valid",
                                    tint =
                                            if (currentPlayerVote?.isValid == true) Color.White
                                            else Color(0xFF6B7280),
                                    modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                                onClick = { onVote(answer.id, false) },
                                modifier =
                                        Modifier.size(40.dp)
                                                .background(
                                                        color =
                                                                if (currentPlayerVote?.isValid ==
                                                                                false
                                                                )
                                                                        Color(0xFFEF4444)
                                                                else Color(0xFFF3F4F6),
                                                        shape = RoundedCornerShape(8.dp)
                                                )
                        ) {
                            Icon(
                                    Icons.Default.ThumbDown,
                                    contentDescription = "Vote invalid",
                                    tint =
                                            if (currentPlayerVote?.isValid == false) Color.White
                                            else Color(0xFF6B7280),
                                    modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            } else {
                // Show vote counts for own answer
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                                Icons.Default.ThumbUp,
                                contentDescription = "Valid votes",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                        )
                        Text(
                                text = "$validVotes valid",
                                fontSize = 14.sp,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Medium
                        )
                    }

                    Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                                Icons.Default.ThumbDown,
                                contentDescription = "Invalid votes",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(16.dp)
                        )
                        Text(
                                text = "$invalidVotes invalid",
                                fontSize = 14.sp,
                                color = Color(0xFFEF4444),
                                fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FinishVotingPhaseCard(onFinishVotingPhase: () -> Unit) {
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
                    text = "Host Controls",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Button(
                    onClick = onFinishVotingPhase,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                    shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(text = "Finish Voting Phase", fontWeight = FontWeight.Medium, fontSize = 16.sp)
            }
        }
    }
}
