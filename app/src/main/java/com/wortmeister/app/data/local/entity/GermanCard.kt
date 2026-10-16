package com.wortmeister.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    foreignKeys = [ForeignKey(
        entity = Deck::class,
        parentColumns = ["id"],
        childColumns = ["deckId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["deckId"])]
)
data class GermanCard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: Long,
    val germanWord: String,
    val partOfSpeech: String, // NOUN, VERB, ADJECTIVE vb.
    val article: String? = null,     // der, die, das
    val pluralForm: String? = null,
    val praesens: String? = null,
    val praeteritum: String? = null,
    val partizipZwei: String? = null,
    val imperativ: String? = null,
    val meaningsTr: String = "",   // Virgülle ayrılmış seçilen anlamlar
    val meaningsEn: String = "",
    val exampleSentence: String? = null,
    val nextReviewDate: Long = 0L,
    val intervalDays: Int = 0,
    val isPriority: Boolean = false,
    val hilfsverb: String? = null,
    val isSeparable: Boolean = false,
    val separablePrefix: String? = null,
    val frontIncludes: String = "GERMAN,GRAMMAR",
    val backIncludes: String = "TURKISH,ENGLISH,EXAMPLE",
    val targetLanguage: String = "de",
    val sourceLanguage: String = "de",
    val originalQuery: String = "",
    val pronunciationScore: Int = -1, // -1: untested, 0-100: evaluated score
    val lastSpokenText: String? = null,
    val lastPronouncedAt: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)
