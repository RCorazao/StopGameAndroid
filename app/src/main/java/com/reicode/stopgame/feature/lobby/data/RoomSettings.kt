package com.reicode.stopgame.feature.lobby.data

import com.reicode.stopgame.realtime.dto.TopicDto

/**
 * Data class that encapsulates editable room configuration settings.
 * This is used for managing room settings in edit mode before applying changes.
 */
data class RoomSettings(
    val maxPlayers: Int,
    val maxRounds: Int,
    val roundDurationSeconds: Int,
    val votingDurationSeconds: Int,
    val topics: List<TopicDto>
) {
    companion object {
        /**
         * Creates RoomSettings from existing RoomDto
         */
        fun fromRoomDto(room: com.reicode.stopgame.realtime.dto.RoomDto): RoomSettings {
            return RoomSettings(
                maxPlayers = room.maxPlayers,
                maxRounds = room.maxRounds,
                roundDurationSeconds = room.roundDurationSeconds,
                votingDurationSeconds = room.votingDurationSeconds,
                topics = room.topics
            )
        }
    }
}