package com.thorinx.wizardchase.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thorinx.wizardchase.R
import com.thorinx.wizardchase.data.Language
import com.thorinx.wizardchase.data.Languages
import com.thorinx.wizardchase.ui.theme.*

@Composable
fun SettingsScreen(
    currentLanguage: Language,
    onLanguageChanged: (Language) -> Unit,
    onMusicEnabledChanged: (Boolean) -> Unit,
    onSoundEnabledChanged: (Boolean) -> Unit,
    isMusicEnabled: Boolean = true,
    isSoundEnabled: Boolean = true,
    onSave: (() -> Unit)? = null,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    // BackHandler MainActivity'de handle ediliyor
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AnomaDark)
    ) {
        // Header - sadece başlık, geri butonu yok
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AnomaDarkGray)
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.settings),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        
        // Settings content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Language Section
            item {
                Text(
                    text = stringResource(R.string.language),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AnomaRed,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            items(Languages.allLanguages) { language ->
                LanguageOption(
                    language = language,
                    isSelected = language.code == currentLanguage.code,
                    onLanguageSelected = onLanguageChanged
                )
            }
            
            // Audio Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.audio_settings),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AnomaRed,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            // Music Toggle
            item {
                AudioToggleOption(
                    title = stringResource(R.string.background_music),
                    isEnabled = isMusicEnabled,
                    onToggle = onMusicEnabledChanged
                )
            }
            
            // Sound Effects Toggle
            item {
                AudioToggleOption(
                    title = stringResource(R.string.sound_effects),
                    isEnabled = isSoundEnabled,
                    onToggle = onSoundEnabledChanged
                )
            }
            
            // Alt kısımda boşluk bırak
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun LanguageOption(
    language: Language,
    isSelected: Boolean,
    onLanguageSelected: (Language) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onLanguageSelected(language) },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AnomaRed else AnomaDarkGray
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = getFlagEmojiForLanguage(language.code),
                    fontSize = 20.sp
                )
                Text(
                    text = language.displayName,
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = Color.White
                )
            }
            
            if (isSelected) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun AudioToggleOption(
    title: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AnomaDarkGray
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = AnomaRed,
                    checkedTrackColor = AnomaRed.copy(alpha = 0.5f),
                    uncheckedThumbColor = AnomaMediumGray,
                    uncheckedTrackColor = AnomaMediumGray.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
private fun getFlagEmojiForLanguage(code: String): String {
    return when (code) {
        "tr" -> "🇹🇷"
        "en" -> "🇺🇸"
        "zh" -> "🇨🇳"
        "ja" -> "🇯🇵"
        "es" -> "🇪🇸"
        "ko" -> "🇰🇷"
        "id" -> "🇮🇩"
        "vi" -> "🇻🇳"
        "ru" -> "🇷🇺"
        else -> "🏳️"
    }
}
