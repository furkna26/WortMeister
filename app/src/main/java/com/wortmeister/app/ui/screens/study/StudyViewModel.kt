package com.wortmeister.app.ui.screens.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wortmeister.app.data.local.entity.Deck
import com.wortmeister.app.data.local.entity.GermanCard
import com.wortmeister.app.data.repository.DeckRepository
import com.wortmeister.app.data.repository.SrsRating
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudyUiState(
    val decks: List<Deck> = emptyList(),
    val selectedDeckId: Long? = null, // null means "Tüm Kartlar"
    val sessionQueue: List<GermanCard> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isSessionComplete: Boolean = false,
    val learnedCount: Int = 0,
    val unsureCount: Int = 0,
    val priorityCount: Int = 0,
    val isLoading: Boolean = false,
    val gamificationStats: com.wortmeister.app.data.local.preferences.GamificationStats? = null
)

class StudyViewModel(
    private val deckRepository: DeckRepository,
    private val streakManager: com.wortmeister.app.data.local.preferences.StreakManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudyUiState(gamificationStats = streakManager.getStats()))
    val uiState: StateFlow<StudyUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(gamificationStats = streakManager.getStats()) }
        viewModelScope.launch {
            deckRepository.allDecks.collect { deckList ->
                _uiState.update { it.copy(decks = deckList) }
            }
        }
        loadSessionCards(null)
    }

    fun selectDeck(deckId: Long?) {
        _uiState.update { it.copy(selectedDeckId = deckId) }
        loadSessionCards(deckId)
    }

    fun loadSessionCards(deckId: Long?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val cards = if (deckId == null || deckId <= 0L) {
                deckRepository.allCards.first()
            } else {
                deckRepository.getCardsByDeck(deckId).first()
            }

            _uiState.update {
                it.copy(
                    sessionQueue = cards.shuffled(),
                    currentIndex = 0,
                    isFlipped = false,
                    isSessionComplete = cards.isEmpty(),
                    learnedCount = 0,
                    unsureCount = 0,
                    priorityCount = 0,
                    isLoading = false
                )
            }
        }
    }

    fun flipCard() {
        _uiState.update { it.copy(isFlipped = !it.isFlipped) }
    }

    fun answerCard(rating: SrsRating) {
        val state = _uiState.value
        if (state.currentIndex >= state.sessionQueue.size) return

        val currentCard = state.sessionQueue[state.currentIndex]

        viewModelScope.launch {
            deckRepository.applySrsReview(currentCard, rating)
            streakManager.recordReview(isLearned = (rating == SrsRating.LEARNED))
        }

        val mutableQueue = state.sessionQueue.toMutableList()
        var newLearned = state.learnedCount
        var newUnsure = state.unsureCount
        var newPriority = state.priorityCount

        when (rating) {
            SrsRating.LEARNED -> newLearned++
            SrsRating.UNSURE -> {
                newUnsure++
                // Re-add to end of session
                mutableQueue.add(currentCard)
            }
            SrsRating.PRIORITY -> newPriority++
        }

        val nextIndex = state.currentIndex + 1
        val isComplete = nextIndex >= mutableQueue.size

        _uiState.update {
            it.copy(
                sessionQueue = mutableQueue,
                currentIndex = nextIndex,
                isFlipped = false,
                isSessionComplete = isComplete,
                learnedCount = newLearned,
                unsureCount = newUnsure,
                priorityCount = newPriority,
                gamificationStats = streakManager.getStats()
            )
        }
    }

    fun restartSession() {
        loadSessionCards(_uiState.value.selectedDeckId)
    }
}
