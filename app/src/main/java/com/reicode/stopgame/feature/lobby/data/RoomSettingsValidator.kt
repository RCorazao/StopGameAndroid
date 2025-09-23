package com.reicode.stopgame.feature.lobby.data

import android.content.Context
import com.reicode.stopgame.R
import com.reicode.stopgame.realtime.dto.TopicDto

/**
 * Validation utilities for room settings. Provides validation functions for all editable room
 * configuration settings.
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
     * Validates max players setting. Requirements: 2.2 - validate between 2 and reasonable maximum
     * (10)
     */
    fun validateMaxPlayers(value: Int, context: Context): String? {
        return when {
            value < MIN_PLAYERS -> context.getString(R.string.max_players_min, MIN_PLAYERS)
            value > MAX_PLAYERS -> context.getString(R.string.max_players_max, MAX_PLAYERS)
            else -> null
        }
    }

    /**
     * Validates max rounds setting. Requirements: 3.2 - validate between 1 and reasonable maximum
     * (20)
     */
    fun validateMaxRounds(value: Int, context: Context): String? {
        return when {
            value < MIN_ROUNDS -> context.getString(R.string.max_rounds_min, MIN_ROUNDS)
            value > MAX_ROUNDS -> context.getString(R.string.max_rounds_max, MAX_ROUNDS)
            else -> null
        }
    }

    /** Validates round duration setting. Requirements: 4.2 - validate between 30 and 300 seconds */
    fun validateRoundDuration(value: Int, context: Context): String? {
        return when {
            value < MIN_ROUND_DURATION ->
                    context.getString(R.string.round_duration_min, MIN_ROUND_DURATION)
            value > MAX_ROUND_DURATION ->
                    context.getString(R.string.round_duration_max, MAX_ROUND_DURATION)
            else -> null
        }
    }

    /**
     * Validates voting duration setting. Requirements: 5.2 - validate between 15 and 120 seconds
     */
    fun validateVotingDuration(value: Int, context: Context): String? {
        return when {
            value < MIN_VOTING_DURATION ->
                    context.getString(R.string.voting_duration_min, MIN_VOTING_DURATION)
            value > MAX_VOTING_DURATION ->
                    context.getString(R.string.voting_duration_max, MAX_VOTING_DURATION)
            else -> null
        }
    }

    /**
     * Validates topic name for adding new topics. Requirements: 6.4 - validate name is not empty
     * and not duplicate, with character limit
     */
    fun validateTopicName(name: String, existingTopics: List<TopicDto>, context: Context): String? {
        val trimmedName = name.trim()

        return when {
            trimmedName.isEmpty() -> context.getString(R.string.topic_name_empty)
            trimmedName.length > MAX_TOPIC_NAME_LENGTH ->
                    context.getString(R.string.topic_name_too_long, MAX_TOPIC_NAME_LENGTH)
            existingTopics.any { it.name.equals(trimmedName, ignoreCase = true) } ->
                    context.getString(R.string.topic_name_exists)
            else -> null
        }
    }

    /**
     * Validates topic removal to ensure minimum count is maintained. Requirements: 7.3 - prevent
     * removal if it would result in less than minimum required topics
     */
    fun validateTopicRemoval(
            currentTopics: List<TopicDto>,
            topicToRemove: TopicDto,
            context: Context
    ): String? {
        return when {
            currentTopics.size <= MIN_TOPICS_REQUIRED ->
                    context.getString(R.string.min_topics_required, MIN_TOPICS_REQUIRED)
            !currentTopics.contains(topicToRemove) -> context.getString(R.string.topic_not_found)
            else -> null
        }
    }

    /**
     * Validates the entire RoomSettings object. Returns a map of field names to error messages for
     * any validation failures.
     */
    fun validateRoomSettings(settings: RoomSettings, context: Context): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        validateMaxPlayers(settings.maxPlayers, context)?.let { errors["maxPlayers"] = it }
        validateMaxRounds(settings.maxRounds, context)?.let { errors["maxRounds"] = it }
        validateRoundDuration(settings.roundDurationSeconds, context)?.let {
            errors["roundDuration"] = it
        }
        validateVotingDuration(settings.votingDurationSeconds, context)?.let {
            errors["votingDuration"] = it
        }

        if (settings.topics.size < MIN_TOPICS_REQUIRED) {
            errors["topics"] = context.getString(R.string.min_topics_required, MIN_TOPICS_REQUIRED)
        }

        return errors
    }

    /** Checks if the room settings are valid (no validation errors). */
    fun isValidRoomSettings(settings: RoomSettings, context: Context): Boolean {
        return validateRoomSettings(settings, context).isEmpty()
    }
}
