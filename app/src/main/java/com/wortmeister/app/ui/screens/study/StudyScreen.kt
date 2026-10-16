package com.wortmeister.app.ui.screens.study

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wortmeister.app.data.local.entity.GermanCard
import com.wortmeister.app.data.repository.SrsRating
import com.wortmeister.app.ui.components.ArticleBadge
import com.wortmeister.app.ui.components.LanguageBadge
import com.wortmeister.app.ui.components.PluralBadge
import com.wortmeister.app.ui.components.VerbTenseTable
import com.wortmeister.app.ui.components.WordTypeBadge
import com.wortmeister.app.ui.theme.SrsLearned
import com.wortmeister.app.ui.theme.SrsPriority
import com.wortmeister.app.ui.theme.SrsUnsure
import com.wortmeister.app.ui.theme.bounceClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(
    viewModel: StudyViewModel,
    onSpeakGerman: (String) -> Unit = {},
    onOpenStatistics: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var deckDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // 1. TOP HEADER: Deck Picker & Refresh (Symmetrical row)
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
                val currentDeckName = if (uiState.selectedDeckId == null) {
                    "Tüm Kartlar"
                } else {
                    uiState.decks.find { it.id == uiState.selectedDeckId }?.name ?: "Tüm Kartlar"
                }

                OutlinedTextField(
                    value = currentDeckName,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    maxLines = 1,
                    label = {
                        Text(
                            text = "Çalışma Destesi",
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    },
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
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Tüm Kartlar",
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        },
                        onClick = {
                            viewModel.selectDeck(null)
                            deckDropdownExpanded = false
                        }
                    )
                    uiState.decks.forEach { deck ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = deck.name,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            },
                            onClick = {
                                viewModel.selectDeck(deck.id)
                                deckDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            IconButton(
                onClick = onOpenStatistics,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = "İstatistiklerim",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                onClick = { viewModel.restartSession() },
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Desteyi Yenile",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. STREAK & STATS STRIP (Tapping opens Statistics)
        uiState.gamificationStats?.let { stats ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                    .clickable { onOpenStatistics() }
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${stats.streakDays} Gün Seri • Bugün: ${stats.cardsToday} Kart",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "📊",
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = "${stats.levelTitle} ➔",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // 3. SESSION PROGRESS BAR
        if (uiState.sessionQueue.isNotEmpty() && !uiState.isSessionComplete) {
            val progress = (uiState.currentIndex.toFloat() / uiState.sessionQueue.size.toFloat()).coerceIn(0f, 1f)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kart ${uiState.currentIndex + 1} / ${uiState.sessionQueue.size}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "%${(progress * 100).toInt()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4. FULL-SCREEN FLASHCARD VIEW
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    Text(
                        text = "Kartlar yükleniyor...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                uiState.sessionQueue.isEmpty() -> {
                    EmptyDeckView()
                }
                uiState.isSessionComplete -> {
                    SessionCompletedView(
                        learnedCount = uiState.learnedCount,
                        unsureCount = uiState.unsureCount,
                        priorityCount = uiState.priorityCount,
                        onRestart = { viewModel.restartSession() }
                    )
                }
                else -> {
                    val currentCard = uiState.sessionQueue[uiState.currentIndex]
                    FlashCardView(
                        card = currentCard,
                        isFlipped = uiState.isFlipped,
                        onCardClick = { viewModel.flipCard() },
                        onSpeakGerman = onSpeakGerman
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 5. SRS BOTTOM ACTION BAR (Strictly Symmetrical & Single-Hand Friendly)
        if (uiState.sessionQueue.isNotEmpty() && !uiState.isSessionComplete) {
            SrsActionBar(
                onLearned = { viewModel.answerCard(SrsRating.LEARNED) },
                onUnsure = { viewModel.answerCard(SrsRating.UNSURE) },
                onPriority = { viewModel.answerCard(SrsRating.PRIORITY) }
            )
        }
    }
}

@Composable
fun FlashCardView(
    card: GermanCard,
    isFlipped: Boolean,
    onCardClick: () -> Unit,
    onSpeakGerman: (String) -> Unit = {}
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 350),
        label = "cardFlip"
    )

    Card(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onCardClick() }
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
        ) {
            if (rotation <= 90f) {
                // FRONT OF CARD
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header: Type badge & Priority star
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WordTypeBadge(partOfSpeech = card.partOfSpeech)
                        if (card.isPriority) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SrsPriority.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = SrsPriority,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "ÖNCELİKLİ",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SrsPriority
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Center Content: German Word & Morphology
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val cardLang = when {
                            card.article != null || card.praesens != null || card.praeteritum != null || card.partizipZwei != null || card.isSeparable -> "de"
                            card.targetLanguage.lowercase() in listOf("en", "english", "gb") -> "en"
                            else -> "de"
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            LanguageBadge(lang = cardLang)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = card.germanWord,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { onSpeakGerman(card.germanWord) },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Seslendir",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Grammar Details (Noun badges or Verb 2x2 grid)
                        if (card.partOfSpeech == "NOUN") {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                card.article?.let { ArticleBadge(article = it) }
                                card.pluralForm?.let { PluralBadge(pluralForm = it) }
                            }
                        } else if (card.partOfSpeech == "VERB") {
                            val isReflexive = card.germanWord.startsWith("sich ") || card.praesens?.contains("sich") == true
                            VerbTenseTable(
                                praesens = card.praesens,
                                praeteritum = card.praeteritum,
                                partizipZwei = card.partizipZwei,
                                imperativ = card.imperativ,
                                hilfsverb = card.hilfsverb,
                                isSeparable = card.isSeparable,
                                separablePrefix = card.separablePrefix,
                                isReflexive = isReflexive,
                                reflexiveType = if (isReflexive) "sich" else null
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tap to flip hint pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Anlamı görmek için dokunun",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else {
                // BACK OF CARD (Flipped)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f }
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "KART ARKA YÜZÜ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            letterSpacing = 1.sp
                        )
                        if (card.isPriority) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SrsPriority.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ÖNCELİKLİ",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SrsPriority
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Turkish Meanings
                        if (card.meaningsTr.isNotBlank()) {
                            LanguageBadge(lang = "tr", label = "TR")
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = card.meaningsTr,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }

                        // English Meanings
                        if (card.meaningsEn.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                LanguageBadge(lang = "en", label = "EN")
                                Text(
                                    text = card.meaningsEn,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        val targetTitle = when (card.targetLanguage.lowercase()) {
                            "en" -> "İngilizce"
                            "tr" -> "Türkçe"
                            else -> "Almanca"
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "$targetTitle: ${card.germanWord}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.outline
                        )

                        // Example Sentence
                        card.exampleSentence?.let { example ->
                            if (example.isNotBlank()) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.FormatQuote,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Örnek Cümle",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            IconButton(
                                                onClick = { onSpeakGerman(example) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                                    contentDescription = "Seslendir",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = example,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Ön yüze dönmek için dokunun",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SrsActionBar(
    onLearned: () -> Unit,
    onUnsure: () -> Unit,
    onPriority: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Çok Önemli (Priority)
        Button(
            onClick = onPriority,
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .bounceClick(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SrsPriority),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Önemli",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Sık Göster",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        // Emin Değilim (Unsure)
        Button(
            onClick = onUnsure,
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .bounceClick(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SrsUnsure),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Tekrar",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Sona Ekle",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        // Öğrendim (Learned)
        Button(
            onClick = onLearned,
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .bounceClick(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SrsLearned),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Öğrendim",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Aralığı Uzat",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
fun SessionCompletedView(
    learnedCount: Int,
    unsureCount: Int,
    priorityCount: Int,
    onRestart: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                modifier = Modifier.size(52.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Tebrikler, Oturum Tamamlandı",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Bu oturumdaki kartların tekrarını tamamladınız.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatBadge(label = "Öğrenildi", count = learnedCount, color = SrsLearned)
                StatBadge(label = "Tekrar Edildi", count = unsureCount, color = SrsUnsure)
                StatBadge(label = "Öncelikli", count = priorityCount, color = SrsPriority)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onRestart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .bounceClick(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Oturumu Yeniden Başlat", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StatBadge(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EmptyDeckView() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(20.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Translate,
            contentDescription = null,
            modifier = Modifier.size(52.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Bu destede kart bulunmuyor",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Sözlük sekmesinden kelime arayıp hemen destenize kart ekleyebilirsiniz.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
