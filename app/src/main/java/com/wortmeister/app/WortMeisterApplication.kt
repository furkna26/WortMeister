package com.wortmeister.app

import android.app.Application
import com.wortmeister.app.data.local.database.AppDatabase
import com.wortmeister.app.data.repository.DeckRepository
import com.wortmeister.app.data.repository.WordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class WortMeisterApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val deckRepository by lazy { DeckRepository(database.deckDao(), database.germanCardDao(), database.studyLogDao()) }
    val cardPreferencesManager by lazy { com.wortmeister.app.data.local.preferences.CardPreferencesManager(this) }
    val wordRepository by lazy { WordRepository(database.germanCardDao(), prefsManager = cardPreferencesManager) }
    val recentSearchesManager by lazy { com.wortmeister.app.data.local.preferences.RecentSearchesManager(this) }
    val streakManager by lazy { com.wortmeister.app.data.local.preferences.StreakManager(this) }

    override fun onCreate() {
        super.onCreate()
        // Pre-initialize database so default deck and starters are ready
        database
    }
}
