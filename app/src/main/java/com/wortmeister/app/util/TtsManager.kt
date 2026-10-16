package com.wortmeister.app.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.GERMAN)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w("TtsManager", "German language is not supported or missing data on this device.")
            } else {
                isInitialized = true
                tts?.setSpeechRate(0.9f) // Slightly slower for clearer learner pronunciation
            }
        } else {
            Log.e("TtsManager", "Failed to initialize TTS engine.")
        }
    }

    fun speakGerman(text: String) {
        if (!isInitialized || text.isBlank()) return
        // Clean out Turkish translation or notes in parentheses / brackets
        val cleanGerman = text
            .replace(Regex("""\s*\([^\)]*\)"""), "")
            .replace(Regex("""\s*\[[^\]]*\]"""), "")
            .substringBefore(" - ")
            .trim()
            .ifBlank { text.trim() }

        tts?.stop()
        tts?.speak(cleanGerman, TextToSpeech.QUEUE_FLUSH, null, "wortmeister_tts_${System.currentTimeMillis()}")
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
