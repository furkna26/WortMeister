package com.wortmeister.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "study_logs",
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["cardId"]),
        Index(value = ["rating"]),
        Index(value = ["targetLanguage"])
    ]
)
data class StudyLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardId: Long,
    val deckId: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val rating: String, // "LEARNED", "UNSURE", "PRIORITY", "PRONUNCIATION"
    val targetLanguage: String = "de",
    val score: Int = -1
)
