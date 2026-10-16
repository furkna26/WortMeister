package com.wortmeister.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wortmeister.app.ui.MainScreen
import com.wortmeister.app.ui.screens.decks.DecksViewModel
import com.wortmeister.app.ui.screens.onboarding.OnboardingScreen
import com.wortmeister.app.ui.screens.search.SearchViewModel
import com.wortmeister.app.ui.screens.statistics.StatisticsViewModel
import com.wortmeister.app.ui.screens.study.StudyViewModel
import com.wortmeister.app.ui.screens.translate.TranslateViewModel
import com.wortmeister.app.ui.theme.WortMeisterTheme
import com.wortmeister.app.util.TtsManager

class MainActivity : ComponentActivity() {

    private val applicationInstance by lazy { application as WortMeisterApplication }

    private val searchViewModel: SearchViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchViewModel(
                    applicationInstance.wordRepository,
                    applicationInstance.deckRepository,
                    applicationInstance.cardPreferencesManager,
                    applicationInstance.recentSearchesManager
                ) as T
            }
        }
    }

    private val translateViewModel: TranslateViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TranslateViewModel(
                    wordRepository = applicationInstance.wordRepository,
                    deckRepository = applicationInstance.deckRepository,
                    prefsManager = applicationInstance.cardPreferencesManager
                ) as T
            }
        }
    }

    private val studyViewModel: StudyViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return StudyViewModel(
                    applicationInstance.deckRepository,
                    applicationInstance.streakManager
                ) as T
            }
        }
    }

    private val decksViewModel: DecksViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DecksViewModel(
                    applicationInstance.deckRepository
                ) as T
            }
        }
    }

    private val statisticsViewModel: StatisticsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return StatisticsViewModel(
                    deckRepository = applicationInstance.deckRepository,
                    streakManager = applicationInstance.streakManager
                ) as T
            }
        }
    }

    private val gameViewModel: com.wortmeister.app.ui.screens.game.GameViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return com.wortmeister.app.ui.screens.game.GameViewModel(
                    deckRepository = applicationInstance.deckRepository
                ) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val ttsManager = remember { TtsManager(this) }

            androidx.compose.runtime.DisposableEffect(Unit) {
                onDispose {
                    ttsManager.shutdown()
                }
            }

            val cardPrefs = applicationInstance.cardPreferencesManager
            var isOnboardingCompleted by remember { mutableStateOf(cardPrefs.isOnboardingCompleted) }

            val searchUiState by searchViewModel.uiState.collectAsState()
            val isDark = when (searchUiState.themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            WortMeisterTheme(darkTheme = isDark) {
                if (!isOnboardingCompleted) {
                    OnboardingScreen(
                        cardPreferencesManager = cardPrefs,
                        deckRepository = applicationInstance.deckRepository,
                        onComplete = {
                            searchViewModel.refreshFromPreferences()
                            isOnboardingCompleted = true
                        }
                    )
                } else {
                    MainScreen(
                        searchViewModel = searchViewModel,
                        translateViewModel = translateViewModel,
                        studyViewModel = studyViewModel,
                        decksViewModel = decksViewModel,
                        statisticsViewModel = statisticsViewModel,
                        gameViewModel = gameViewModel,
                        deckRepository = applicationInstance.deckRepository,
                        cardPreferencesManager = cardPrefs,
                        onSpeakGerman = { ttsManager.speakGerman(it) },
                        onResetOnboarding = {
                            cardPrefs.resetOnboarding()
                            isOnboardingCompleted = false
                        }
                    )
                }
            }
        }
    }
}
