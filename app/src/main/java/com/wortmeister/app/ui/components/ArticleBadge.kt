package com.wortmeister.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
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
import com.wortmeister.app.ui.theme.ArticleDas
import com.wortmeister.app.ui.theme.ArticleDer
import com.wortmeister.app.ui.theme.ArticleDie

@Composable
fun ArticleBadge(
    article: String,
    modifier: Modifier = Modifier
) {
    val cleanArticle = article.lowercase().trim()
    val isDark = MaterialTheme.colorScheme.background == com.wortmeister.app.ui.theme.BackgroundDark

    val (bgColor, textColor) = when (cleanArticle) {
        "der" -> {
            val c = if (isDark) com.wortmeister.app.ui.theme.ArticleDerDark else ArticleDer
            c.copy(alpha = if (isDark) 0.22f else 0.15f) to c
        }
        "die" -> {
            val c = if (isDark) com.wortmeister.app.ui.theme.ArticleDieDark else ArticleDie
            c.copy(alpha = if (isDark) 0.22f else 0.15f) to c
        }
        "das" -> {
            val c = if (isDark) com.wortmeister.app.ui.theme.ArticleDasDark else ArticleDas
            c.copy(alpha = if (isDark) 0.22f else 0.15f) to c
        }
        else -> Color.Gray.copy(alpha = 0.15f) to Color.Gray
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = cleanArticle.uppercase(),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            softWrap = false
        )
    }
}
