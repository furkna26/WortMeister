package com.wortmeister.app.ui.screens.translate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wortmeister.app.data.local.entity.Deck
import com.wortmeister.app.data.local.entity.GermanCard
import com.wortmeister.app.data.local.preferences.CardPreferencesManager
import com.wortmeister.app.data.remote.api.TranslationApi
import com.wortmeister.app.data.remote.model.WordSearchResult
import com.wortmeister.app.data.repository.DeckRepository
import com.wortmeister.app.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TranslateUiState(
    val inputText: String = "",
    val sourceLang: String = "tr",
    val targetLang: String = "de",
    val isTranslating: Boolean = false,
    val isAnalyzingGrammar: Boolean = false,
    val translatedText: String? = null,
    val grammarAnalysis: WordSearchResult? = null,
    val selectedDeckId: Long = 1L,
    val decks: List<Deck> = emptyList(),
    val saveSuccessMessage: String? = null,
    val errorMessage: String? = null
)

class TranslateViewModel(
    private val translationApi: TranslationApi = TranslationApi(),
    private val wordRepository: WordRepository,
    private val deckRepository: DeckRepository,
    private val prefsManager: CardPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(TranslateUiState())
    val uiState: StateFlow<TranslateUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            deckRepository.allDecks.collect { deckList ->
                _uiState.update { state ->
                    val currentDeckId = if (deckList.any { it.id == state.selectedDeckId }) {
                        state.selectedDeckId
                    } else {
                        deckList.firstOrNull()?.id ?: 1L
                    }
                    state.copy(decks = deckList, selectedDeckId = currentDeckId)
                }
            }
        }
    }

    fun onInputTextChange(text: String) {
        val limited = text.take(150)
        _uiState.update {
            it.copy(
                inputText = limited,
                errorMessage = null,
                saveSuccessMessage = null
            )
        }
    }

    fun setSourceLang(lang: String) {
        _uiState.update { it.copy(sourceLang = lang) }
    }

    fun setTargetLang(lang: String) {
        _uiState.update { it.copy(targetLang = lang) }
    }

    fun swapLanguages() {
        _uiState.update { state ->
            val prevSource = state.sourceLang
            val prevTarget = state.targetLang
            val prevInput = state.inputText
            val prevTranslated = state.translatedText ?: ""
            state.copy(
                sourceLang = prevTarget,
                targetLang = prevSource,
                inputText = prevTranslated.take(150),
                translatedText = prevInput.takeIf { it.isNotBlank() },
                grammarAnalysis = null
            )
        }
        if (_uiState.value.inputText.isNotBlank()) {
            translate()
        }
    }

    fun selectDeck(deckId: Long) {
        _uiState.update { it.copy(selectedDeckId = deckId) }
    }

    fun translate() {
        val query = _uiState.value.inputText.trim().take(150)
        if (query.isBlank()) return

        val source = _uiState.value.sourceLang
        val target = _uiState.value.targetLang
        val apiKey = prefsManager.deepLApiKey

        _uiState.update {
            it.copy(
                isTranslating = true,
                errorMessage = null,
                saveSuccessMessage = null,
                translatedText = null,
                grammarAnalysis = null
            )
        }

        viewModelScope.launch {
            try {
                val result = translationApi.translateText(
                    text = query,
                    sourceLang = source,
                    targetLang = target,
                    apiKey = apiKey
                )

                if (result.isNullOrBlank()) {
                    _uiState.update {
                        it.copy(
                            isTranslating = false,
                            errorMessage = "Çeviri yapılamadı. Lütfen bağlantınızı kontrol edin."
                        )
                    }
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        isTranslating = false,
                        translatedText = result
                    )
                }

                val germanCandidate = when {
                    target == "de" -> result
                    source == "de" -> query
                    else -> null
                }

                if (!germanCandidate.isNullOrBlank()) {
                    analyzeGermanGrammar(germanCandidate)
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isTranslating = false,
                        errorMessage = "Hata: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    private fun analyzeGermanGrammar(germanText: String) {
        val cleanCandidate = germanText.trim()
            .replace(Regex("^(der|die|das)\\s+", RegexOption.IGNORE_CASE), "")
            .trim()

        if (cleanCandidate.isBlank()) return

        _uiState.update { it.copy(isAnalyzingGrammar = true) }

        viewModelScope.launch {
            try {
                val search = wordRepository.searchWord(cleanCandidate, "de")
                if (search.isSuccess) {
                    _uiState.update {
                        it.copy(
                            isAnalyzingGrammar = false,
                            grammarAnalysis = search
                        )
                    }
                } else {
                    _uiState.update { it.copy(isAnalyzingGrammar = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isAnalyzingGrammar = false) }
            }
        }
    }

    fun saveAsCard() {
        val state = _uiState.value
        val source = state.sourceLang
        val target = state.targetLang
        val originalInput = state.inputText.trim()
        val translated = state.translatedText?.trim() ?: ""

        if (originalInput.isBlank() || translated.isBlank()) return

        val germanWord: String
        val meaningTr: String
        val meaningEn: String

        if (source == "de") {
            germanWord = originalInput
            meaningTr = if (target == "tr") translated else ""
            meaningEn = if (target == "en") translated else ""
        } else if (target == "de") {
            germanWord = translated
            meaningTr = if (source == "tr") originalInput else ""
            meaningEn = if (source == "en") originalInput else ""
        } else {
            germanWord = originalInput
            meaningTr = translated
            meaningEn = ""
        }

        val analysis = state.grammarAnalysis
        val article = analysis?.article
        val pos = analysis?.partOfSpeech ?: "OTHER"
        val plural = analysis?.pluralForm
        val praesens = analysis?.praesens
        val praeteritum = analysis?.praeteritum
        val partizipZwei = analysis?.partizipZwei
        val imperativ = analysis?.imperativ
        val hilfsverb = analysis?.hilfsverb
        val isSeparable = analysis?.isSeparable ?: false
        val separablePrefix = analysis?.separablePrefix

        val displayGerman = if (pos == "NOUN" && article != null && !germanWord.startsWith(article, true)) {
            "$article $germanWord"
        } else {
            germanWord
        }

        val newCard = GermanCard(
            deckId = state.selectedDeckId,
            germanWord = displayGerman,
            partOfSpeech = pos,
            article = article,
            pluralForm = plural,
            praesens = praesens,
            praeteritum = praeteritum,
            partizipZwei = partizipZwei,
            imperativ = imperativ,
            hilfsverb = hilfsverb,
            isSeparable = isSeparable,
            separablePrefix = separablePrefix,
            meaningsTr = meaningTr.ifBlank { analysis?.meaningsTr?.firstOrNull() ?: "" },
            meaningsEn = meaningEn.ifBlank { analysis?.meaningsEn?.firstOrNull() ?: "" },
            exampleSentence = analysis?.exampleSentences?.firstOrNull()?.let {
                if (it.turkish.isNotBlank()) "${it.german} (${it.turkish})" else it.german
            },
            nextReviewDate = System.currentTimeMillis(),
            intervalDays = 0,
            frontIncludes = "GERMAN,GRAMMAR",
            backIncludes = "TURKISH,ENGLISH,EXAMPLE",
            targetLanguage = if (source == "de" || target == "de") "de" else target
        )

        viewModelScope.launch {
            try {
                deckRepository.saveCard(newCard)
                _uiState.update {
                    it.copy(saveSuccessMessage = "„$displayGerman“ karta kaydedildi!")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Kaydetme başarısız: ${e.localizedMessage}")
                }
            }
        }
    }
}