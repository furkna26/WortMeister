package com.wortmeister.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wortmeister.app.data.local.entity.StudyLog
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: StudyLog): Long

    @Query("SELECT * FROM study_logs WHERE timestamp >= :start AND timestamp < :end ORDER BY timestamp DESC")
    fun getLogsBetween(start: Long, end: Long): Flow<List<StudyLog>>

    @Query("SELECT COUNT(*) FROM study_logs WHERE timestamp >= :start AND timestamp < :end AND rating = 'LEARNED'")
    fun getLearnedCountBetween(start: Long, end: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM study_logs WHERE timestamp >= :start AND timestamp < :end AND rating IN ('LEARNED', 'UNSURE', 'PRIORITY')")
    fun getTotalReviewsBetween(start: Long, end: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM study_logs WHERE timestamp >= :start AND timestamp < :end AND rating = 'PRONUNCIATION'")
    fun getPronunciationCountBetween(start: Long, end: Long): Flow<Int>

    @Query("SELECT AVG(score) FROM study_logs WHERE timestamp >= :start AND timestamp < :end AND rating = 'PRONUNCIATION' AND score >= 0")
    fun getAveragePronunciationScoreBetween(start: Long, end: Long): Flow<Float?>

    @Query("SELECT COUNT(*) FROM study_logs WHERE timestamp >= :start AND timestamp < :end AND targetLanguage = :lang AND rating IN ('LEARNED', 'UNSURE', 'PRIORITY')")
    fun getReviewsForLanguageBetween(start: Long, end: Long, lang: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM study_logs WHERE timestamp >= :start AND timestamp < :end AND targetLanguage = :lang AND rating = 'LEARNED'")
    fun getLearnedForLanguageBetween(start: Long, end: Long, lang: String): Flow<Int>

    @Query("SELECT * FROM study_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int): Flow<List<StudyLog>>

    @Query("SELECT COUNT(*) FROM study_logs")
    fun getTotalLogsCount(): Flow<Int>
}
