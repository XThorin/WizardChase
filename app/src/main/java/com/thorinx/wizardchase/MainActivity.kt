package com.thorinx.wizardchase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.os.Build
import android.content.res.Configuration
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import android.os.LocaleList
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thorinx.wizardchase.data.Languages
import com.thorinx.wizardchase.ui.screens.GameScreen
import com.thorinx.wizardchase.audio.AudioManager
import com.thorinx.wizardchase.ui.screens.ResultsScreen
import com.thorinx.wizardchase.ui.screens.SettingsScreen
import com.thorinx.wizardchase.ui.screens.WelcomeScreen
import com.thorinx.wizardchase.ui.theme.WizardChaseTheme
import com.thorinx.wizardchase.viewmodel.GameScreen as GameScreenEnum
import com.thorinx.wizardchase.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WizardChaseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WizardChaseApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun WizardChaseApp(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()

    val baseContext = LocalContext.current
    
    // Initialize AudioManager and load settings
    LaunchedEffect(Unit) {
        val audioManager = AudioManager(baseContext)
        viewModel.setAudioManager(audioManager)
        
        // Load saved settings
        val prefs = baseContext.getSharedPreferences("settings", 0)
        
        // Load language
        val savedLanguage = prefs.getString("language_code", null)
        if (!savedLanguage.isNullOrBlank() && savedLanguage != currentLanguage.code) {
            val lang = Languages.allLanguages.firstOrNull { it.code == savedLanguage }
            if (lang != null) viewModel.setLanguage(lang)
        }
        
        // Load audio settings
        val musicEnabled = prefs.getBoolean("music_enabled", true)
        val soundEnabled = prefs.getBoolean("sound_enabled", true)
        viewModel.setMusicEnabled(musicEnabled)
        viewModel.setSoundEnabled(soundEnabled)
    }
    val localizedContext = androidx.compose.runtime.remember(currentLanguage.code) {
        val config = Configuration(baseContext.resources.configuration)
        val locale = when (currentLanguage.code) {
            "tr" -> Locale("tr", "TR")
            "en" -> Locale("en", "US")
            "zh" -> Locale("zh", "CN")
            "ja" -> Locale("ja", "JP")
            "es" -> Locale("es", "ES")
            "ko" -> Locale("ko", "KR")
            "id" -> Locale("in", "ID") // Indonesian uses 'in' as language code
            "vi" -> Locale("vi", "VN")
            "ru" -> Locale("ru", "RU")
            else -> Locale.forLanguageTag(currentLanguage.code)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        } else {
            @Suppress("DEPRECATION")
            config.setLocale(locale)
        }
        baseContext.createConfigurationContext(config)
    }

    CompositionLocalProvider(LocalContext provides localizedContext) {
        // Handle back button behavior based on current screen
        when (currentScreen) {
            GameScreenEnum.SETTINGS -> {
                // Settings sayfasında geri butonuna basılırsa welcome'a dön
                BackHandler {
                    viewModel.backFromSettings()
                }
            }
            GameScreenEnum.RESULTS -> {
                // Results sayfasında geri butonuna basılırsa welcome'a dön
                BackHandler {
                    viewModel.restartGame()
                }
            }
            GameScreenEnum.GAME -> {
                // Game ekranı kendi BackHandler'ını kullanıyor (dialog gösteriyor)
                // Burada hiçbir şey yapmıyoruz çünkü GameScreen kendi BackHandler'ını kullanıyor
            }
            GameScreenEnum.WELCOME -> {
                // Welcome sayfasında varsayılan davranış (uygulamadan çık)
                // Burada hiçbir şey yapmıyoruz çünkü varsayılan Android davranışı kullanılıyor
            }
        }
        
        when (currentScreen) {
        GameScreenEnum.WELCOME -> {
            WelcomeScreen(
                viewModel = viewModel,
                modifier = modifier
            )
        }
        GameScreenEnum.GAME -> {
            GameScreen(
                viewModel = viewModel,
                modifier = modifier
            )
        }
        GameScreenEnum.RESULTS -> {
            ResultsScreen(
                viewModel = viewModel,
                modifier = modifier
            )
        }
        GameScreenEnum.SETTINGS -> {
            val isMusicEnabled by viewModel.isMusicEnabled.collectAsState()
            val isSoundEnabled by viewModel.isSoundEnabled.collectAsState()
            
            SettingsScreen(
                currentLanguage = currentLanguage,
                onLanguageChanged = { language ->
                    try {
                        viewModel.setLanguage(language)
                        // Save immediately when language changes
                        val prefs = baseContext.getSharedPreferences("settings", 0)
                        prefs.edit().putString("language_code", language.code).commit() // Use commit instead of apply for immediate save
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                onMusicEnabledChanged = { enabled ->
                    viewModel.setMusicEnabled(enabled)
                    // Save audio settings
                    val prefs = baseContext.getSharedPreferences("settings", 0)
                    prefs.edit().putBoolean("music_enabled", enabled).apply()
                },
                onSoundEnabledChanged = { enabled ->
                    viewModel.setSoundEnabled(enabled)
                    // Save audio settings
                    val prefs = baseContext.getSharedPreferences("settings", 0)
                    prefs.edit().putBoolean("sound_enabled", enabled).apply()
                },
                isMusicEnabled = isMusicEnabled,
                isSoundEnabled = isSoundEnabled,
                onSave = {
                    try {
                        // Save current language and go back
                        val prefs = baseContext.getSharedPreferences("settings", 0)
                        prefs.edit().putString("language_code", currentLanguage.code).commit()
                        viewModel.backFromSettings()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // If save fails, still try to go back
                        viewModel.backFromSettings()
                    }
                },
                onBackPressed = { 
                    try {
                        // Just go back without saving changes
                        viewModel.backFromSettings() 
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                modifier = modifier
            )
        }
        }
    }
}