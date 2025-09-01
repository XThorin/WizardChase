package com.thorinx.wizardchase.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.AudioAttributes
import com.thorinx.wizardchase.R
import android.media.AudioManager as AndroidAudioManager

class AudioManager(private val context: Context) {
    
    private var backgroundMusicPlayer: MediaPlayer? = null
    private var soundEffectPlayer: MediaPlayer? = null
    
    private var isMusicEnabled = true
    private var isSoundEnabled = true
    private var musicVolume = 0.7f
    private var soundVolume = 1.0f
    
    fun playBackgroundMusic() {
        if (!isMusicEnabled) return
        
        try {
            // Background music dosyasını raw klasöründen yükle
            backgroundMusicPlayer = MediaPlayer.create(context, R.raw.background_music)
            backgroundMusicPlayer?.apply {
                isLooping = true // Müzik sürekli tekrar etsin
                setVolume(musicVolume, musicVolume)
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun stopBackgroundMusic() {
        backgroundMusicPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        backgroundMusicPlayer = null
    }
    
    fun pauseBackgroundMusic() {
        backgroundMusicPlayer?.pause()
    }
    
    fun resumeBackgroundMusic() {
        if (isMusicEnabled && backgroundMusicPlayer != null) {
            backgroundMusicPlayer?.start()
        }
    }
    
    fun playSoundEffect(soundResId: Int) {
        if (!isSoundEnabled) return
        
        try {
            soundEffectPlayer = MediaPlayer.create(context, soundResId)
            soundEffectPlayer?.apply {
                setVolume(soundVolume, soundVolume)
                setOnCompletionListener { release() }
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun playWizardCaughtSound() {
        playSoundEffect(R.raw.wizard_caught)
    }
    
    fun playPowerUpSound() {
        playSoundEffect(R.raw.powerup_collected)
    }
    
    fun playGameOverSound() {
        playSoundEffect(R.raw.game_over)
    }
    
    fun setMusicEnabled(enabled: Boolean) {
        isMusicEnabled = enabled
        if (!enabled) {
            pauseBackgroundMusic()
        } else if (backgroundMusicPlayer != null) {
            resumeBackgroundMusic()
        }
    }
    
    fun setSoundEnabled(enabled: Boolean) {
        isSoundEnabled = enabled
    }
    
    fun setMusicVolume(volume: Float) {
        musicVolume = volume.coerceIn(0f, 1f)
        backgroundMusicPlayer?.setVolume(musicVolume, musicVolume)
    }
    
    fun setSoundVolume(volume: Float) {
        soundVolume = volume.coerceIn(0f, 1f)
    }
    
    fun isMusicPlaying(): Boolean {
        return backgroundMusicPlayer?.isPlaying == true
    }
    
    fun release() {
        stopBackgroundMusic()
        soundEffectPlayer?.release()
        soundEffectPlayer = null
    }
}

