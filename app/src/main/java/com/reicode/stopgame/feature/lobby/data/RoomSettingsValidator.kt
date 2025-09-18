package com.reicode.stopgame.feature.lobby.data

import com.reicode.stopgame.realtime.dto.TopicDto

/**
 * Validation utilities for room settings.
 * Provides validation functions for all editable room configuration settings.
 */
object RoomSettingsValidator {
    
    // Validation constants
    private const val MIN_PLAYERS = 2
    private const val MAX_PLAYERS = 10
    private const val MIN_ROUNDS = 1
    private const val MAX_ROUNDS = 5
    private const val MIN_ROUND_DURATION = 30
    private const val MAX_ROUND_DURATION = 300
    private const val MIN_VOTING_DURATION = 15
    private const val MAX_VOTING_DURATION = 120
    private const val MAX_TOPIC_NAME_LENGTH = 40
    private const val MIN_TOPICS_REQUIRED = 1

    /**
     * Validates max players setting.
     * Requirements: 2.2 - validate between 2 and reasonable maximum (10)
     */
    fun validateMaxPlayers(value: Int): String? {
        return when {
            value < MIN_PLAYERS -> "Maximum players must be at least $MIN_PLAYERS"
            value > MAX_PLAYERS -> "Maximum players cannot exceed $MAX_PLAYERS"
            else -> null
        }
    }

    /**
     * Validates max rounds setting.
     * Requirements: 3.2 - validate between 1 and reasonable maximum (20)
     */
    fun validateMaxRounds(value: Int): String? {
        return when {
            value < MIN_ROUNDS -> "Maximum rounds must be at least $MIN_ROUNDS"
            value > MAX_ROUNDS -> "Maximum rounds cannot exceed $MAX_ROUNDS"
            else -> null
        }
    }

    /**
     * Validates round duration setting.
     * Requirements: 4.2 - validate between 30 and 300 seconds
     */
    fun validateRoundDuration(value: Int): String? {
        return when {
            value < MIN_ROUND_DURATION -> "Round duration must be at least $MIN_ROUND_DURATION seconds"
            value > MAX_ROUND_DURATION -> "Round duration cannot exceed $MAX_ROUND_DURATION seconds"
            else -> null
        }
    }

    /**
     * Validates voting duration setting.
     * Requirements: 5.2 - validate between 15 and 120 seconds
     */
    fun validateVotingDuration(value: Int): String? {
        return when {
            value < MIN_VOTING_DURATION -> "Voting duration must be at least $MIN_VOTING_DURATION seconds"
            value > MAX_VOTING_DURATION -> "Voting duration cannot exceed $MAX_VOTING_DURATION seconds"
            else -> null
        }
    }

    /**
     * Validates topic name for adding new topics.
     * Requirements: 6.4 - validate name is not empty and not duplicate, with character limit
     */
    fun validateTopicName(name: String, existingTopics: List<TopicDto>): String? {
        val trimmedName = name.trim()
        
        return when {
            trimmedName.isEmpty() -> "Topic name cannot be empty"
            trimmedName.length > MAX_TOPIC_NAME_LENGTH -> "Topic name cannot exceed $MAX_TOPIC_NAME_LENGTH characters"
            existingTopics.any { it.name.equals(trimmedName, ignoreCase = true) } -> "Topic name already exists"
            else -> null
        }
    }

    /**
     * Validates topic removal to ensure minimum count is maintained.
     * Requirements: 7.3 - prevent removal if it would result in less than minimum required topics
     */
    fun validateTopicRemoval(currentTopics: List<TopicDto>, topicToRemove: TopicDto): String? {
        return when {
            currentTopics.size <= MIN_TOPICS_REQUIRED -> "At least $MIN_TOPICS_REQUIRED topic is required"
            !currentTopics.contains(topicToRemove) -> "Topic not found in current topics list"
            else -> null
        }
    }

    /**
     * Validates the entire RoomSettings object.
     * Returns a map of field names to error messages for any validation failures.
     */
    fun validateRoomSettings(settings: RoomSettings): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        validateMaxPlayers(settings.maxPlayers)?.let { errors["maxPlayers"] = it }
        validateMaxRounds(settings.maxRounds)?.let { errors["maxRounds"] = it }
        validateRoundDuration(settings.roundDurationSeconds)?.let { errors["roundDuration"] = it }
        validateVotingDuration(settings.votingDurationSeconds)?.let { errors["votingDuration"] = it }
        
        if (settings.topics.size < MIN_TOPICS_REQUIRED) {
            errors["topics"] = "At least $MIN_TOPICS_REQUIRED topic is required"
        }

        return errors
    }

    /**
     * Checks if the room settings are valid (no validation errors).
     */
    fun isValidRoomSettings(settings: RoomSettings): Boolean {
        return validateRoomSettings(settings).isEmpty()
    }
}