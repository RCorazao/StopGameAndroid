package com.reicode.stopgame.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.reicode.stopgame.R

/**
 * Utility functions for localization and string formatting
 */
object LocalizationUtils {
    
    /**
     * Get localized status text based on game state
     */
    @Composable
    fun getStatusText(
        isWaiting: Boolean = false,
        isPlaying: Boolean = false,
        isVoting: Boolean = false,
        isResults: Boolean = false,
        isFinished: Boolean = false,
        roundNumber: Int = 0,
        letter: String = ""
    ): String {
        return when {
            isWaiting -> stringResource(R.string.status_waiting_players)
            isPlaying -> stringResource(R.string.status_round_letter, roundNumber, letter)
            isVoting -> stringResource(R.string.status_voting)
            isResults -> stringResource(R.string.status_results)
            isFinished -> stringResource(R.string.status_finished)
            else -> stringResource(R.string.status_waiting_players)
        }
    }
    
    /**
     * Format score text
     */
    @Composable
    fun formatScore(score: Int): String {
        return stringResource(R.string.score, score)
    }
    
    /**
     * Format points text
     */
    @Composable
    fun formatPoints(points: Int): String {
        return stringResource(R.string.points, points)
    }
}