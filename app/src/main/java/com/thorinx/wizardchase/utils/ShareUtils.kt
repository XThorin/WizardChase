package com.thorinx.wizardchase.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

object ShareUtils {
    
    fun shareScoreToX(context: Context, playerName: String, score: Int) {
        val message = "🎮 Wizard Chase oyununda $score puan yaptım! 🧙‍♂️\n" +
                     "Oyuncu: $playerName\n" +
                     "#WizardChase #Anoma #Gaming"
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
            
            // Try to open X (Twitter) app first
            setPackage("com.twitter.android")
            
            // If X app is not available, fall back to any app that can handle text sharing
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // If X app is not available, open share dialog
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Skoru Paylaş"))
        }
    }
    
    fun shareScoreToAnyApp(context: Context, playerName: String, score: Int) {
        val message = "🎮 Wizard Chase oyununda $score puan yaptım! 🧙‍♂️\n" +
                     "Oyuncu: $playerName\n" +
                     "#WizardChase #Anoma #Gaming"
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        context.startActivity(Intent.createChooser(intent, "Skoru Paylaş"))
    }
}

