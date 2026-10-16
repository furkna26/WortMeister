package com.wortmeister.app.ui.screens.about

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.wortmeister.app.data.local.preferences.CardPreferencesManager
import com.wortmeister.app.data.local.seed.CuratedDecksSeeder
import com.wortmeister.app.data.remote.api.TranslationApi
import com.wortmeister.app.data.repository.DeckRepository
import kotlinx.coroutines.launch

@Composable
fun AboutScreen(
    deckRepository: DeckRepository,
    cardPreferencesManager: CardPreferencesManager? = null,
    onResetOnboarding: () -> Unit,
    onOpenStatistics: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ÖĞRENME İSTATİSTİKLERİM KARTI (Compact)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenStatistics() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = "İstatistiklerim",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Öğrenme İstatistiklerim",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Zaman aralıklı analiz, öğrenilenler ve artış/düşüş trendi",
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "Görüntüle",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // 2. DEEPL TRANSLATION API (500k Karakter/Ay Ücretsiz)
            var apiKeyInput by remember {
                mutableStateOf(cardPreferencesManager?.customDeepLApiKey ?: "")
            }
            var isTesting by remember { mutableStateOf(false) }
            var testStatusMessage by remember { mutableStateOf<String?>(null) }

            MinimalSectionCard(
                title = "DeepL Çeviri API",
                icon = Icons.Default.Language,
                color = Color(0xFF0F766E)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Uygulama arka planda hazır entegre DeepL API motoruyla çalışır. Kendi özel DeepL Free API anahtarınızı tanımlamak isterseniz buraya girebilirsiniz.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )

                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it.trim() },
                        label = { Text("Özel DeepL Auth Key (İsteğe Bağlı)") },
                        placeholder = { Text("Varsayılan sistem anahtarı devrede") },
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                cardPreferencesManager?.deepLApiKey = apiKeyInput.trim()
                                Toast.makeText(context, "DeepL anahtarı kaydedildi.", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Kaydet", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                val effectiveKey = if (apiKeyInput.isNotBlank()) apiKeyInput.trim() else (cardPreferencesManager?.deepLApiKey ?: "")
                                if (effectiveKey.isBlank()) {
                                    Toast.makeText(context, "Lütfen önce bir anahtar girin.", Toast.LENGTH_SHORT).show()
                                    return@OutlinedButton
                                }
                                isTesting = true
                                testStatusMessage = null
                                coroutineScope.launch {
                                    try {
                                        val testRes = TranslationApi().translateViaDeepL(
                                            text = "Hallo Welt",
                                            targetLang = "tr",
                                            apiKey = effectiveKey
                                        )
                                        if (testRes != null) {
                                            testStatusMessage = "Bağlantı başarılı: $testRes"
                                            Toast.makeText(context, "DeepL API başarıyla doğrulandı!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            testStatusMessage = "Bağlantı başarısız. Anahtarınızı kontrol edin."
                                        }
                                    } catch (e: Exception) {
                                        testStatusMessage = "Hata: ${e.localizedMessage}"
                                    } finally {
                                        isTesting = false
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !isTesting
                        ) {
                            Text(if (isTesting) "Test Ediliyor..." else "Bağlantıyı Test Et", fontSize = 11.sp, maxLines = 1)
                        }
                    }

                    testStatusMessage?.let { msg ->
                        Text(
                            text = msg,
                            fontSize = 11.sp,
                            color = if (msg.startsWith("Bağlantı başarılı")) Color(0xFF10B981) else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 3. DATA SOURCES (KULLANILAN VERİ KAYNAKLARI)
            MinimalSectionCard(
                title = "Veri Kaynakları",
                icon = Icons.Default.Storage,
                color = Color(0xFF2563EB)
            ) {
                SourceRow(
                    title = "Wiktionary & Kaikki",
                    badge = "CC BY-SA 4.0",
                    desc = "Kelime anlamları, çoğullar ve çekim tabloları",
                    url = "https://de.wiktionary.org",
                    onOpen = { uriHandler.openUri(it) }
                )
                SourceRow(
                    title = "Tatoeba Projesi",
                    badge = "CC BY 2.0 FR",
                    desc = "Çift dilli örnek cümle arşivi",
                    url = "https://tatoeba.org",
                    onOpen = { uriHandler.openUri(it) }
                )
                SourceRow(
                    title = "DWDS & Verbformen",
                    badge = "Referans",
                    desc = "Fiil zamanları ve morfolojik çekim modelleri",
                    url = "https://www.dwds.de",
                    onOpen = { uriHandler.openUri(it) }
                )
                SourceRow(
                    title = "Redensarten-Index",
                    badge = "Deyimler",
                    desc = "Almanca deyimler ve kalıp fiil birleşikleri",
                    url = "https://www.redensarten-index.de",
                    onOpen = { uriHandler.openUri(it) }
                )
            }

            // 3. OPEN SOURCE LIBRARIES (KÜTÜPHANELER)
            MinimalSectionCard(
                title = "Kullanılan Kütüphaneler",
                icon = Icons.Default.Code,
                color = Color(0xFF7C3AED)
            ) {
                val libs = listOf(
                    "Jetpack Compose & Material 3" to "Apache 2.0",
                    "Room SQLite Database" to "Apache 2.0",
                    "Kotlin Coroutines & Flow" to "Apache 2.0",
                    "Square OkHttp & Retrofit" to "Apache 2.0",
                    "Jsoup HTML Parser" to "MIT License"
                )
                libs.forEach { (name, lic) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = name, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = lic, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            // 4. LEGAL & PRIVACY (GİZLİLİK VE LİSANS)
            MinimalSectionCard(
                title = "Gizlilik & Lisans",
                icon = Icons.Default.VerifiedUser,
                color = Color(0xFF10B981)
            ) {
                Text(
                    text = "• Veri Kaynakları: Açık sözlük ve korpus verileri Creative Commons (CC BY-SA / CC BY) lisanslama şartlarına tabidir.\n" +
                           "• Gizlilik: Oluşturduğunuz kartlar, desteler ve çalışma verileri cihazınızda yerel SQLite Room veri tabanında saklanır.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }

            // 5. ACTIONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onResetOnboarding,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Kurulumu Yenile", fontSize = 12.sp, maxLines = 1, softWrap = false)
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val count = CuratedDecksSeeder.seedAllCuratedDecks(deckRepository)
                            Toast.makeText(
                                context,
                                "Tüm seviye desteleri yüklendi! ($count kart)",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Desteleri Yükle", fontSize = 12.sp, maxLines = 1, softWrap = false)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun MinimalSectionCard(
    title: String,
    icon: ImageVector,
    color: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(17.dp)
                )
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            content()
        }
    }
}

@Composable
fun SourceRow(
    title: String,
    badge: String,
    desc: String,
    url: String,
    onOpen: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onOpen(url) },
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = desc,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}
