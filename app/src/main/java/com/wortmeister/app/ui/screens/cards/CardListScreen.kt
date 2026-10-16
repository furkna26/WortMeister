package com.wortmeister.app.ui.screens.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wortmeister.app.data.local.entity.GermanCard
import com.wortmeister.app.data.repository.DeckRepository
import com.wortmeister.app.ui.components.ArticleBadge
import com.wortmeister.app.ui.components.LanguageBadge
import com.wortmeister.app.ui.components.ReflexiveBadge
import com.wortmeister.app.ui.components.WordTypeBadge
import com.wortmeister.app.ui.theme.SrsPriority
import com.wortmeister.app.ui.theme.bounceClick
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CardListScreen(
    deckRepository: DeckRepository,
    onSpeakGerman: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val allCards by deckRepository.allCards.collectAsState(initial = emptyList())
    val allDecks by deckRepository.allDecks.collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var showOnlyPriority by remember { mutableStateOf(false) }
    var selectedDeckFilterId by remember { mutableStateOf<Long?>(null) }

    // Dialog state for Custom Card Creation
    var showAddCustomCardDialog by remember { mutableStateOf(false) }
    var customFrontText by remember { mutableStateOf("") }
    var customBackText by remember { mutableStateOf("") }
    var customPartOfSpeech by remember { mutableStateOf("NOUN") }
    var customArticle by remember { mutableStateOf<String?>("der") }
    var customMeaningEn by remember { mutableStateOf("") }
    var customExampleSentence by remember { mutableStateOf("") }
    var customIsPriority by remember { mutableStateOf(false) }
    var customSelectedDeckId by remember { mutableStateOf<Long?>(null) }
    var customDeckDropdownExpanded by remember { mutableStateOf(false) }

    val filteredCards = allCards.filter { card ->
        val matchesQuery = searchQuery.isBlank() ||
                card.germanWord.contains(searchQuery, ignoreCase = true) ||
                card.meaningsTr.contains(searchQuery, ignoreCase = true) ||
                card.meaningsEn.contains(searchQuery, ignoreCase = true)
        val matchesPriority = !showOnlyPriority || card.isPriority
        val matchesDeck = selectedDeckFilterId == null || card.deckId == selectedDeckFilterId
        matchesQuery && matchesPriority && matchesDeck
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    customSelectedDeckId = allDecks.firstOrNull()?.id ?: 1L
                    showAddCustomCardDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Özel Kart Ekle")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Özel Kart Ekle", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Kayıtlı Kartlarım",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Toplam ${allCards.size} kelime kartı kayıtlı",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Filter Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Kayıtlı kartlarda ara...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = !showOnlyPriority && selectedDeckFilterId == null,
                    onClick = {
                        showOnlyPriority = false
                        selectedDeckFilterId = null
                    },
                    label = { Text("Tümü (${allCards.size})", fontSize = 12.sp) },
                    shape = RoundedCornerShape(10.dp)
                )
                FilterChip(
                    selected = showOnlyPriority,
                    onClick = { showOnlyPriority = !showOnlyPriority },
                    label = { Text("Önemli (${allCards.count { it.isPriority }})", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = SrsPriority
                        )
                    },
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Cards List
            if (filteredCards.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (allCards.isEmpty()) "Henüz kart eklenmemiş. Sağ alttaki butondan özel kart ekleyebilir veya Sözlük'ten aratabilirsiniz." else "Aramaya uygun kart bulunamadı.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredCards, key = { it.id }) { card ->
                        SavedCardRowItem(
                            card = card,
                            onSpeak = { onSpeakGerman(card.germanWord) },
                            onTogglePriority = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    deckRepository.toggleCardPriority(card.id, card.isPriority)
                                }
                            },
                            onDelete = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    deckRepository.deleteCard(card)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // DIALOG: Özel Kart Ekleme (Custom Card Creation)
    if (showAddCustomCardDialog) {
        AlertDialog(
            onDismissRequest = { showAddCustomCardDialog = false },
            title = {
                Text(text = "Yeni Özel Kart Ekle", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Deck Selector
                    if (allDecks.isNotEmpty()) {
                        ExposedDropdownMenuBox(
                            expanded = customDeckDropdownExpanded,
                            onExpandedChange = { customDeckDropdownExpanded = !customDeckDropdownExpanded }
                        ) {
                            val selectedDeck = allDecks.find { it.id == customSelectedDeckId } ?: allDecks.first()
                            OutlinedTextField(
                                value = selectedDeck.name,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Hedef Deste", fontSize = 11.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = customDeckDropdownExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = customDeckDropdownExpanded,
                                onDismissRequest = { customDeckDropdownExpanded = false }
                            ) {
                                allDecks.forEach { deck ->
                                    DropdownMenuItem(
                                        text = { Text(deck.name) },
                                        onClick = {
                                            customSelectedDeckId = deck.id
                                            customDeckDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Front text
                    OutlinedTextField(
                        value = customFrontText,
                        onValueChange = { customFrontText = it },
                        label = { Text("Ön Yüz (Almanca Kelime / Soru / Cümle)") },
                        placeholder = { Text("Örn: das Buch veya Auf Wiedersehen") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Back text
                    OutlinedTextField(
                        value = customBackText,
                        onValueChange = { customBackText = it },
                        label = { Text("Arka Yüz (Türkçe Anlam / Karşılık)") },
                        placeholder = { Text("Örn: Kitap veya Görüşmek üzere") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Part of Speech Chips
                    Text(text = "Kelime Türü:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    val posList = listOf("NOUN" to "İsim", "VERB" to "Fiil", "ADJECTIVE" to "Sıfat", "PHRASE" to "Kalıp", "OTHER" to "Diğer")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        posList.forEach { (pos, label) ->
                            FilterChip(
                                selected = customPartOfSpeech == pos,
                                onClick = { customPartOfSpeech = pos },
                                label = { Text(label, fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    // Article Selection if NOUN
                    if (customPartOfSpeech == "NOUN") {
                        Text(text = "Artikel:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        val articles = listOf("der", "die", "das")
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            articles.forEach { art ->
                                FilterChip(
                                    selected = customArticle == art,
                                    onClick = { customArticle = art },
                                    label = { Text(art.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }

                    // Optional English meaning
                    OutlinedTextField(
                        value = customMeaningEn,
                        onValueChange = { customMeaningEn = it },
                        label = { Text("İngilizce Karşılık (İsteğe Bağlı)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Optional Example Sentence
                    OutlinedTextField(
                        value = customExampleSentence,
                        onValueChange = { customExampleSentence = it },
                        label = { Text("Örnek Cümle (İsteğe Bağlı)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Priority Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Öncelikli Olarak İşaretle", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Switch(
                            checked = customIsPriority,
                            onCheckedChange = { customIsPriority = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customFrontText.isNotBlank() && customBackText.isNotBlank()) {
                            val deckId = customSelectedDeckId ?: allDecks.firstOrNull()?.id ?: 1L
                            val finalArticle = if (customPartOfSpeech == "NOUN") customArticle else null
                            val cleanGerman = if (finalArticle != null && customFrontText.startsWith("$finalArticle ", true)) {
                                customFrontText
                            } else if (finalArticle != null) {
                                "$finalArticle $customFrontText"
                            } else {
                                customFrontText
                            }

                            val deckName = allDecks.find { it.id == deckId }?.name ?: ""
                            val cardLang = if (deckName.contains("İngilizce", true)) "en" else "de"

                            val newCard = GermanCard(
                                deckId = deckId,
                                germanWord = cleanGerman,
                                partOfSpeech = customPartOfSpeech,
                                article = finalArticle,
                                pluralForm = null,
                                praesens = null,
                                praeteritum = null,
                                partizipZwei = null,
                                imperativ = null,
                                meaningsTr = customBackText,
                                meaningsEn = customMeaningEn,
                                exampleSentence = customExampleSentence.takeIf { it.isNotBlank() },
                                nextReviewDate = System.currentTimeMillis(),
                                intervalDays = 0,
                                isPriority = customIsPriority,
                                frontIncludes = "GERMAN,GRAMMAR",
                                backIncludes = "TURKISH,ENGLISH,EXAMPLE",
                                targetLanguage = cardLang
                            )

                            CoroutineScope(Dispatchers.IO).launch {
                                deckRepository.saveCard(newCard)
                            }

                            showAddCustomCardDialog = false
                            customFrontText = ""
                            customBackText = ""
                            customMeaningEn = ""
                            customExampleSentence = ""
                            customIsPriority = false
                        }
                    }
                ) {
                    Text("Kaydet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCustomCardDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
fun SavedCardRowItem(
    card: GermanCard,
    onSpeak: () -> Unit = {},
    onTogglePriority: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Row 1: Word + Badges + Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Word and Badges
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    val effectiveLang = when {
                        card.article != null || card.praesens != null || card.praeteritum != null || card.partizipZwei != null || card.isSeparable -> "de"
                        card.targetLanguage.lowercase() in listOf("en", "english", "gb") -> "en"
                        else -> "de"
                    }
                    LanguageBadge(lang = effectiveLang)
                    Text(
                        text = card.germanWord,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    card.article?.let { ArticleBadge(article = it) }
                    val isReflexive = card.partOfSpeech == "VERB" && (card.germanWord.startsWith("sich ") || card.praesens?.contains("sich") == true)
                    if (isReflexive) {
                        ReflexiveBadge()
                    }
                    WordTypeBadge(partOfSpeech = card.partOfSpeech)
                }

                // Action Buttons (Compact, cleanly spaced)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(onClick = onSpeak, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Seslendir",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onTogglePriority, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (card.isPriority) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Öncelik",
                            tint = if (card.isPriority) SrsPriority else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Sil",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Row 2: Turkish Meaning
            if (card.meaningsTr.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = card.meaningsTr,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Row 3: English Meaning (if present)
            if (card.meaningsEn.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = card.meaningsEn,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Row 4: Example Sentence Quote (if present)
            card.exampleSentence?.let { ex ->
                if (ex.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = ex,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}
