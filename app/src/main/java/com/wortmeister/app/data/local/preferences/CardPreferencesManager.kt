package com.wortmeister.app.data.local.preferences

import android.content.Context
import android.content.SharedPreferences

class CardPreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("wortmeister_card_prefs", Context.MODE_PRIVATE)

    companion object {
        const val FIELD_GERMAN = "GERMAN"
        const val FIELD_TURKISH = "TURKISH"
        const val FIELD_ENGLISH = "ENGLISH"
        const val FIELD_GRAMMAR = "GRAMMAR"
        const val FIELD_EXAMPLE = "EXAMPLE"

        val DEFAULT_FRONT = setOf(FIELD_GERMAN, FIELD_GRAMMAR)
        val DEFAULT_BACK = setOf(FIELD_TURKISH, FIELD_ENGLISH, FIELD_EXAMPLE)
        const val DEFAULT_DEEPL_KEY = "a6f3a765-5960-469c-b469-15287391946b:fx"
    }

    var frontIncludes: Set<String>
        get() = prefs.getStringSet("front_includes", DEFAULT_FRONT) ?: DEFAULT_FRONT
        set(value) = prefs.edit().putStringSet("front_includes", value).apply()

    var backIncludes: Set<String>
        get() = prefs.getStringSet("back_includes", DEFAULT_BACK) ?: DEFAULT_BACK
        set(value) = prefs.edit().putStringSet("back_includes", value).apply()

    var targetLanguage: String
        get() = prefs.getString("target_language", "de") ?: "de"
        set(value) = prefs.edit().putString("target_language", value).apply()

    var searchLanguage: String
        get() = prefs.getString("search_language", "auto") ?: "auto"
        set(value) = prefs.edit().putString("search_language", value).apply()

    var themeMode: String
        get() = prefs.getString("theme_mode", "SYSTEM") ?: "SYSTEM"
        set(value) = prefs.edit().putString("theme_mode", value).apply()

    var isOnboardingCompleted: Boolean
        get() = prefs.getBoolean("is_onboarding_completed", false)
        set(value) = prefs.edit().putBoolean("is_onboarding_completed", value).apply()

    var installedDictionaries: Set<String>
        get() = prefs.getStringSet("installed_dictionaries", setOf("de", "tr")) ?: setOf("de", "tr")
        set(value) = prefs.edit().putStringSet("installed_dictionaries", value).apply()

    var deepLApiKey: String
        get() = prefs.getString("deepl_api_key", null)?.takeIf { it.isNotBlank() } ?: DEFAULT_DEEPL_KEY
        set(value) = prefs.edit().putString("deepl_api_key", value.trim()).apply()

    var customDeepLApiKey: String
        get() = prefs.getString("deepl_api_key", "") ?: ""
        set(value) = prefs.edit().putString("deepl_api_key", value.trim()).apply()

    fun resetOnboarding() {
        prefs.edit().putBoolean("is_onboarding_completed", false).apply()
    }
}
