package com.wortmeister.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wortmeister.app.data.local.entity.GermanCard
import kotlinx.coroutines.flow.Flow

@Dao
interface GermanCardDao {
    @Query("SELECT * FROM cards ORDER BY id DESC")
    fun getAllCards(): Flow<List<GermanCard>>

    @Query("SELECT * FROM cards WHERE deckId = :deckId ORDER BY id DESC")
    fun getCardsByDeck(deckId: Long): Flow<List<GermanCard>>

    @Query("SELECT * FROM cards WHERE id = :id LIMIT 1")
    suspend fun getCardById(id: Long): GermanCard?

    @Query("SELECT * FROM cards WHERE LOWER(germanWord) = LOWER(:word) LIMIT 1")
    suspend fun getCardByWord(word: String): GermanCard?

    @Query("SELECT * FROM cards WHERE nextReviewDate <= :currentTime ORDER BY isPriority DESC, nextReviewDate ASC")
    fun getDueCards(currentTime: Long): Flow<List<GermanCard>>

    @Query("SELECT * FROM cards WHERE deckId = :deckId AND nextReviewDate <= :currentTime ORDER BY isPriority DESC, nextReviewDate ASC")
    fun getDueCardsForDeck(deckId: Long, currentTime: Long): Flow<List<GermanCard>>

    @Query("SELECT * FROM cards WHERE isPriority = 1 ORDER BY id DESC")
    fun getPriorityCards(): Flow<List<GermanCard>>

    @Query("SELECT COUNT(*) FROM cards WHERE deckId = :deckId")
    fun getCardCountForDeck(deckId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM cards")
    fun getTotalCardCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM cards WHERE createdAt >= :start AND createdAt < :end")
    fun getCardsAddedBetween(start: Long, end: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM cards WHERE targetLanguage = :lang")
    fun getCardCountForLanguage(lang: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: GermanCard): Long

    @Update
    suspend fun updateCard(card: GermanCard): Int

    @Delete
    suspend fun deleteCard(card: GermanCard): Int

    @Query("UPDATE cards SET isPriority = :isPriority WHERE id = :cardId")
    suspend fun updatePriority(cardId: Long, isPriority: Boolean): Int

    @Query("UPDATE cards SET pronunciationScore = :score, lastSpokenText = :spokenText, lastPronouncedAt = :timestamp WHERE id = :cardId")
    suspend fun updatePronunciation(cardId: Long, score: Int, spokenText: String, timestamp: Long): Int
}
