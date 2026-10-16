package com.wortmeister.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WordTypeBadge(
    partOfSpeech: String,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == com.wortmeister.app.ui.theme.BackgroundDark
    val (label, color) = when (partOfSpeech.uppercase()) {
        "NOUN" -> "İsim" to if (isDark) Color(0xFF60A5FA) else Color(0xFF2563EB)
        "VERB" -> "Fiil" to if (isDark) Color(0xFFA78BFA) else Color(0xFF7C3AED)
        "ADJECTIVE" -> "Sıfat" to if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706)
        "PREPOSITION" -> "Edat" to if (isDark) Color(0xFF34D399) else Color(0xFF059669)
        "PHRASE" -> "Kalıp" to if (isDark) Color(0xFFF472B6) else Color(0xFFDB2777)
        else -> "Kelime" to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = if (isDark) 0.22f else 0.12f))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun SeparableVerbBadge(
    isSeparable: Boolean,
    prefix: String? = null,
    modifier: Modifier = Modifier
) {
    if (!isSeparable) return
    val isDark = MaterialTheme.colorScheme.background == com.wortmeister.app.ui.theme.BackgroundDark
    val text = if (!prefix.isNullOrBlank()) "Ayrılabilen Fiil (trennbar: $prefix | ...)" else "Ayrılabilen Fiil (trennbar)"
    val textColor = if (isDark) Color(0xFFFCD34D) else Color(0xFFB45309)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF59E0B).copy(alpha = if (isDark) 0.22f else 0.15f))
            .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ReflexiveVerbBadge(
    isReflexive: Boolean,
    reflexiveType: String? = null,
    modifier: Modifier = Modifier
) {
    if (!isReflexive) return
    val isDark = MaterialTheme.colorScheme.background == com.wortmeister.app.ui.theme.BackgroundDark
    val text = if (!reflexiveType.isNullOrBlank()) "Reflexives Verb ($reflexiveType)" else "Reflexives Verb (sich)"
    val textColor = if (isDark) Color(0xFF67E8F9) else Color(0xFF0E7490)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF06B6D4).copy(alpha = if (isDark) 0.22f else 0.15f))
            .border(1.dp, Color(0xFF06B6D4).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ReflexiveBadge(
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == com.wortmeister.app.ui.theme.BackgroundDark
    val textColor = if (isDark) Color(0xFF67E8F9) else Color(0xFF0E7490)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF06B6D4).copy(alpha = if (isDark) 0.22f else 0.12f))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Reflexiv",
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun PluralBadge(
    pluralForm: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 10.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Çoğul: $pluralForm",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun VerbTenseTable(
    praesens: String?,
    praeteritum: String?,
    partizipZwei: String?,
    imperativ: String?,
    hilfsverb: String? = null,
    regularity: String? = null,
    isSeparable: Boolean = false,
    separablePrefix: String? = null,
    isReflexive: Boolean = false,
    reflexiveType: String? = null,
    irregularConjugation: com.wortmeister.app.data.remote.parser.FullIrregularConjugation? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        // Table Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (irregularConjugation != null) "DÜZENSİZ FİİL ÇEKİM TABLOSU" else "FİİL ZAMANLARI & ÇEKİMLERİ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.8.sp
            )
            val regText = irregularConjugation?.vowelChange ?: regularity
            if (regText != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = regText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (isReflexive) {
            Spacer(modifier = Modifier.height(8.dp))
            ReflexiveVerbBadge(isReflexive = true, reflexiveType = reflexiveType)
        }

        if (isSeparable) {
            Spacer(modifier = Modifier.height(8.dp))
            SeparableVerbBadge(isSeparable = true, prefix = separablePrefix)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // If Irregular Conjugation is present, render all person forms!
        if (irregularConjugation != null) {
            val conj = irregularConjugation

            // Stammformen Row
            Text(
                text = "Stammformen: ${conj.infinitiv} · ${conj.praesensEr} · ${conj.praeteritumEr} · ${conj.perfektEr}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Two-column side by side table: Präsens & Präteritum
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Column 1: Präsens
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PRÄSENS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    VerbPersonLine("ich", conj.praesensIch)
                    VerbPersonLine("du", conj.praesensDu, isHighlight = true)
                    VerbPersonLine("er/sie/es", conj.praesensEr)
                    VerbPersonLine("wir", conj.praesensWir)
                    VerbPersonLine("ihr", conj.praesensIhr)
                    VerbPersonLine("sie/Sie", conj.praesensSie)
                }

                // Column 2: Präteritum
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PRÄTERITUM",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    VerbPersonLine("ich", conj.praeteritumIch)
                    VerbPersonLine("du", conj.praeteritumDu)
                    VerbPersonLine("er/sie/es", conj.praeteritumEr)
                    VerbPersonLine("wir", conj.praeteritumWir)
                    VerbPersonLine("ihr", conj.praeteritumIhr)
                    VerbPersonLine("sie/Sie", conj.praeteritumSie)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Perfekt, Konjunktiv II & Imperativ
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Konjunktiv II: ich ${conj.konjunktivZweiIch}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Imperativ: ${conj.imperativDu}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Standard 4-card layout for regular verbs
            // 1. PRIMARY ROW: Präsens (3. Sg) & Präteritum
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VerbTenseGridCard(
                    title = "Präsens (3. Sg)",
                    value = praesens.takeIf { !it.isNullOrBlank() } ?: "-",
                    icon = Icons.Default.Schedule,
                    badgeColor = Color(0xFF2563EB),
                    modifier = Modifier.weight(1f)
                )
                VerbTenseGridCard(
                    title = "Präteritum",
                    value = praeteritum.takeIf { !it.isNullOrBlank() } ?: "-",
                    icon = Icons.Default.History,
                    badgeColor = Color(0xFF7C3AED),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. PROMINENT PERFEKT CARD: Hilfsverb (haben / sein) + Partizip II
            val cleanHilfsverb = when {
                hilfsverb?.lowercase()?.contains("sein") == true || hilfsverb?.lowercase()?.contains("ist") == true -> "sein (ist)"
                hilfsverb?.lowercase()?.contains("haben") == true || hilfsverb?.lowercase()?.contains("hat") == true -> "haben (hat)"
                partizipZwei?.startsWith("ist ") == true -> "sein (ist)"
                partizipZwei?.startsWith("hat ") == true -> "haben (hat)"
                else -> if (hilfsverb.isNullOrBlank()) "haben (hat)" else hilfsverb
            }

            val fullPerfektForm = when {
                partizipZwei.isNullOrBlank() -> "-"
                partizipZwei.startsWith("ist ") || partizipZwei.startsWith("hat ") -> partizipZwei
                cleanHilfsverb.contains("sein") -> "ist $partizipZwei"
                else -> "hat $partizipZwei"
            }

            PerfektTenseCard(
                hilfsverbBadge = cleanHilfsverb,
                perfektForm = fullPerfektForm
            )

            // 3. IMPERATIV (Altta ikincil bilgi olarak)
            if (!imperativ.isNullOrBlank() && imperativ != "-") {
                Spacer(modifier = Modifier.height(8.dp))
                ImperativTenseCard(imperativ = imperativ)
            }
        }
    }
}

@Composable
private fun VerbPersonLine(
    pronoun: String,
    form: String,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = pronoun,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = form,
            fontSize = 12.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
            color = if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun VerbTenseGridCard(
    title: String,
    value: String,
    icon: ImageVector,
    badgeColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(badgeColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = badgeColor,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 16.sp,
                softWrap = true
            )
        }
    }
}

@Composable
fun PerfektTenseCard(
    hilfsverbBadge: String,
    perfektForm: String,
    modifier: Modifier = Modifier
) {
    val isSein = hilfsverbBadge.contains("sein")
    val hilfsverbColor = if (isSein) Color(0xFF0891B2) else Color(0xFF059669)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF059669).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircleOutline,
                    contentDescription = null,
                    tint = Color(0xFF059669),
                    modifier = Modifier.size(15.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Perfekt (Geçmiş Zaman)",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = perfektForm,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 17.sp,
                    softWrap = true
                )
            }
        }

        // Prominent Hilfsverb Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(hilfsverbColor.copy(alpha = 0.15f))
                .border(1.dp, hilfsverbColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = hilfsverbColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = hilfsverbBadge,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = hilfsverbColor,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

@Composable
fun ImperativTenseCard(
    imperativ: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.06f),
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 8.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFD97706).copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = Color(0xFFD97706),
                modifier = Modifier.size(13.dp)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Imperativ (Emir Kipi)",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = imperativ,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 15.sp,
                softWrap = true
            )
        }
    }
}
