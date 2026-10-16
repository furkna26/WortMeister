package com.wortmeister.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size

fun getLanguageFlag(lang: String): String {
    return when (lang.lowercase()) {
        "tr", "turkish" -> "🇹🇷"
        "de", "german" -> "🇩🇪"
        "en", "english", "gb" -> "🇬🇧"
        else -> "🌐"
    }
}

fun getLanguageCode(lang: String): String {
    return when (lang.lowercase()) {
        "tr", "turkish" -> "TR"
        "de", "german" -> "DE"
        "en", "english", "gb" -> "EN"
        else -> "AUTO"
    }
}

fun getLanguageTitle(lang: String): String {
    return when (lang.lowercase()) {
        "tr" -> "Türkçe"
        "de" -> "Almanca"
        "en" -> "İngilizce"
        else -> "Otomatik"
    }
}

@Composable
fun VectorFlagDE(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .size(width = 15.dp, height = 10.dp)
            .clip(RoundedCornerShape(2.dp))
            .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
    ) {
        val h = size.height / 3f
        drawRect(Color(0xFF1E1E1E), androidx.compose.ui.geometry.Offset(0f, 0f), androidx.compose.ui.geometry.Size(size.width, h))
        drawRect(Color(0xFFDD0000), androidx.compose.ui.geometry.Offset(0f, h), androidx.compose.ui.geometry.Size(size.width, h))
        drawRect(Color(0xFFFFCC00), androidx.compose.ui.geometry.Offset(0f, h * 2f), androidx.compose.ui.geometry.Size(size.width, h))
    }
}

@Composable
fun VectorFlagTR(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .size(width = 15.dp, height = 10.dp)
            .clip(RoundedCornerShape(2.dp))
            .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
    ) {
        drawRect(Color(0xFFE30A17))
        // White Crescent & Star
        drawCircle(Color.White, radius = size.height * 0.32f, center = androidx.compose.ui.geometry.Offset(size.width * 0.42f, size.height * 0.5f))
        drawCircle(Color(0xFFE30A17), radius = size.height * 0.25f, center = androidx.compose.ui.geometry.Offset(size.width * 0.47f, size.height * 0.5f))
        drawCircle(Color.White, radius = size.height * 0.10f, center = androidx.compose.ui.geometry.Offset(size.width * 0.68f, size.height * 0.5f))
    }
}

@Composable
fun VectorFlagEN(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .size(width = 15.dp, height = 10.dp)
            .clip(RoundedCornerShape(2.dp))
            .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
    ) {
        drawRect(Color(0xFF012169))
        val midX = size.width / 2f
        val midY = size.height / 2f
        val wCross = size.width * 0.30f
        val hCross = size.height * 0.30f
        // White Cross
        drawRect(Color.White, androidx.compose.ui.geometry.Offset(midX - wCross / 2f, 0f), androidx.compose.ui.geometry.Size(wCross, size.height))
        drawRect(Color.White, androidx.compose.ui.geometry.Offset(0f, midY - hCross / 2f), androidx.compose.ui.geometry.Size(size.width, hCross))
        // Red Cross
        val wRed = size.width * 0.18f
        val hRed = size.height * 0.18f
        drawRect(Color(0xFFC8102E), androidx.compose.ui.geometry.Offset(midX - wRed / 2f, 0f), androidx.compose.ui.geometry.Size(wRed, size.height))
        drawRect(Color(0xFFC8102E), androidx.compose.ui.geometry.Offset(0f, midY - hRed / 2f), androidx.compose.ui.geometry.Size(size.width, hRed))
    }
}

@Composable
fun LanguageBadge(
    lang: String,
    label: String? = null,
    showFlag: Boolean = true,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == com.wortmeister.app.ui.theme.BackgroundDark
    val code = getLanguageCode(lang)
    val (bgColor, textColor) = when (lang.lowercase()) {
        "tr" -> Color(0xFFE11D48).copy(alpha = if (isDark) 0.18f else 0.08f) to if (isDark) Color(0xFFFB7185) else Color(0xFFBE123C)
        "de" -> Color(0xFF4F46E5).copy(alpha = if (isDark) 0.18f else 0.08f) to if (isDark) Color(0xFF818CF8) else Color(0xFF4338CA)
        "en", "gb" -> Color(0xFF2563EB).copy(alpha = if (isDark) 0.18f else 0.08f) to if (isDark) Color(0xFF60A5FA) else Color(0xFF1D4ED8)
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (showFlag) {
                when (lang.lowercase()) {
                    "de", "german" -> VectorFlagDE()
                    "tr", "turkish" -> VectorFlagTR()
                    "en", "english", "gb" -> VectorFlagEN()
                    else -> {}
                }
            }
            Text(
                text = label ?: code,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun SearchSourceBanner(
    sourceLang: String,
    searchedTerm: String,
    targetGerman: String,
    modifier: Modifier = Modifier
) {
    val cleanSearched = searchedTerm.trim().lowercase().replace(Regex("^(der|die|das)\\s+"), "")
    val cleanTarget = targetGerman.trim().lowercase().replace(Regex("^(der|die|das)\\s+"), "")
    if (sourceLang == "de" && cleanSearched == cleanTarget) {
        return
    }

    val sourceCode = getLanguageCode(sourceLang)
    val germanCode = getLanguageCode("de")

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "[$sourceCode]",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "„$searchedTerm“",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "→",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "[$germanCode]",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = targetGerman,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun AutoCorrectionBanner(
    correctedFrom: String,
    correctedTo: String,
    modifier: Modifier = Modifier
) {
    val cleanFrom = correctedFrom.trim().lowercase().replace(Regex("^(der|die|das)\\s+"), "")
    val cleanTo = correctedTo.trim().lowercase().replace(Regex("^(der|die|das)\\s+"), "")
    if (cleanFrom == cleanTo) {
        return
    }

    val isDark = MaterialTheme.colorScheme.background == com.wortmeister.app.ui.theme.BackgroundDark
    val textColor = if (isDark) Color(0xFFFCD34D) else Color(0xFFB45309)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF59E0B).copy(alpha = if (isDark) 0.18f else 0.08f))
            .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "DÜZELTME:",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "„$correctedFrom“ yerine „$correctedTo“ gösteriliyor",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = textColor
            )
        }
    }
}

@Composable
fun RootLemmaBanner(
    lemmaInfo: String,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == com.wortmeister.app.ui.theme.BackgroundDark
    val textColor = if (isDark) Color(0xFFC4B5FD) else Color(0xFF6D28D9)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF4F46E5).copy(alpha = if (isDark) 0.18f else 0.08f))
            .border(1.dp, Color(0xFF4F46E5).copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "KÖK BİLGİSİ:",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = lemmaInfo,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = textColor
            )
        }
    }
}


