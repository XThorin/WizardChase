package com.thorinx.wizardchase.data

data class Language(
    val code: String,
    val name: String,
    val displayName: String
)

object Languages {
    val TURKISH = Language("tr", "Türkçe", "Türkçe")
    val ENGLISH = Language("en", "English", "English")
    val CHINESE = Language("zh", "中文", "中文")
    val JAPANESE = Language("ja", "日本語", "日本語")
    val SPANISH = Language("es", "Español", "Español")
    val KOREAN = Language("ko", "한국어", "한국어")
    val INDONESIAN = Language("id", "Bahasa Indonesia", "Bahasa Indonesia")
    val VIETNAMESE = Language("vi", "Tiếng Việt", "Tiếng Việt")
    val RUSSIAN = Language("ru", "Русский", "Русский")
    
    val allLanguages = listOf(
        TURKISH, ENGLISH, CHINESE, JAPANESE, SPANISH, 
        KOREAN, INDONESIAN, VIETNAMESE, RUSSIAN
    )
}

