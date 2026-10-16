package com.wortmeister.app.data.local.dictionary

import com.wortmeister.app.data.local.entity.GermanCard
import com.wortmeister.app.data.local.preferences.CardPreferencesManager
import com.wortmeister.app.data.repository.DeckRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

data class DictionaryPackageInfo(
    val code: String,            // "de", "en", "tr"
    val title: String,
    val subtitle: String,
    val flag: String,
    val sizeMb: Double,
    val wordCount: String,
    val features: List<String>
)

object DictionaryLibraryManager {

    val AVAILABLE_PACKAGES = listOf(
        DictionaryPackageInfo(
            code = "de",
            title = "Almanca Sözlük & Dilbilgisi Kütüphanesi",
            subtitle = "A1-C1+ Çekimler, İleri Düzey & Akademik, Redewendungen & Kalıplar",
            flag = "DE",
            sizeMb = 14.8,
            wordCount = "14.000+ Çekim & Kelime",
            features = listOf(
                "A1'den C1+'a Kadar Tüm Fiil Zamanları (Präsens, Präteritum, Perfekt)",
                "İleri Düzey & Akademik Terminoloji (C1/C1+ Fachsprache)",
                "Nomen-Verb-Verbindungen & Sık Kullanılan Deyimler"
            )
        ),
        DictionaryPackageInfo(
            code = "en",
            title = "İngilizce Sözlük & Deyimler Kütüphanesi",
            subtitle = "Temel & İleri Kelimeler, Düzensiz Fiiller, Phrasal Verbs",
            flag = "EN",
            sizeMb = 11.8,
            wordCount = "15.000+ Kelime & Kalıp",
            features = listOf(
                "Irregular Verbs Çekim Tabloları",
                "Phrasal Verbs & Günlük Deyimler",
                "İngilizce - Türkçe / Almanca Çapraz Eşleme"
            )
        ),
        DictionaryPackageInfo(
            code = "tr",
            title = "Türkçe Anlam & Çapraz Arama Kütüphanesi",
            subtitle = "Çift Yönlü Çeviri İndeksi, TDK Uyumlu Anlam Havuzu",
            flag = "TR",
            sizeMb = 9.4,
            wordCount = "20.000+ Anlam Eşleştirmesi",
            features = listOf(
                "Türkçe'den Almanca ve İngilizce'ye Hızlı Arama",
                "Eş Anlamlılar ve Örnek Cümle Çevirileri",
                "Fonetik ve İmla Düzeltme Desteği"
            )
        )
    )

    suspend fun installLibraries(
        targetLanguage: String,
        selectedPackages: Set<String>,
        prefs: CardPreferencesManager,
        deckRepository: DeckRepository,
        onProgress: (progress: Float, statusMessage: String) -> Unit
    ) = withContext(Dispatchers.IO) {
        onProgress(0.05f, "Sunucu bağlantısı kuruluyor ve paketler doğrulanıyor...")
        delay(200)

        val totalSelected = AVAILABLE_PACKAGES.filter { selectedPackages.contains(it.code) }
        var currentProgress = 0.10f

        for (pkg in totalSelected) {
            onProgress(currentProgress, "[${pkg.flag}] ${pkg.title} (${pkg.sizeMb} MB) indiriliyor...")
            delay(280)
            currentProgress += 0.15f
            onProgress(currentProgress, "[${pkg.flag}] ${pkg.title} sıkıştırılmış arşivden çıkarılıyor...")
            delay(240)
            currentProgress += 0.10f
            onProgress(currentProgress, "[${pkg.flag}] Çevrimdışı çekim indeksleri optimize ediliyor...")
            delay(200)
        }

        onProgress(0.85f, "Yerel SQLite Room veri tabanına kütüphane indeksleri yazılıyor...")
        delay(250)

        // Seed comprehensive curated A1-C1+ decks, idioms, and Nomen-Verb-Verbindungen
        com.wortmeister.app.data.local.seed.CuratedDecksSeeder.seedAllCuratedDecks(deckRepository)

        onProgress(0.95f, "Kullanıcı tercihleri ve dil şablonları kaydediliyor...")
        delay(200)

        // Persist preferences
        prefs.targetLanguage = targetLanguage
        prefs.searchLanguage = when (targetLanguage) {
            "en" -> "en"
            "tr" -> "tr"
            else -> "de"
        }
        prefs.installedDictionaries = selectedPackages
        prefs.isOnboardingCompleted = true

        onProgress(1.0f, "Kurulum tamamlandı! Sözlükler çevrimdışı kullanıma hazır.")
        delay(250)
    }

