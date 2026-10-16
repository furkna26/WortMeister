package com.wortmeister.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.wortmeister.app.data.repository.DeckRepository
import com.wortmeister.app.ui.screens.about.AboutScreen
import com.wortmeister.app.ui.screens.cards.CardListScreen
import com.wortmeister.app.ui.screens.decks.DecksScreen
import com.wortmeister.app.ui.screens.decks.DecksViewModel
import com.wortmeister.app.ui.screens.search.SearchScreen
import com.wortmeister.app.ui.screens.search.SearchViewModel
import com.wortmeister.app.ui.screens.statistics.StatisticsScreen
import com.wortmeister.app.ui.screens.statistics.StatisticsViewModel
import com.wortmeister.app.ui.screens.study.StudyScreen
import com.wortmeister.app.ui.screens.study.StudyViewModel
import com.wortmeister.app.ui.screens.translate.TranslateScreen
import com.wortmeister.app.ui.screens.translate.TranslateViewModel
import androidx.compose.runtime.mutableStateOf

import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.runtime.collectAsState
import com.wortmeister.app.ui.screens.game.GameScreen
import com.wortmeister.app.ui.screens.game.GameSelectionDialog
import com.wortmeister.app.ui.screens.game.GameViewModel

sealed class AppTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : AppTab("Anasayfa", Icons.Filled.Home, Icons.Outlined.Home)
    data object Translate : AppTab("Çeviri", Icons.Filled.Translate, Icons.Outlined.Translate)
    data object Study : AppTab("Tekrar", Icons.Filled.School, Icons.Outlined.School)
    data object Decks : AppTab("Desteler", Icons.Filled.Folder, Icons.Outlined.Folder)
    data object Cards : AppTab("Kartlar", Icons.Filled.Style, Icons.Outlined.Style)
    data object About : AppTab("Hakkında", Icons.Filled.Info, Icons.Outlined.Info)
}

@Composable
fun MainScreen(
    searchViewModel: SearchViewModel,
    translateViewModel: TranslateViewModel,
    studyViewModel: StudyViewModel,
    decksViewModel: DecksViewModel,
    statisticsViewModel: StatisticsViewModel,
    gameViewModel: GameViewModel,
    deckRepository: DeckRepository,
    cardPreferencesManager: com.wortmeister.app.data.local.preferences.CardPreferencesManager? = null,
    onSpeakGerman: (String) -> Unit = {},
    onResetOnboarding: () -> Unit = {}
) {
    val tabs = listOf(
        AppTab.Home,
        AppTab.Translate,
        AppTab.Study,
        AppTab.Decks,
        AppTab.Cards,
        AppTab.About
    )
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var showStatistics by rememberSaveable { mutableStateOf(false) }

    val gameUiState by gameViewModel.uiState.collectAsState()

    if (gameUiState.showDeckSelector) {
        GameSelectionDialog(
            uiState = gameUiState,
            onDismiss = { gameViewModel.dismissDeckSelector() },
            onScopeChange = { gameViewModel.setDeckScope(it) },
            onToggleDeck = { gameViewModel.toggleDeckSelection(it) },
            onSelectAllDecks = { gameViewModel.selectAllDecks(it) },
            onStartGame = { gameViewModel.startGame() },
            onSelectGameType = { gameViewModel.selectGameType(it) }
        )
    }

    if (gameUiState.isGameActive) {
        GameScreen(
            uiState = gameUiState,
            onSelectOption = { gameViewModel.selectOption(it) },
            onNextQuestion = { gameViewModel.nextQuestion() },
            onRestartGame = { gameViewModel.restartGame() },
            onExitGame = { gameViewModel.exitGame() },
            onSpeakGerman = onSpeakGerman
        )
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = !showStatistics && selectedTabIndex == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            showStatistics = false
                            selectedTabIndex = index
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 9.5.sp,
                                maxLines = 1,
                                softWrap = false,
                                letterSpacing = (-0.3).sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        if (showStatistics) {
            StatisticsScreen(
                viewModel = statisticsViewModel,
                onBack = { showStatistics = false },
                modifier = modifier
            )
        } else {
            when (selectedTabIndex) {
                0 -> SearchScreen(
                    viewModel = searchViewModel,
                    onSpeakGerman = onSpeakGerman,
                    onResetOnboarding = onResetOnboarding,
                    onNavigateToAbout = { selectedTabIndex = 5 },
                    onStudyDeck = { deckId ->
                        studyViewModel.selectDeck(deckId)
                        selectedTabIndex = 2
                    },
                    onOpenGame = { gameType ->
                        gameViewModel.openDeckSelector(gameType)
                    },
                    modifier = modifier
                )
                1 -> TranslateScreen(
                    viewModel = translateViewModel,
                    onSpeakGerman = onSpeakGerman,
                    modifier = modifier
                )
                2 -> StudyScreen(
                    viewModel = studyViewModel,
                    onSpeakGerman = onSpeakGerman,
                    onOpenStatistics = { showStatistics = true },
                    modifier = modifier
                )
                3 -> DecksScreen(
                    viewModel = decksViewModel,
                    onStudyDeck = { deckId ->
                        studyViewModel.selectDeck(deckId)
                        selectedTabIndex = 2
                    },
                    modifier = modifier
                )
                4 -> CardListScreen(
                    deckRepository = deckRepository,
                    onSpeakGerman = onSpeakGerman,
                    modifier = modifier
                )
                5 -> AboutScreen(
                    deckRepository = deckRepository,
                    cardPreferencesManager = cardPreferencesManager,
                    onResetOnboarding = onResetOnboarding,
                    onOpenStatistics = { showStatistics = true },
                    modifier = modifier
                )
            }
        }
    }
}
