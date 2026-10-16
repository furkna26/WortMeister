package com.wortmeister.app.util

import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wortmeister.app.data.local.entity.Deck
import com.wortmeister.app.data.local.entity.GermanCard
import com.wortmeister.app.data.repository.DeckRepository

data class SharedCardDto(
    val germanWord: String,
    val partOfSpeech: String = "OTHER",
    val article: String? = null,
    val pluralForm: String? = null,
    val praesens: String? = null,
    val praeteritum: String? = null,
    val partizipZwei: String? = null,
    val imperativ: String? = null,
    val hilfsverb: String? = null,
    val isSeparable: Boolean = false,
    val separablePrefix: String? = null,
    val meaningsTr: String = "",
    val meaningsEn: String = "",
    val exampleSentence: String? = null
)

data class ImportResult(
    val deckName: String,
    val cardCount: Int
)

object DeckShareManager {

    private val gson = Gson()
    private const val TAG_START = "--- WORTMEISTER_DECK_EXPORT ---"
    private const val TAG_END = "--- END_WORTMEISTER_DECK ---"

    fun shareDeck(context: Context, deck: Deck, cards: List<GermanCard>) {
        val dtoList = cards.map {
            SharedCardDto(
                germanWord = it.germanWord,
                partOfSpeech = it.partOfSpeech,
                article = it.article,
                pluralForm = it.pluralForm,
                praesens = it.praesens,
                praeteritum = it.praeteritum,
                partizipZwei = it.partizipZwei,
                imperativ = it.imperativ,
                hilfsverb = it.hilfsverb,
                isSeparable = it.isSeparable,
                separablePrefix = it.separablePrefix,
                meaningsTr = it.meaningsTr,
                meaningsEn = it.meaningsEn,
                exampleSentence = it.exampleSentence
            )
        }

        val json = gson.toJson(dtoList)

        val shareText = buildString {
            appendLine("WortMeister Deste Paylaşımı: „${deck.name}“ (${cards.size} Kart)")
            appendLine("Bu desteyi WortMeister uygulamasında „Desteyi İçe Aktar“ butonuna yapıştırarak hemen kullanmaya başlayabilirsiniz.")
            appendLine()
            appendLine(TAG_START)
            appendLine(json)
            appendLine(TAG_END)
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, "WortMeister Deste: ${deck.name}")
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Desteyi Paylaş (WhatsApp, Telegram vb.)")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    suspend fun importDeck(
        rawText: String,
        repository: DeckRepository,
        fallbackDeckName: String = "İçe Aktarılan Deste"
    ): Result<ImportResult> {
        val trimmed = rawText.trim()
        if (trimmed.isBlank()) {
            return Result.failure(IllegalArgumentException("Metin boş olamaz."))
        }

        // 1. Try to find deck name from header
        var deckName = fallbackDeckName
        val headerMatch = Regex("""WortMeister Deste Paylaşımı:\s*[„"'](.+?)[”"']""").find(trimmed)
        if (headerMatch != null) {
            val extractedName = headerMatch.groupValues[1].trim()
            if (extractedName.isNotBlank()) deckName = extractedName
        }

        // 2. Extract JSON payload
        val jsonPayload = if (trimmed.contains(TAG_START) && trimmed.contains(TAG_END)) {
            trimmed.substringAfter(TAG_START).substringBefore(TAG_END).trim()
        } else {
            // Check if user pasted pure JSON or raw text
            trimmed
        }

        return try {
            val type = object : TypeToken<List<SharedCardDto>>() {}.type
            val cardDtos: List<SharedCardDto> = gson.fromJson(jsonPayload, type)

            if (cardDtos.isEmpty()) {
                return Result.failure(IllegalStateException("Destede kart bulunamadı."))
            }

            // Create new deck in Room
            val deckId = repository.createDeck(deckName)

            // Insert cards
            for (dto in cardDtos) {
                val card = GermanCard(
                    deckId = deckId,
                    germanWord = dto.germanWord,
                    partOfSpeech = dto.partOfSpeech,
                    article = dto.article,
                    pluralForm = dto.pluralForm,
                    praesens = dto.praesens,
                    praeteritum = dto.praeteritum,
                    partizipZwei = dto.partizipZwei,
                    imperativ = dto.imperativ,
                    hilfsverb = dto.hilfsverb,
                    isSeparable = dto.isSeparable,
                    separablePrefix = dto.separablePrefix,
                    meaningsTr = dto.meaningsTr,
                    meaningsEn = dto.meaningsEn,
                    exampleSentence = dto.exampleSentence,
                    nextReviewDate = System.currentTimeMillis()
                )
                repository.saveCard(card)
            }

            Result.success(ImportResult(deckName = deckName, cardCount = cardDtos.size))
        } catch (e: Exception) {
            Result.failure(Exception("Deste verisi çözümlenemedi. Lütfen paylaşılan metnin tamamını yapıştırdığınızdan emin olun.", e))
        }
    }
}
