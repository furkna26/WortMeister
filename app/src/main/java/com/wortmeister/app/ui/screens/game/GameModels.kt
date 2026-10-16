package com.wortmeister.app.ui.screens.game

import com.wortmeister.app.data.local.entity.Deck
import com.wortmeister.app.data.local.entity.GermanCard

enum class GameType(
    val title: String,
    val subtitle: String,
    val categoryLabel: String
) {
    ARTICLE(
        title = "Artikel Testi",
        subtitle = "der · die · das artikellerini doğru eşleştirin",
        categoryLabel = "İsim Artikelleri"
    ),
    VERB(
        title = "Fiil Testi",
        subtitle = "Zaman çekimleri, Partizip II ve anlamlar",
        categoryLabel = "Fiil Çekimleri"
    ),
    NOUN(
        title = "İsim Testi",
        subtitle = "Çoğul (Plural) formları ve Türkçe anlamlar",
        categoryLabel = "İsimler & Çoğullar"
    ),
    ADJECTIVE(
        title = "Sıfat Testi",
        subtitle = "Sıfat anlamları ve derecelendirme (Komparativ/Superlativ)",
        categoryLabel = "Sıfatlar"
    ),
    PHRASE(
        title = "Kalıp Testi",
        subtitle = "Deyimler ve Nomen-Verb-Verbindungen",
        categoryLabel = "Kalıplar & Deyimler"
    ),
    MIXED(
        title = "Karma Test",
        subtitle = "Tüm kelime türlerinden karışık pratik",
        categoryLabel = "Tüm Kelimeler"
    )
}

enum class DeckScopeOption {
    ALL_DECKS,
    SELECTED_DECKS
}

data class GameQuestion(
    val card: GermanCard,
    val questionPrompt: String,
    val questionSubject: String,
    val subPrompt: String? = null,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String? = null,
    val audioWord: String? = null
)

data class GameUiState(
    val availableDecks: List<Deck> = emptyList(),
    val selectedGameType: GameType? = null,
    val showDeckSelector: Boolean = false,
    val deckScope: DeckScopeOption = DeckScopeOption.ALL_DECKS,
    val selectedDeckIds: Set<Long> = emptySet(),
    val eligibleCardCount: Int = 0,
    val isGameActive: Boolean = false,
    val questions: List<GameQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerRevealed: Boolean = false,
    val correctAnswersCount: Int = 0,
    val wrongAnswersCount: Int = 0,
    val isGameFinished: Boolean = false
)
