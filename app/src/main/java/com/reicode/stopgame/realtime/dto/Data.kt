package com.reicode.stopgame.realtime.dto

data class CreateRoomRequest(
    val hostName: String
)

data class JoinRoomRequest(
    val roomCode: String,
    val playerName: String
)

data class UpdateRoomSettingsRequest(
    val maxPlayers: Int,
    val maxRounds: Int,
    val roundDurationSeconds: Int,
    val votingDurationSeconds: Int,
    val topics: List<String>
)

data class RoomDto(
    val id: String,
    val code: String,
    val hostUserId: String,
    val topics: List<TopicDto>,
    val players: List<PlayerDto>,
    val state: Int, // can map to RoomState enum later
    val rounds: List<RoundDto>,
    val createdAt: String,
    val expiresAt: String?,
    val maxPlayers: Int,
    val roundDurationSeconds: Int,
    val votingDurationSeconds: Int,
    val maxRounds: Int,
    val currentRound: RoundDto?,
    val hasPlayersSubmittedAnswers: Boolean
)

enum class RoomState(val value: Int) {
    Waiting(0),
    Playing(1),
    Voting(2),
    Results(3),
    Finished(4);

    companion object {
        fun fromValue(value: Int) = values().firstOrNull { it.value == value } ?: Waiting
    }
}

data class PlayerDto(
    val id: String,
    val connectionId: String,
    val name: String,
    val score: Int,
    val isConnected: Boolean,
    val joinedAt: String,
    val isHost: Boolean
)

data class TopicDto(
    val id: String,
    val name: String,
    val isDefault: Boolean,
    val createdByUserId: String,
    val createdAt: String
)

data class RoundDto(
    val id: String,
    val letter: String,
    val startedAt: String,
    val endedAt: String?,
    val answers: List<AnswerDto>,
    val isActive: Boolean,
    val timeRemainingSeconds: Int
)

data class AnswerDto(
    val id: String,
    val topicId: String,
    val playerId: String,
    val playerName: String,
    val topicName: String,
    val value: String,
    val createdAt: String,
    val votes: List<VoteDto>
)

data class VoteDto(
    val voterId: String,
    val voterName: String,
    val answerOwnerId: String,
    val answerOwnerName: String,
    val topicId: String,
    val topicName: String,
    val isValid: Boolean,
    val createdAt: String
)
