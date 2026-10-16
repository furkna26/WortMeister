package com.wortmeister.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.wortmeister.app.ui.theme.PlusJakartaSans

@Composable
fun WortMeisterLogoHeader(
    themeMode: String = "SYSTEM",
    onToggleTheme: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Wort",
                    fontSize = 24.sp,
                    fontFamily = com.wortmeister.app.ui.theme.PlusJakartaSans,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Meister",
                    fontSize = 24.sp,
                    fontFamily = com.wortmeister.app.ui.theme.PlusJakartaSans,
                    fontWeight = FontWeight.Black,
                    color = androidx.compose.ui.graphics.Color(0xFF2563EB),
                    letterSpacing = (-0.5).sp
                )
            }
            Text(
                text = "Akıllı Sözlük & Morfoloji Motoru",
                fontSize = 11.sp,
                fontFamily = com.wortmeister.app.ui.theme.PlusJakartaSans,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.2.sp
            )
        }

        IconButton(
            onClick = onToggleTheme,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            val (icon, desc) = when (themeMode) {
                "DARK" -> Icons.Default.DarkMode to "Karanlık Mod Açık"
                "LIGHT" -> Icons.Default.LightMode to "Aydınlık Mod Açık"
                else -> Icons.Default.SettingsBrightness to "Sistem Teması"
            }
            Icon(
                imageVector = icon,
                contentDescription = desc,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
