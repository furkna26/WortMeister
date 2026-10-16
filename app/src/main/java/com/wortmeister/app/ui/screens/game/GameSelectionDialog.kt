package com.wortmeister.app.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wortmeister.app.ui.theme.SrsLearned

import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults

@Composable
fun GameSelectionDialog(
    uiState: GameUiState,
    onDismiss: () -> Unit,
    onScopeChange: (DeckScopeOption) -> Unit,
    onToggleDeck: (Long) -> Unit,
    onSelectAllDecks: (Boolean) -> Unit,
    onStartGame: () -> Unit,
    onSelectGameType: (GameType) -> Unit = {}
) {
    val gameType = uiState.selectedGameType ?: return
    val isEligibleEnough = uiState.eligibleCardCount >= 4

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF111111),
        tonalElevation = 0.dp,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = gameType.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Text(
                        text = gameType.categoryLabel,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // GAME TYPE SELECTOR CHIPS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GameType.values().forEach { type ->
                        val isSelected = type == gameType
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectGameType(type) },
                            label = {
                                Text(
                                    text = type.title.replace(" Testi", ""),
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF1A1A1A),
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                Text(
                    text = gameType.subtitle,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFF27272A)
                )

                Text(
                    text = "Deste Kapsamı",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // OPTION 1: ALL DECKS
                val isAllSelected = uiState.deckScope == DeckScopeOption.ALL_DECKS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isAllSelected) Color(0xFF1C1C1E) else Color(0xFF141416))
                        .border(
                            1.dp,
                            if (isAllSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else Color(0xFF27272A),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onScopeChange(DeckScopeOption.ALL_DECKS) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isAllSelected,
                        onClick = { onScopeChange(DeckScopeOption.ALL_DECKS) },
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Tüm Desteleri Dahil Et",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Kayıtlı tüm destelerdeki kartlar taranır (${uiState.availableDecks.size} deste)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // OPTION 2: SELECT DECKS
                val isSpecificSelected = uiState.deckScope == DeckScopeOption.SELECTED_DECKS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSpecificSelected) Color(0xFF1C1C1E) else Color(0xFF141416))
                        .border(
                            1.dp,
                            if (isSpecificSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else Color(0xFF27272A),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onScopeChange(DeckScopeOption.SELECTED_DECKS) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSpecificSelected,
                        onClick = { onScopeChange(DeckScopeOption.SELECTED_DECKS) },
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Dahil Edilecek Desteleri Seç",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Yalnızca işaretlenen destelerdeki kartlar kullanılır",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // DECK CHECKBOX LIST (When SELECTED_DECKS)
                if (isSpecificSelected) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Desteler (${uiState.selectedDeckIds.size}/${uiState.availableDecks.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TextButton(
                                onClick = { onSelectAllDecks(true) },
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Tümünü Seç", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            TextButton(
                                onClick = { onSelectAllDecks(false) },
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Temizle", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 180.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (uiState.availableDecks.isEmpty()) {
                            Text(
                                text = "Henüz oluşturulmuş bir deste yok.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            uiState.availableDecks.forEach { deck ->
                                val isChecked = uiState.selectedDeckIds.contains(deck.id)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isChecked) Color(0xFF1E293B).copy(alpha = 0.5f) else Color(0xFF171717))
                                        .clickable { onToggleDeck(deck.id) }
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { onToggleDeck(deck.id) },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = MaterialTheme.colorScheme.primary
                                        ),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.Folder,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = deck.name,
                                        fontSize = 13.sp,
                                        color = if (isChecked) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                // DYNAMIC ELIGIBILITY BANNER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isEligibleEnough) Color(0xFF064E3B).copy(alpha = 0.4f)
                            else Color(0xFF7F1D1D).copy(alpha = 0.35f)
                        )
                        .border(
                            1.dp,
                            if (isEligibleEnough) SrsLearned.copy(alpha = 0.5f)
                            else Color(0xFFEF4444).copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (isEligibleEnough) Icons.Default.CheckCircle else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (isEligibleEnough) SrsLearned else Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isEligibleEnough) {
                                "Bu mod için ${uiState.eligibleCardCount} uygun kart hazır."
                            } else {
                                "Bu mod için en az 4 uygun kart gerekli (Şu an: ${uiState.eligibleCardCount}). Lütfen daha fazla deste seçin."
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isEligibleEnough) Color(0xFFD1FAE5) else Color(0xFFFEE2E2)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onStartGame,
                enabled = isEligibleEnough,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color(0xFF27272A)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Oyunu Başlat",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Vazgeç",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}