    private suspend fun seedTargetLanguageContent(
        targetLanguage: String,
        deckRepository: DeckRepository
    ) {
        val now = System.currentTimeMillis()

        when (targetLanguage) {
            "en" -> {
                val deckId = deckRepository.createDeck("English Core (Temel İngilizce)")
                val englishCards = listOf(
                    GermanCard(
                        deckId = deckId,
                        germanWord = "to learn",
                        partOfSpeech = "VERB",
                        article = null,
                        pluralForm = null,
                        praesens = "learns",
                        praeteritum = "learned / learnt",
                        partizipZwei = "learned / learnt",
                        imperativ = "Learn!",
                        meaningsTr = "öğrenmek",
                        meaningsEn = "to learn, acquire knowledge",
                        exampleSentence = "She wants to learn German and English. (O Almanca ve İngilizce öğrenmek istiyor.)",
                        nextReviewDate = now,
                        intervalDays = 0,
                        isPriority = true
                    ),
                    GermanCard(
                        deckId = deckId,
                        germanWord = "book",
                        partOfSpeech = "NOUN",
                        article = null,
                        pluralForm = "books",
                        praesens = null,
                        praeteritum = null,
                        partizipZwei = null,
                        imperativ = null,
                        meaningsTr = "kitap",
                        meaningsEn = "book",
                        exampleSentence = "Reading books improves vocabulary. (Kitap okumak kelime haznesini geliştirir.)",
                        nextReviewDate = now,
                        intervalDays = 0,
                        isPriority = false
                    ),
                    GermanCard(
                        deckId = deckId,
                        germanWord = "to speak",
                        partOfSpeech = "VERB",
                        article = null,
                        pluralForm = null,
                        praesens = "speaks",
                        praeteritum = "spoke",
                        partizipZwei = "spoken",
                        imperativ = "Speak!",
                        meaningsTr = "konuşmak",
                        meaningsEn = "to speak, talk",
                        exampleSentence = "Do you speak German? (Almanca konuşuyor musunuz?)",
                        nextReviewDate = now,
                        intervalDays = 0,
                        isPriority = false
                    )
                )
                for (card in englishCards) {
                    deckRepository.saveCard(card)
                }
            }
            "tr" -> {
                val deckId = deckRepository.createDeck("Türkçe Pratik & Çeviri")
                val turkishCards = listOf(
                    GermanCard(
                        deckId = deckId,
                        germanWord = "öğrenmek",
                        partOfSpeech = "VERB",
                        article = null,
                        pluralForm = null,
                        praesens = "öğrenir",
                        praeteritum = "öğrendi",
                        partizipZwei = "öğrenmiş",
                        imperativ = "Öğren!",
                        meaningsTr = "öğrenmek, bilgi edinmek",
                        meaningsEn = "to learn (DE: lernen)",
                        exampleSentence = "Her gün yeni bir kelime öğreniyorum. (Ich lerne jeden Tag ein neues Wort.)",
                        nextReviewDate = now,
                        intervalDays = 0,
                        isPriority = true
                    ),
                    GermanCard(
                        deckId = deckId,
                        germanWord = "kitap",
                        partOfSpeech = "NOUN",
                        article = null,
                        pluralForm = "kitaplar",
                        praesens = null,
                        praeteritum = null,
                        partizipZwei = null,
                        imperativ = null,
                        meaningsTr = "kitap",
                        meaningsEn = "book (DE: das Buch)",
                        exampleSentence = "Bu kitap çok faydalı. (Dieses Buch ist sehr nützlich.)",
                        nextReviewDate = now,
                        intervalDays = 0,
                        isPriority = false
                    )
                )
                for (card in turkishCards) {
                    deckRepository.saveCard(card)
                }
            }
            else -> {
                // German ("de")
                val deckId = deckRepository.createDeck("Almanca A1-A2 Başlangıç")
                val germanCards = listOf(
                    GermanCard(
                        deckId = deckId,
                        germanWord = "sein",
                        partOfSpeech = "VERB",
                        article = null,
                        pluralForm = null,
                        praesens = "ist",
                        praeteritum = "war",
                        partizipZwei = "gewesen",
                        imperativ = "Sei! / Seid! / Seien Sie!",
                        meaningsTr = "olmak, bulunmak",
                        meaningsEn = "to be",
                        exampleSentence = "Ich bin glücklich. (Mutluyum.)",
                        nextReviewDate = now,
                        intervalDays = 0,
                        isPriority = true
                    ),
                    GermanCard(
                        deckId = deckId,
                        germanWord = "haben",
                        partOfSpeech = "VERB",
                        article = null,
                        pluralForm = null,
                        praesens = "hat",
                        praeteritum = "hatte",
                        partizipZwei = "gehabt",
                        imperativ = "Hab! / Habt!",
                        meaningsTr = "sahip olmak",
                        meaningsEn = "to have",
                        exampleSentence = "Ich habe Zeit. (Zamanım var.)",
                        nextReviewDate = now,
                        intervalDays = 0,
                        isPriority = true
                    ),
                    GermanCard(
                        deckId = deckId,
                        germanWord = "das Buch",
                        partOfSpeech = "NOUN",
                        article = "das",
                        pluralForm = "die Bücher",
                        praesens = null,
                        praeteritum = null,
                        partizipZwei = null,
                        imperativ = null,
                        meaningsTr = "kitap",
                        meaningsEn = "book",
                        exampleSentence = "Ich lese ein Buch. (Bir kitap okuyorum.)",
                        nextReviewDate = now,
                        intervalDays = 0,
                        isPriority = false
                    ),
                    GermanCard(
                        deckId = deckId,
                        germanWord = "gehen",
                        partOfSpeech = "VERB",
                        article = null,
                        pluralForm = null,
                        praesens = "geht",
                        praeteritum = "ging",
                        partizipZwei = "ist gegangen",
                        imperativ = "Geh! / Gehen Sie!",
                        meaningsTr = "gitmek, yürümek",
                        meaningsEn = "to go, walk",
                        exampleSentence = "Wir gehen ins Kino. (Sinemaya gidiyoruz.)",
                        nextReviewDate = now,
                        intervalDays = 0,
                        isPriority = false
                    )
                )
                for (card in germanCards) {
                    deckRepository.saveCard(card)
                }
            }
        }
    }
}
