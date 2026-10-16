package com.wortmeister.app.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Translate
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import com.wortmeister.app.ui.theme.SrsLearned
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wortmeister.app.data.local.preferences.CardPreferencesManager
import com.wortmeister.app.ui.components.ArticleBadge
import com.wortmeister.app.ui.components.AutoCorrectionBanner
import com.wortmeister.app.ui.components.LanguageBadge
import com.wortmeister.app.ui.components.PluralBadge
import com.wortmeister.app.ui.components.ReflexiveBadge
import com.wortmeister.app.ui.components.RootLemmaBanner
import com.wortmeister.app.ui.components.SearchSourceBanner
import com.wortmeister.app.ui.components.SuggestionEngineCard
import com.wortmeister.app.ui.components.VerbTenseTable
import com.wortmeister.app.ui.components.WordTypeBadge
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.SportsEsports
import com.wortmeister.app.ui.screens.game.GameType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onSpeakGerman: (String) -> Unit = {},
    onResetOnboarding: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onStudyDeck: (Long) -> Unit = {},
    onOpenGame: (GameType) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    var showNewDeckDialog by remember { mutableStateOf(false) }
    var newDeckName by remember { mutableStateOf("") }
    var deckDropdownExpanded by remember { mutableStateOf(false) }

    var showAddMeaningDialog by remember { mutableStateOf(false) }
    var customMeaningInput by remember { mutableStateOf("") }

    var showAddExampleDialog by remember { mutableStateOf(false) }
    var customExampleGerman by remember { mutableStateOf("") }
    var customExampleTurkish by remember { mutableStateOf("") }

    var showTemplateDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.saveSuccessMessage) {
        uiState.saveSuccessMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearFeedback()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearFeedback()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 12.dp)
        ) {
            // 1. TOP HEADER: Logo + App Name + Actions (About, Template Settings, Theme)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                com.wortmeister.app.ui.components.WortMeisterLogoHeader(
                    themeMode = uiState.themeMode,
                    onToggleTheme = { viewModel.toggleThemeMode() },
                    modifier = Modifier.weight(1f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = onNavigateToAbout,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Hakkında ve Lisanslar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    IconButton(
                        onClick = { showTemplateDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Kart Şablonu Ayarları",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. QUIZLET PILL SEARCH BAR
            TextField(
                value = uiState.query,
                onValueChange = { viewModel.onQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                placeholder = {
                    Text(
                        "Kelime, terim veya çeviri ara...",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Ara",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                },
                trailingIcon = {
                    if (uiState.query.isNotBlank()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Temizle")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(27.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        viewModel.searchWord()
                    }
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3. SEARCH LANGUAGE SELECTOR (Horizontally scrollable and non-wrapping)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val languages = listOf(
                    "auto" to "Otomatik",
                    "de" to "DE · Almanca",
                    "tr" to "TR · Türkçe",
                    "en" to "EN · İngilizce"
                )
                languages.forEach { (code, label) ->
                    val isSelected = uiState.selectedSearchLanguage == code
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setSearchLanguage(code) },
                        label = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                maxLines = 1,
                                softWrap = false,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            // 4. LOADING INDICATOR
            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                )
            }

            // 5. QUIZLET HOME FEED: "Jump back in" Hero Card, Games & "Recents"
            if (uiState.query.isBlank() && uiState.searchResult == null) {
                // KELİME OYUNLARI & TESTLER (Pitch-Black OLED Cards)
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kelime Oyunları & Testler",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Alıştırma Yap",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                val gameTypes = listOf(
                    GameType.ARTICLE to (Icons.Default.School to "der · die · das"),
                    GameType.VERB to (Icons.Default.Translate to "Partizip II & Zamanlar"),
                    GameType.NOUN to (Icons.Default.Style to "Çoğullar & Anlam"),
                    GameType.ADJECTIVE to (Icons.Default.AutoAwesome to "Komparativ & Sıfatlar"),
                    GameType.PHRASE to (Icons.Default.FormatQuote to "Deyimler & Kalıplar"),
                    GameType.MIXED to (Icons.Default.SportsEsports to "Karma Pratik Testi")
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 2
                ) {
                    gameTypes.forEach { (type, meta) ->
                        val (icon, desc) = meta
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onOpenGame(type) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF27272A))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(17.dp)
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = type.title,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                                Text(
                                    text = desc,
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                val activeDeck = uiState.recentDecks.firstOrNull()
                if (activeDeck != null) {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Kaldığın Yerden Devam Et",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = activeDeck.name,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Deste • ${activeDeck.cardCount} kart",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = { onStudyDeck(activeDeck.id) },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Başla",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // Two-tone Quizlet progress bar (Emerald & Light Grey)
                            val progress = if (activeDeck.cardCount > 0) {
                                (activeDeck.learnedCount.toFloat() / activeDeck.cardCount.toFloat()).coerceIn(0f, 1f)
                            } else 0f

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = SrsLearned,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${activeDeck.learnedCount}/${activeDeck.cardCount} kart çalışıldı",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "%${(progress * 100).toInt()}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SrsLearned
                                    )
                                }
                            }

                            Button(
                                onClick = { onStudyDeck(activeDeck.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(
                                    text = "Devam Et",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = androidx.compose.ui.graphics.Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = androidx.compose.ui.graphics.Color.White
                                )
                            }
                        }
                    }
                }

                // Recents Decks Section
                if (uiState.recentDecks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(22.dp))
                    Text(
                        text = "Son Desteler",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.recentDecks.take(4).forEachIndexed { index, deck ->
                            val (tileBg, tileIconColor, tileIcon) = when (index % 3) {
                                0 -> Triple(androidx.compose.ui.graphics.Color(0xFFE8EEFF), androidx.compose.ui.graphics.Color(0xFF4255FF), Icons.Default.Style)
                                1 -> Triple(androidx.compose.ui.graphics.Color(0xFFE6F7EF), androidx.compose.ui.graphics.Color(0xFF23B26D), Icons.Default.Folder)
                                else -> Triple(androidx.compose.ui.graphics.Color(0xFFFFF4E6), androidx.compose.ui.graphics.Color(0xFFFF9500), Icons.Default.School)
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onStudyDeck(deck.id) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(tileBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = tileIcon,
                                            contentDescription = null,
                                            tint = tileIconColor,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = deck.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${deck.cardCount} kart • WortMeister",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Recent Searches
                if (uiState.recentSearches.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Son Aramalar",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        uiState.recentSearches.take(6).forEach { recent ->
                            SuggestionChip(
                                onClick = {
                                    focusManager.clearFocus()
                                    viewModel.searchWord(recent)
                                },
                                label = { Text(recent, fontSize = 12.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }

            // 6. SUGGESTIONS / DID YOU MEAN?
            if (uiState.liveSuggestions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                SuggestionEngineCard(
                    suggestions = uiState.liveSuggestions,
                    onSuggestionClick = { selectedWord ->
                        focusManager.clearFocus()
                        viewModel.searchWord(selectedWord)
                    }
                )
            }

            // 6.5. MULTI-RESULT CANDIDATE SELECTOR ("X sonuç bulundu")
            if (uiState.candidateWords.isNotEmpty() && uiState.searchResult != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${uiState.candidateWords.size} sonuç bulundu",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Geçiş yapmak için seçin",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            uiState.candidateWords.forEachIndexed { index, candidate ->
                                val isSelected = uiState.searchResult?.germanWord?.equals(candidate, ignoreCase = true) == true
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        if (!isSelected) {
                                            viewModel.selectCandidateWord(candidate)
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = "${index + 1}. $candidate",
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        labelColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 6.7. EŞLEŞME BULUNAMADI - DEEPL İLE ÇEVİR KARTI
            if (uiState.hasNoMatch && !uiState.isLoading) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Text(
                            text = "Sözlükte Eşleşme Bulunamadı",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        val searchedWord = uiState.lastSearchedWord.ifBlank { uiState.query }
                        Text(
                            text = "„$searchedWord“ sözlük veritabanında bulunamadı. DeepL çeviri motorunu kullanarak bu kelime veya cümleyi çevirebilir ve çift dilli bir flashcard olarak destenize kaydedebilirsiniz.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = { viewModel.translateWithDeepLAndCreateCard() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DeepL ile Çevir",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = androidx.compose.ui.graphics.Color.White
                            )
                        }
                    }
                }
            }

            // 7. SEARCH RESULT CARD (Symmetrical Human Design)
            uiState.searchResult?.let { result ->
                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        // Translation / Source Banner: ONLY show when translating from TR or EN, or if terms are truly distinct!
                        val cleanQuery = result.searchedTerm.trim().lowercase().replace(Regex("^(der|die|das)\\s+"), "")
                        val cleanTarget = result.germanWord.trim().lowercase().replace(Regex("^(der|die|das)\\s+"), "")
                        val isSameGermanWord = cleanQuery == cleanTarget

                        if (result.sourceLanguage != "de" && !isSameGermanWord) {
                            SearchSourceBanner(
                                sourceLang = result.sourceLanguage,
                                searchedTerm = result.searchedTerm,
                                targetGerman = result.germanWord
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Auto-correction / Lemma Banners: ONLY show for genuine spelling typos, NOT for article or capitalization!
                        val isArticleOrCaseCorrection = !result.correctedFrom.isNullOrBlank() && (
                            isSameGermanWord ||
                            result.correctedFrom.trim().lowercase().replace(Regex("^(der|die|das)\\s+"), "") == cleanTarget
                        )

                        if (!result.correctedFrom.isNullOrBlank() && !isArticleOrCaseCorrection) {
                            AutoCorrectionBanner(
                                correctedFrom = result.correctedFrom,
                                correctedTo = result.germanWord.replace(Regex("^(der|die|das)\\s+"), "")
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        if (!result.rootLemmaInfo.isNullOrBlank()) {
                            RootLemmaBanner(lemmaInfo = result.rootLemmaInfo)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Header: Word + Speaker + Type Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                LanguageBadge(lang = "de", label = "DE")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = result.germanWord,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = { onSpeakGerman(result.germanWord) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Seslendir",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (result.isReflexive) {
                                    ReflexiveBadge()
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                WordTypeBadge(partOfSpeech = result.partOfSpeech)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Grammar Details
                        if (result.partOfSpeech == "NOUN") {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                result.article?.let { ArticleBadge(article = it) }
                                result.pluralForm?.let { PluralBadge(pluralForm = it) }
                            }
                        } else if (result.partOfSpeech == "VERB") {
                            VerbTenseTable(
                                praesens = result.praesens,
                                praeteritum = result.praeteritum,
                                partizipZwei = result.partizipZwei,
                                imperativ = result.imperativ,
                                hilfsverb = result.hilfsverb,
                                regularity = result.regularity,
                                isSeparable = result.isSeparable,
                                separablePrefix = result.separablePrefix,
                                isReflexive = result.isReflexive,
                                reflexiveType = result.reflexiveType,
                                irregularConjugation = result.irregularConjugation
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Divider
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Turkish Meanings Section
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Türkçe Anlamlar",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (uiState.availableMeaningsTr.isEmpty() || uiState.availableMeaningsTr.all { it.contains("bulunamadı", true) }) {
                                    TextButton(onClick = { viewModel.translateWithDeepLAndCreateCard() }) {
                                        Icon(imageVector = Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("DeepL ile Çevir", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                TextButton(onClick = { showAddMeaningDialog = true }) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Anlam Ekle", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            uiState.availableMeaningsTr.forEach { meaning ->
                                val isSelected = uiState.selectedMeaningsTr.contains(meaning)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.toggleMeaningTr(meaning) },
                                    label = { Text(meaning, fontSize = 13.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        // English Meanings Section
                        if (uiState.availableMeaningsEn.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "İngilizce Karşılıklar",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                uiState.availableMeaningsEn.take(4).forEach { meaning ->
                                    val isSelected = uiState.selectedMeaningsEn.contains(meaning)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.toggleMeaningEn(meaning) },
                                        label = { Text(meaning, fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }
                        }

                        // Example Sentences Section
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Örnek Cümleler",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            TextButton(onClick = { showAddExampleDialog = true }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Cümle Ekle", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        if (uiState.availableExamples.isEmpty()) {
                            Text(
                                text = "Örnek cümle bulunamadı. Butondan ekleyebilirsiniz.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                uiState.availableExamples.take(3).forEach { pair ->
                                    val fullText = if (pair.turkish.isNotBlank()) "${pair.german} (${pair.turkish})" else pair.german
                                    val isSelected = uiState.selectedExample == fullText

                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.selectExample(fullText) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected)
                                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                            else
                                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { viewModel.selectExample(fullText) }
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = pair.german,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                if (pair.turkish.isNotBlank()) {
                                                    Text(
                                                        text = pair.turkish,
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                            IconButton(
                                                onClick = { onSpeakGerman(pair.german) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                                    contentDescription = "Seslendir",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Target Deck Selector & Save Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ExposedDropdownMenuBox(
                                expanded = deckDropdownExpanded,
                                onExpandedChange = { deckDropdownExpanded = !deckDropdownExpanded },
                                modifier = Modifier.weight(1f)
                            ) {
                                val currentDeck = uiState.decks.find { it.id == uiState.selectedDeckId }
                                OutlinedTextField(
                                    value = currentDeck?.name ?: "Deste Seç",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Hedef Deste", fontSize = 11.sp) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deckDropdownExpanded) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                ExposedDropdownMenu(
                                    expanded = deckDropdownExpanded,
                                    onDismissRequest = { deckDropdownExpanded = false }
                                ) {
                                    uiState.decks.forEach { deck ->
                                        DropdownMenuItem(
                                            text = { Text(deck.name) },
                                            onClick = {
                                                viewModel.selectDeck(deck.id)
                                                deckDropdownExpanded = false
                                            }
                                        )
                                    }
                                    DropdownMenuItem(
                                        text = { Text("+ Yeni Deste Oluştur", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                                        onClick = {
                                            deckDropdownExpanded = false
                                            showNewDeckDialog = true
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.saveCard() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(imageVector = Icons.Default.BookmarkAdd, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Kartı Desteye Kaydet", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // DIALOG: Card Template Preferences
    if (showTemplateDialog) {
        AlertDialog(
            onDismissRequest = { showTemplateDialog = false },
            title = { Text("Kart Şablonu Ayarları", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "Kartın ÖN Yüzünde Gösterilecekler:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val frontOptions = listOf(
                        CardPreferencesManager.FIELD_GERMAN to "Almanca",
                        CardPreferencesManager.FIELD_TURKISH to "Türkçe",
                        CardPreferencesManager.FIELD_ENGLISH to "İngilizce",
                        CardPreferencesManager.FIELD_GRAMMAR to "Gramer & Zamanlar",
                        CardPreferencesManager.FIELD_EXAMPLE to "Örnek Cümle"
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        frontOptions.forEach { (fieldKey, label) ->
                            val isSelected = uiState.frontIncludes.contains(fieldKey)
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.toggleFrontInclude(fieldKey) },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Kartın ARKA Yüzünde Gösterilecekler:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val backOptions = listOf(
                        CardPreferencesManager.FIELD_TURKISH to "Türkçe Anlam",
                        CardPreferencesManager.FIELD_ENGLISH to "İngilizce Anlam",
                        CardPreferencesManager.FIELD_GERMAN to "Almanca Kelime",
                        CardPreferencesManager.FIELD_EXAMPLE to "Örnek Cümle",
                        CardPreferencesManager.FIELD_GRAMMAR to "Gramer Detayı"
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        backOptions.forEach { (fieldKey, label) ->
                            val isSelected = uiState.backIncludes.contains(fieldKey)
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.toggleBackInclude(fieldKey) },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showTemplateDialog = false }) {
                    Text("Tamam")
                }
            }
        )
    }

    // DIALOG: Add Custom Meaning
    if (showAddMeaningDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddMeaningDialog = false
                customMeaningInput = ""
            },
            title = { Text("Özel Türkçe Anlam Ekle", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = customMeaningInput,
                    onValueChange = { customMeaningInput = it },
                    label = { Text("Anlam") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customMeaningInput.isNotBlank()) {
                            viewModel.addCustomMeaningTr(customMeaningInput)
                            showAddMeaningDialog = false
                            customMeaningInput = ""
                        }
                    }
                ) {
                    Text("Ekle")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddMeaningDialog = false
                    customMeaningInput = ""
                }) {
                    Text("İptal")
                }
            }
        )
    }

    // DIALOG: Add Custom Example
    if (showAddExampleDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddExampleDialog = false
                customExampleGerman = ""
                customExampleTurkish = ""
            },
            title = { Text("Özel Örnek Cümle Ekle", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = customExampleGerman,
                        onValueChange = { customExampleGerman = it },
                        label = { Text("Almanca Cümle") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = customExampleTurkish,
                        onValueChange = { customExampleTurkish = it },
                        label = { Text("Türkçe Çevirisi (İsteğe Bağlı)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customExampleGerman.isNotBlank()) {
                            viewModel.addCustomExample(customExampleGerman, customExampleTurkish)
                            showAddExampleDialog = false
                            customExampleGerman = ""
                            customExampleTurkish = ""
                        }
                    }
                ) {
                    Text("Ekle")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddExampleDialog = false
                    customExampleGerman = ""
                    customExampleTurkish = ""
                }) {
                    Text("İptal")
                }
            }
        )
    }

    // DIALOG: Create New Deck
    if (showNewDeckDialog) {
        AlertDialog(
            onDismissRequest = {
                showNewDeckDialog = false
                newDeckName = ""
            },
            title = { Text("Yeni Deste Oluştur", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newDeckName,
                    onValueChange = { newDeckName = it },
                    label = { Text("Deste Adı") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newDeckName.isNotBlank()) {
                            viewModel.createAndSelectDeck(newDeckName)
                            showNewDeckDialog = false
                            newDeckName = ""
                        }
                    }
                ) {
                    Text("Oluştur")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showNewDeckDialog = false
                    newDeckName = ""
                }) {
                    Text("İptal")
                }
            }
        )
    }

    // DIALOG: About & Licenses Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("Hakkında, Şeffaflık & Lisanslar", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "WortMeister v1.0.0",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Almanca ve yabancı dil öğrenenler için akıllı sözlük, A1-C1+ çekim tablosu, deyimler (Redewendungen) ve aralıklı tekrar (SRS) flashcard uygulaması.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        text = "Veri Kaynakları & Atıflar",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "• Wiktionary & Kaikki: Kelime anlamları ve çekim tabloları (CC BY-SA 4.0)\n" +
                               "• Tatoeba: Çift dilli örnek cümle arşivi (CC BY 2.0 FR)\n" +
                               "• Verbformen / Netzverb: Fiil çekimleri ve morfoloji (CC BY-SA 4.0)\n" +
                               "• Redensarten-Index: Deyimler ve kalıp ifadeler",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        text = "Açık Kaynak Bileşenler",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "• Jetpack Compose & Material 3 (Apache 2.0)\n" +
                               "• Room SQLite Database (Apache 2.0)\n" +
                               "• OkHttp & Retrofit (Apache 2.0)\n" +
                               "• Jsoup HTML Parser (MIT)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        text = "Gizlilik",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Oluşturduğunuz kartlar, desteler ve çalışma ilerlemeniz cihazınızda yerel Room veri tabanında saklanır.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showAboutDialog = false }) {
                    Text("Kapat")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showAboutDialog = false
                        onResetOnboarding()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Dil & Kütüphane Kurulumu", fontSize = 12.sp)
                }
            }
        )
    }
}
