package com.thorinx.wizardchase.data

import androidx.compose.ui.geometry.Offset

data class Player(
    val name: String,
    val score: Int = 0,
    val highScore: Int = 0
)

data class GameState(
    val isPlaying: Boolean = false,
    val isPaused: Boolean = false,
    val score: Int = 0,
    val timeRemaining: Int = 60,
    val wizardPosition: Offset = Offset(0f, 0f),
    val powerUpPosition: Offset? = null,
    val powerUpType: PowerUpType? = null
)

enum class PowerUpType {
    SCORE_MULTIPLIER,
    TIME_BONUS,
    SLOW_MOTION
}

data class GameResult(
    val playerName: String,
    val finalScore: Int,
    val timePlayed: Int
)
