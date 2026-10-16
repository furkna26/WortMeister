package com.wortmeister.app.data.repository

import com.wortmeister.app.data.local.dao.DeckDao
import com.wortmeister.app.data.local.dao.GermanCardDao
import com.wortmeister.app.data.local.dao.StudyLogDao
import com.wortmeister.app.data.local.entity.Deck
import com.wortmeister.app.data.local.entity.GermanCard
import com.wortmeister.app.data.local.entity.StudyLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext

enum class SrsRating {
    LEARNED,   // Öğrendim (Aralığı uzat, nadir göster)
    UNSURE,    // Emin Değilim (Aralığı koru, oturum sonuna ekle)
    PRIORITY   // Çok Önemli (Öncelikli işaretle, sık göster)
}

class DeckRepository(
    private val deckDao: DeckDao,
    private val cardDao: GermanCardDao,
    private val studyLogDao: StudyLogDao? = null
) {
    val allDecks: Flow<List<Deck>> = deckDao.getAllDecks()
    val allCards: Flow<List<GermanCard>> = cardDao.getAllCards()
    val priorityCards: Flow<List<GermanCard>> = cardDao.getPriorityCards()
    val totalCardCount: Flow<Int> = cardDao.getTotalCardCount()

    fun getCardsByDeck(deckId: Long): Flow<List<GermanCard>> = cardDao.getCardsByDeck(deckId)

    fun getDueCards(deckId: Long? = null): Flow<List<GermanCard>> {
        val now = System.currentTimeMillis()
        return if (deckId == null || deckId <= 0L) {
            cardDao.getDueCards(now)
        } else {
            cardDao.getDueCardsForDeck(deckId, now)
        }
    }

    suspend fun createDeck(name: String): Long = withContext(Dispatchers.IO) {
        val existing = deckDao.getDeckByName(name.trim())
        if (existing != null) return@withContext existing.id
        deckDao.insertDeck(Deck(name = name.trim()))
    }

    suspend fun deleteDeck(deck: Deck) = withContext(Dispatchers.IO) {
        deckDao.deleteDeck(deck)
    }

    suspend fun saveCard(card: GermanCard): Long = withContext(Dispatchers.IO) {
        cardDao.insertCard(card)
    }

    suspend fun deleteCard(card: GermanCard) = withContext(Dispatchers.IO) {
        cardDao.deleteCard(card)
    }

    suspend fun toggleCardPriority(cardId: Long, currentPriority: Boolean) = withContext(Dispatchers.IO) {
        cardDao.updatePriority(cardId, !currentPriority)
    }

    suspend fun recordPronunciation(cardId: Long, score: Int, spokenText: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        cardDao.updatePronunciation(cardId, score, spokenText, now)
        val card = cardDao.getCardById(cardId)
        studyLogDao?.insertLog(
            StudyLog(
                cardId = cardId,
                deckId = card?.deckId ?: 0L,
                timestamp = now,
                rating = "PRONUNCIATION",
                targetLanguage = card?.targetLanguage ?: "de",
                score = score
            )
        )
    }

    suspend fun applySrsReview(card: GermanCard, rating: SrsRating): GermanCard = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val oneDayMillis = 24L * 60 * 60 * 1000
        val updatedCard = when (rating) {
            SrsRating.LEARNED -> {
                val newInterval = when (card.intervalDays) {
                    0 -> 1
                    1 -> 3
                    3 -> 7
                    7 -> 14
                    14 -> 30
                    else -> card.intervalDays * 2
                }
                card.copy(
                    intervalDays = newInterval,
                    nextReviewDate = now + newInterval * oneDayMillis
                )
            }
            SrsRating.UNSURE -> {
                card.copy(
                    intervalDays = 1,
                    nextReviewDate = now + oneDayMillis
                )
            }
            SrsRating.PRIORITY -> {
                card.copy(
                    isPriority = true,
                    intervalDays = 0,
                    nextReviewDate = now + (4L * 60 * 60 * 1000) // 4 hours
                )
            }
        }
        cardDao.updateCard(updatedCard)
        studyLogDao?.insertLog(
            StudyLog(
                cardId = card.id,
                deckId = card.deckId,
                timestamp = now,
                rating = rating.name,
                targetLanguage = card.targetLanguage
            )
        )
        updatedCard
    }

    // Statistics Queries
    fun getLearnedCountBetween(start: Long, end: Long): Flow<Int> {
        return studyLogDao?.getLearnedCountBetween(start, end) ?: flowOf(0)
    }

    fun getCardsAddedBetween(start: Long, end: Long): Flow<Int> {
        return cardDao.getCardsAddedBetween(start, end)
    }

    fun getTotalReviewsBetween(start: Long, end: Long): Flow<Int> {
        return studyLogDao?.getTotalReviewsBetween(start, end) ?: flowOf(0)
    }

    fun getPronunciationCountBetween(start: Long, end: Long): Flow<Int> {
        return studyLogDao?.getPronunciationCountBetween(start, end) ?: flowOf(0)
    }

    fun getAveragePronunciationScoreBetween(start: Long, end: Long): Flow<Float?> {
        return studyLogDao?.getAveragePronunciationScoreBetween(start, end) ?: flowOf(null)
    }

    fun getReviewsForLanguageBetween(start: Long, end: Long, lang: String): Flow<Int> {
        return studyLogDao?.getReviewsForLanguageBetween(start, end, lang) ?: flowOf(0)
    }

    fun getLearnedForLanguageBetween(start: Long, end: Long, lang: String): Flow<Int> {
        return studyLogDao?.getLearnedForLanguageBetween(start, end, lang) ?: flowOf(0)
    }

    fun getCardCountForLanguage(lang: String): Flow<Int> {
        return cardDao.getCardCountForLanguage(lang)
    }

    suspend fun backfillLogsIfEmpty() = withContext(Dispatchers.IO) {
        val totalLogs = studyLogDao?.getTotalLogsCount()?.first() ?: 0
        if (totalLogs == 0) {
            val allExisting = cardDao.getAllCards().first()
            val now = System.currentTimeMillis()
            allExisting.forEach { card ->
                if (card.intervalDays > 0) {
                    studyLogDao?.insertLog(
                        StudyLog(
                            cardId = card.id,
                            deckId = card.deckId,
                            timestamp = now - (card.intervalDays * 12L * 60 * 60 * 1000), // Approximate timestamp
                            rating = "LEARNED",
                            targetLanguage = card.targetLanguage
                        )
                    )
                }
            }
        }
    }
}
