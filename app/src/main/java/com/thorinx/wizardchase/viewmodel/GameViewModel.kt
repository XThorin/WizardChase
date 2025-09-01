package com.thorinx.wizardchase.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thorinx.wizardchase.data.GameState
import com.thorinx.wizardchase.data.Language
import com.thorinx.wizardchase.data.Languages
import com.thorinx.wizardchase.data.Player
import com.thorinx.wizardchase.data.PowerUpType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random
import com.thorinx.wizardchase.audio.AudioManager

class GameViewModel : ViewModel() {
    
    private val _player = MutableStateFlow(Player(""))
    val player: StateFlow<Player> = _player.asStateFlow()
    
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
    
    private val _currentScreen = MutableStateFlow(GameScreen.WELCOME)
    val currentScreen: StateFlow<GameScreen> = _currentScreen.asStateFlow()
    
    private val _currentLanguage = MutableStateFlow(Languages.TURKISH)
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()
    
    private val _isMusicEnabled = MutableStateFlow(true)
    val isMusicEnabled: StateFlow<Boolean> = _isMusicEnabled.asStateFlow()
    
    private val _isSoundEnabled = MutableStateFlow(true)
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()
    
    private var gameTimer: kotlinx.coroutines.Job? = null
    private var wizardMovementTimer: kotlinx.coroutines.Job? = null
    private var powerUpTimer: kotlinx.coroutines.Job? = null
    
    private var audioManager: AudioManager? = null
    
    fun setPlayerName(name: String) {
        _player.value = _player.value.copy(name = name)
    }
    
    fun setAudioManager(audioManager: AudioManager) {
        this.audioManager = audioManager
    }
    
    fun setMusicEnabled(enabled: Boolean) {
        _isMusicEnabled.value = enabled
        audioManager?.setMusicEnabled(enabled)
    }
    
    fun setSoundEnabled(enabled: Boolean) {
        _isSoundEnabled.value = enabled
        audioManager?.setSoundEnabled(enabled)
    }
    
    fun startGame() {
        _currentScreen.value = GameScreen.GAME
        _gameState.value = GameState(
            isPlaying = true,
            score = 0,
            timeRemaining = 60,
            wizardPosition = Offset(100f, 100f)
        )
        
        // Start background music
        audioManager?.playBackgroundMusic()
        
        startGameTimer()
        startWizardMovement()
        startPowerUpSpawn()
    }
    
    fun catchWizard() {
        val currentState = _gameState.value
        val newScore = currentState.score + 10
        
        _gameState.value = currentState.copy(
            score = newScore
        )
        
        // Play wizard caught sound
        audioManager?.playWizardCaughtSound()
        
        // Update player's high score
        if (newScore > _player.value.highScore) {
            _player.value = _player.value.copy(highScore = newScore)
        }
        
        moveWizardToRandomPosition()
    }
    
    fun catchPowerUp() {
        val currentState = _gameState.value
        val powerUpType = currentState.powerUpType
        
        // Play power-up sound
        audioManager?.playPowerUpSound()
        
        when (powerUpType) {
            PowerUpType.SCORE_MULTIPLIER -> {
                _gameState.value = currentState.copy(
                    score = currentState.score + 50,
                    powerUpPosition = null,
                    powerUpType = null
                )
            }
            PowerUpType.TIME_BONUS -> {
                _gameState.value = currentState.copy(
                    timeRemaining = currentState.timeRemaining + 10,
                    powerUpPosition = null,
                    powerUpType = null
                )
            }
            PowerUpType.SLOW_MOTION -> {
                // Slow down wizard for 5 seconds
                viewModelScope.launch {
                    _gameState.value = currentState.copy(
                        powerUpPosition = null,
                        powerUpType = null
                    )
                    delay(5000)
                }
            }
            null -> {}
        }
    }
    
    fun endGame() {
        gameTimer?.cancel()
        wizardMovementTimer?.cancel()
        powerUpTimer?.cancel()
        
        // Stop background music and play game over sound
        audioManager?.stopBackgroundMusic()
        audioManager?.playGameOverSound()
        
        _gameState.value = _gameState.value.copy(isPlaying = false)
        _currentScreen.value = GameScreen.RESULTS
    }
    
    fun restartGame() {
        // Cancel all timers first
        gameTimer?.cancel()
        wizardMovementTimer?.cancel()
        powerUpTimer?.cancel()
        
        // Reset game state
        _gameState.value = GameState()
        
        // Keep the player name but reset other player data
        val currentPlayerName = _player.value.name
        _player.value = Player(currentPlayerName)
        
        // Navigate back to welcome screen
        _currentScreen.value = GameScreen.WELCOME
    }
    
