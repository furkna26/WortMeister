package com.wortmeister.app.ui.screens.decks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wortmeister.app.data.local.entity.Deck
import com.wortmeister.app.data.repository.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DeckWithCount(
    val deck: Deck,
    val cardCount: Int
)

class DecksViewModel(
    private val deckRepository: DeckRepository
) : ViewModel() {

    val decksWithCount: StateFlow<List<DeckWithCount>> = combine(
        deckRepository.allDecks,
        deckRepository.allCards
    ) { decks, cards ->
        decks.map { deck ->
            val count = cards.count { it.deckId == deck.id }
            DeckWithCount(deck = deck, cardCount = count)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun createDeck(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            deckRepository.createDeck(name.trim())
        }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch {
            deckRepository.deleteDeck(deck)
        }
    }

    fun getCardsForDeck(deckId: Long, onResult: (List<com.wortmeister.app.data.local.entity.GermanCard>) -> Unit) {
        viewModelScope.launch {
            val cards = deckRepository.getCardsByDeck(deckId)
            cards.collect {
                onResult(it)
                return@collect
            }
        }
    }

    fun importDeckText(rawText: String, onComplete: (Result<com.wortmeister.app.util.ImportResult>) -> Unit) {
        viewModelScope.launch {
            val result = com.wortmeister.app.util.DeckShareManager.importDeck(rawText, deckRepository)
            onComplete(result)
        }
    }

    fun getCuratedBundles(): List<com.wortmeister.app.data.local.seed.CuratedBundleInfo> {
        return com.wortmeister.app.data.local.seed.CuratedDecksSeeder.getCuratedBundlesInfo()
    }

    fun loadSelectedCuratedDecks(
        selectedDeckNames: Set<String>,
        onComplete: (decksCount: Int, cardsCount: Int) -> Unit
    ) {
        viewModelScope.launch {
            val result = com.wortmeister.app.data.local.seed.CuratedDecksSeeder.seedSelectedCuratedDecks(
                selectedDeckNames,
                deckRepository
            )
            onComplete(result.first, result.second)
        }
    }

    fun loadCuratedLevelDecks(onSuccess: (Int) -> Unit) {
        viewModelScope.launch {
            val count = com.wortmeister.app.data.local.seed.CuratedDecksSeeder.seedAllCuratedDecks(deckRepository)
            onSuccess(count)
        }
    }
}

