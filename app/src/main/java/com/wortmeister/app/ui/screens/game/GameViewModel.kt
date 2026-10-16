package com.wortmeister.app.ui.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wortmeister.app.data.local.entity.Deck
import com.wortmeister.app.data.local.entity.GermanCard
import com.wortmeister.app.data.repository.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(
    private val deckRepository: DeckRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var cachedDecks: List<Deck> = emptyList()
    private var cachedCards: List<GermanCard> = emptyList()

    init {
        viewModelScope.launch {
            deckRepository.allDecks.collectLatest { decks ->
                cachedDecks = decks
                _uiState.update { current ->
                    val validIds = current.selectedDeckIds.intersect(decks.map { it.id }.toSet())
                    val updatedIds = if (current.selectedDeckIds.isEmpty() && decks.isNotEmpty()) {
                        decks.map { it.id }.toSet()
                    } else validIds

                    val newState = current.copy(
                        availableDecks = decks,
                        selectedDeckIds = updatedIds
                    )
                    newState.copy(eligibleCardCount = calculateEligibleCount(newState))
                }
            }
        }

        viewModelScope.launch {
            deckRepository.allCards.collectLatest { cards ->
                cachedCards = cards
                _uiState.update { current ->
                    current.copy(eligibleCardCount = calculateEligibleCount(current))
                }
            }
        }
    }

    private fun getFilteredCards(state: GameUiState): List<GermanCard> {
        return when (state.deckScope) {
            DeckScopeOption.ALL_DECKS -> cachedCards
            DeckScopeOption.SELECTED_DECKS -> cachedCards.filter { it.deckId in state.selectedDeckIds }
        }
    }

    private fun calculateEligibleCount(state: GameUiState): Int {
        val gameType = state.selectedGameType ?: return 0
        val pool = getFilteredCards(state)
        return GameQuestionGenerator.filterEligibleCards(pool, gameType).size
    }

    fun openDeckSelector(gameType: GameType) {
        _uiState.update { current ->
            val initialDeckIds = if (current.selectedDeckIds.isEmpty()) {
                cachedDecks.map { it.id }.toSet()
            } else current.selectedDeckIds
            val tempState = current.copy(
                selectedGameType = gameType,
                showDeckSelector = true,
                selectedDeckIds = initialDeckIds
            )
            tempState.copy(eligibleCardCount = calculateEligibleCount(tempState))
        }
    }

    fun selectGameType(gameType: GameType) {
        _uiState.update { current ->
            val updated = current.copy(selectedGameType = gameType)
            updated.copy(eligibleCardCount = calculateEligibleCount(updated))
        }
    }

    fun dismissDeckSelector() {
        _uiState.update { it.copy(showDeckSelector = false) }
    }

    fun setDeckScope(scope: DeckScopeOption) {
        _uiState.update { current ->
            val updated = current.copy(deckScope = scope)
            updated.copy(eligibleCardCount = calculateEligibleCount(updated))
        }
    }

    fun toggleDeckSelection(deckId: Long) {
        _uiState.update { current ->
            val updatedIds = if (current.selectedDeckIds.contains(deckId)) {
                current.selectedDeckIds - deckId
            } else {
                current.selectedDeckIds + deckId
            }
            val updated = current.copy(selectedDeckIds = updatedIds)
            updated.copy(eligibleCardCount = calculateEligibleCount(updated))
        }
    }

    fun selectAllDecks(select: Boolean) {
        _uiState.update { current ->
            val updatedIds = if (select) cachedDecks.map { it.id }.toSet() else emptySet()
            val updated = current.copy(selectedDeckIds = updatedIds)
            updated.copy(eligibleCardCount = calculateEligibleCount(updated))
        }
    }

    fun startGame() {
        val currentState = _uiState.value
        val gameType = currentState.selectedGameType ?: return
        val pool = getFilteredCards(currentState)
        val eligible = GameQuestionGenerator.filterEligibleCards(pool, gameType)
        if (eligible.size < 4) return

        val questions = GameQuestionGenerator.generateQuestions(
            eligibleCards = eligible,
            allPoolCards = cachedCards,
            gameType = gameType,
            maxQuestions = 15
        )

        if (questions.isEmpty()) return

        _uiState.update {
            it.copy(
                showDeckSelector = false,
                isGameActive = true,
                questions = questions,
                currentQuestionIndex = 0,
                selectedOptionIndex = null,
                isAnswerRevealed = false,
                correctAnswersCount = 0,
                wrongAnswersCount = 0,
                isGameFinished = false
            )
        }
    }

    fun selectOption(index: Int) {
        val current = _uiState.value
        if (current.isAnswerRevealed || current.currentQuestionIndex >= current.questions.size) return

        val question = current.questions[current.currentQuestionIndex]
        val isCorrect = (index == question.correctIndex)

        _uiState.update {
            it.copy(
                selectedOptionIndex = index,
                isAnswerRevealed = true,
                correctAnswersCount = if (isCorrect) it.correctAnswersCount + 1 else it.correctAnswersCount,
                wrongAnswersCount = if (!isCorrect) it.wrongAnswersCount + 1 else it.wrongAnswersCount
            )
        }
    }

    fun nextQuestion() {
        val current = _uiState.value
        if (current.currentQuestionIndex + 1 < current.questions.size) {
            _uiState.update {
                it.copy(
                    currentQuestionIndex = it.currentQuestionIndex + 1,
                    selectedOptionIndex = null,
                    isAnswerRevealed = false
                )
            }
        } else {
            _uiState.update {
                it.copy(isGameFinished = true)
            }
        }
    }

    fun restartGame() {
        val current = _uiState.value
        val gameType = current.selectedGameType ?: return
        val pool = getFilteredCards(current)
        val eligible = GameQuestionGenerator.filterEligibleCards(pool, gameType)
        if (eligible.size < 4) {
            exitGame()
            return
        }

        val questions = GameQuestionGenerator.generateQuestions(
            eligibleCards = eligible,
            allPoolCards = cachedCards,
            gameType = gameType,
            maxQuestions = 15
        )

        _uiState.update {
            it.copy(
                questions = questions,
                currentQuestionIndex = 0,
                selectedOptionIndex = null,
                isAnswerRevealed = false,
                correctAnswersCount = 0,
                wrongAnswersCount = 0,
                isGameFinished = false
            )
        }
    }

    fun exitGame() {
        _uiState.update {
            it.copy(
                isGameActive = false,
                isGameFinished = false,
                selectedGameType = null,
                questions = emptyList(),
                currentQuestionIndex = 0,
                selectedOptionIndex = null,
                isAnswerRevealed = false,
                correctAnswersCount = 0,
                wrongAnswersCount = 0
            )
        }
    }
}