    fun setLanguage(language: Language) {
        try {
            // Use viewModelScope to ensure proper thread safety
            viewModelScope.launch {
                _currentLanguage.value = language
            }
        } catch (e: Exception) {
            // Log error but don't crash
            e.printStackTrace()
            // Fallback: try direct assignment
            try {
                _currentLanguage.value = language
            } catch (fallbackException: Exception) {
                fallbackException.printStackTrace()
            }
        }
    }
    
    fun showSettings() {
        _currentScreen.value = GameScreen.SETTINGS
    }
    
    fun backFromSettings() {
        try {
            // Use viewModelScope to ensure proper thread safety
            viewModelScope.launch {
                _currentScreen.value = GameScreen.WELCOME
            }
        } catch (e: Exception) {
            // Log error but don't crash
            e.printStackTrace()
            // Fallback: try direct assignment
            try {
                _currentScreen.value = GameScreen.WELCOME
            } catch (fallbackException: Exception) {
                fallbackException.printStackTrace()
            }
        }
    }
    
    fun pauseGame() {
        _gameState.value = _gameState.value.copy(isPaused = true)
        audioManager?.pauseBackgroundMusic()
    }
    
    fun resumeGame() {
        _gameState.value = _gameState.value.copy(isPaused = false)
        audioManager?.resumeBackgroundMusic()
    }
    
    private fun startGameTimer() {
        gameTimer = viewModelScope.launch {
            while (_gameState.value.timeRemaining > 0 && _gameState.value.isPlaying) {
                delay(1000)
                // Oyun duraklatıldığında süreyi azaltma
                if (!_gameState.value.isPaused) {
                    _gameState.value = _gameState.value.copy(
                        timeRemaining = _gameState.value.timeRemaining - 1
                    )
                }
            }
            if (_gameState.value.timeRemaining <= 0) {
                endGame()
            }
        }
    }
    
    private fun startWizardMovement() {
        wizardMovementTimer = viewModelScope.launch {
            while (_gameState.value.isPlaying) {
                delay(1000) // Fixed 1 second delay
                // Oyun duraklatıldığında wizard hareket etmesin
                if (!_gameState.value.isPaused) {
                    moveWizardToRandomPosition()
                }
            }
        }
    }
    
    private fun startPowerUpSpawn() {
        powerUpTimer = viewModelScope.launch {
            while (_gameState.value.isPlaying) {
                delay(Random.nextLong(5000, 15000)) // Random spawn between 5-15 seconds
                // Oyun duraklatıldığında power-up spawn olmasın
                if (_gameState.value.isPlaying && !_gameState.value.isPaused) {
                    spawnPowerUp()
                }
            }
        }
    }
    
    private fun moveWizardToRandomPosition() {
        // Adjust position based on typical screen dimensions
        // Limit wizard to stay above the navigation bar area
        val maxX = 350f
        val maxY = 600f  // Reduced from 800f to keep wizard above navigation
        val minX = 50f
        val minY = 150f
        
        val newX = Random.nextFloat() * (maxX - minX) + minX
        val newY = Random.nextFloat() * (maxY - minY) + minY
        _gameState.value = _gameState.value.copy(
            wizardPosition = Offset(newX, newY)
        )
    }
    
    private fun spawnPowerUp() {
        val powerUpTypes = PowerUpType.values()
        val randomType = powerUpTypes[Random.nextInt(powerUpTypes.size)]
        
        // Adjust position based on typical screen dimensions - same as wizard
        val maxX = 350f
        val maxY = 600f  // Reduced from 800f to keep power-ups in visible area like wizard
        val minX = 50f
        val minY = 150f
        
        val newX = Random.nextFloat() * (maxX - minX) + minX
        val newY = Random.nextFloat() * (maxY - minY) + minY
        
        _gameState.value = _gameState.value.copy(
            powerUpPosition = Offset(newX, newY),
            powerUpType = randomType
        )
        
        // Remove power-up after 3 seconds
        viewModelScope.launch {
            delay(3000)
            if (_gameState.value.powerUpType == randomType) {
                _gameState.value = _gameState.value.copy(
                    powerUpPosition = null,
                    powerUpType = null
                )
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        gameTimer?.cancel()
        wizardMovementTimer?.cancel()
        powerUpTimer?.cancel()
        audioManager?.release()
    }
}

enum class GameScreen {
    WELCOME,
    GAME,
    RESULTS,
    SETTINGS
}
