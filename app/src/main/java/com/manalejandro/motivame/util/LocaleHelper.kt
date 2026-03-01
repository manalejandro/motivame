package com.manalejandro.motivame.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    data class Language(val code: String, val flag: String, val nativeName: String)

    val SUPPORTED_LANGUAGES = listOf(
        Language("es", "🇪🇸", "Español"),
        Language("en", "🇬🇧", "English"),
        Language("zh", "🇨🇳", "中文"),
        Language("fr", "🇫🇷", "Français"),
        Language("de", "🇩🇪", "Deutsch"),
        Language("pt", "🇵🇹", "Português"),
        Language("ja", "🇯🇵", "日本語"),
        Language("ko", "🇰🇷", "한국어")
    )

    @SuppressLint("AppBundleLocaleChanges")
    fun applyLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    fun wrap(context: Context, languageCode: String): Context {
        if (languageCode.isEmpty()) return context
        return applyLocale(context, languageCode)
    }
}

