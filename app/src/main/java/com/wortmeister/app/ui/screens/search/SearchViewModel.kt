package com.wortmeister.app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wortmeister.app.data.local.entity.Deck
import com.wortmeister.app.data.local.entity.GermanCard
import com.wortmeister.app.data.local.preferences.CardPreferencesManager
import com.wortmeister.app.data.remote.api.TranslationApi
import com.wortmeister.app.data.remote.model.ExamplePair
import com.wortmeister.app.data.remote.model.WordSearchResult
import com.wortmeister.app.data.repository.DeckRepository
import com.wortmeister.app.data.repository.WordRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.combine

data class DeckInfo(
    val id: Long,
    val name: String,
    val cardCount: Int = 0,
    val learnedCount: Int = 0
)

data class SearchUiState(
    val query: String = "",
    val lastSearchedWord: String = "",
    val hasNoMatch: Boolean = false,
    val selectedSearchLanguage: String = "auto", // "auto", "tr", "de", "en"
    val targetLanguage: String = "de",            // "de", "tr", "en"
    val themeMode: String = "SYSTEM",             // "SYSTEM", "LIGHT", "DARK"
    val frontIncludes: Set<String> = CardPreferencesManager.DEFAULT_FRONT,
    val backIncludes: Set<String> = CardPreferencesManager.DEFAULT_BACK,
    val isLoading: Boolean = false,
    val searchResult: WordSearchResult? = null,
    val candidateWords: List<String> = emptyList(),
    val availableMeaningsTr: List<String> = emptyList(),
    val selectedMeaningsTr: Set<String> = emptySet(),
    val availableMeaningsEn: List<String> = emptyList(),
    val selectedMeaningsEn: Set<String> = emptySet(),
    val availableExamples: List<ExamplePair> = emptyList(),
    val selectedExample: String? = null,
    val selectedDeckId: Long = 1L,
    val decks: List<Deck> = emptyList(),
    val recentDecks: List<DeckInfo> = emptyList(),
    val liveSuggestions: List<String> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val saveSuccessMessage: String? = null,
    val errorMessage: String? = null
)

class SearchViewModel(
    private val wordRepository: WordRepository,
    private val deckRepository: DeckRepository,
    private val prefsManager: CardPreferencesManager,
    private val recentSearchesManager: com.wortmeister.app.data.local.preferences.RecentSearchesManager,
    private val translationApi: TranslationApi = TranslationApi()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SearchUiState(
            frontIncludes = prefsManager.frontIncludes,
            backIncludes = prefsManager.backIncludes,
            targetLanguage = prefsManager.targetLanguage,
            selectedSearchLanguage = prefsManager.searchLanguage,
            themeMode = prefsManager.themeMode,
            recentSearches = recentSearchesManager.getRecentSearches()
        )
    )
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var suggestionJob: Job? = null

    init {
        _uiState.update { it.copy(recentSearches = recentSearchesManager.getRecentSearches()) }
        viewModelScope.launch {
            combine(deckRepository.allDecks, deckRepository.allCards) { deckList, allCards ->
                deckList.map { deck ->
                    val cardsInDeck = allCards.filter { it.deckId == deck.id }
                    val learnedInDeck = cardsInDeck.count { it.intervalDays > 0 }
                    DeckInfo(
                        id = deck.id,
                        name = deck.name,
                        cardCount = cardsInDeck.size,
                        learnedCount = learnedInDeck
                    )
                }
            }.collect { deckInfos ->
                _uiState.update { state ->
                    val currentDeckId = if (deckInfos.any { it.id == state.selectedDeckId }) {
                        state.selectedDeckId
                    } else {
                        deckInfos.firstOrNull()?.id ?: 1L
                    }
                    state.copy(
                        recentDecks = deckInfos,
                        decks = deckInfos.map { Deck(id = it.id, name = it.name) },
                        selectedDeckId = currentDeckId
                    )
                }
            }
        }
    }

    fun toggleThemeMode() {
        val current = _uiState.value.themeMode
        val next = when (current) {
            "SYSTEM" -> "DARK"
            "DARK" -> "LIGHT"
            else -> "SYSTEM"
        }
        prefsManager.themeMode = next
        _uiState.update { it.copy(themeMode = next) }
    }

    fun setSearchLanguage(lang: String) {
        prefsManager.searchLanguage = lang
        _uiState.update { it.copy(selectedSearchLanguage = lang) }
    }

    fun setTargetLanguage(lang: String) {
        prefsManager.targetLanguage = lang
        _uiState.update { it.copy(targetLanguage = lang) }
    }

    fun refreshFromPreferences() {
        _uiState.update {
            it.copy(
                targetLanguage = prefsManager.targetLanguage,
                selectedSearchLanguage = prefsManager.searchLanguage,
                frontIncludes = prefsManager.frontIncludes,
                backIncludes = prefsManager.backIncludes,
                themeMode = prefsManager.themeMode
            )
        }
    }

    fun toggleFrontInclude(field: String) {
        _uiState.update { state ->
            val set = state.frontIncludes.toMutableSet()
            if (set.contains(field)) {
                if (set.size > 1) set.remove(field) // Keep at least one
            } else {
                set.add(field)
            }
            prefsManager.frontIncludes = set
            state.copy(frontIncludes = set)
        }
    }

    fun toggleBackInclude(field: String) {
        _uiState.update { state ->
            val set = state.backIncludes.toMutableSet()
            if (set.contains(field)) {
                if (set.size > 1) set.remove(field) // Keep at least one
            } else {
                set.add(field)
            }
            prefsManager.backIncludes = set
            state.copy(backIncludes = set)
        }
    }

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery, hasNoMatch = false, errorMessage = null, saveSuccessMessage = null) }

        suggestionJob?.cancel()
        if (newQuery.trim().length >= 2) {
            suggestionJob = viewModelScope.launch {
                delay(250) // Debounce
                val suggestions = wordRepository.getSuggestions(newQuery.trim(), _uiState.value.selectedSearchLanguage)
                _uiState.update { it.copy(liveSuggestions = suggestions) }
            }
        } else {
            _uiState.update { it.copy(liveSuggestions = emptyList()) }
        }
    }

    fun searchWord(wordToSearch: String? = null) {
        val word = (wordToSearch ?: _uiState.value.query).trim()
        if (word.isBlank()) return

        suggestionJob?.cancel()
        recentSearchesManager.addSearch(word)
        _uiState.update {
            it.copy(
                query = word,
                lastSearchedWord = word,
                isLoading = true,
                hasNoMatch = false,
                errorMessage = null,
                saveSuccessMessage = null,
                liveSuggestions = emptyList(),
                recentSearches = recentSearchesManager.getRecentSearches()
            )
        }

        viewModelScope.launch {
            try {
                val lang = _uiState.value.selectedSearchLanguage
                val result = wordRepository.searchWord(word, lang)
                val initialTr = result.meaningsTr.filter { it.isNotBlank() }
                val initialEn = result.meaningsEn.filter { it.isNotBlank() }

                val isNoMatch = !result.isSuccess || (
                    initialTr.all { it.contains("bulunamadı", true) || it.contains("unavailable", true) } &&
                    initialEn.all { it.contains("unavailable", true) || it.contains("bulunamadı", true) }
                )

                _uiState.update { state ->
                    if (isNoMatch) {
                        state.copy(
                            isLoading = false,
                            hasNoMatch = true,
                            searchResult = null,
                            liveSuggestions = result.suggestions
                        )
                    } else {
                        val defaultSelectedTr = initialTr.filter { !it.contains("bulunamadı", true) }.take(2).toSet()
                        val defaultSelectedEn = initialEn.filter { !it.contains("unavailable", true) }.take(1).toSet()

                        val defaultEx = result.exampleSentences.firstOrNull()?.let {
                            if (it.turkish.isNotBlank()) "${it.german} (${it.turkish})" else it.german
                        }

                        state.copy(
                            isLoading = false,
                            hasNoMatch = false,
                            searchResult = result,
                            candidateWords = result.candidateWords,
                            availableMeaningsTr = initialTr,
                            selectedMeaningsTr = defaultSelectedTr,
                            availableMeaningsEn = initialEn,
                            selectedMeaningsEn = defaultSelectedEn,
                            availableExamples = result.exampleSentences,
                            selectedExample = defaultEx,
                            liveSuggestions = result.suggestions
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        hasNoMatch = true,
                        searchResult = null,
                        errorMessage = "Arama sırasında bir hata oluştu: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun translateWithDeepLAndCreateCard(targetTerm: String? = null) {
        val word = (targetTerm ?: _uiState.value.lastSearchedWord.ifBlank { _uiState.value.query }).trim()
        if (word.isBlank()) return

        _uiState.update {
            it.copy(
                isLoading = true,
                hasNoMatch = false,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                val apiKey = prefsManager.deepLApiKey ?: CardPreferencesManager.DEFAULT_DEEPL_KEY
                val lang = _uiState.value.selectedSearchLanguage

                var germanWord: String
                var meaningTr: String
                var meaningEn: String
                var sourceLang = "de"

                val isTr = lang == "tr" || isLikelyTurkish(word)

                if (isTr) {
                    sourceLang = "tr"
                    meaningTr = word
                    val translatedDe = translationApi.translateViaDeepL(word, "de", apiKey, "tr")
                        ?: translationApi.translateText(word, "tr", "de", apiKey)
                        ?: word
                    germanWord = cleanGermanArticle(translatedDe)
                    meaningEn = translationApi.translateViaDeepL(germanWord, "en", apiKey, "de")
                        ?: translationApi.translateText(germanWord, "de", "en", apiKey)
                        ?: ""
                } else if (lang == "en") {
                    sourceLang = "en"
                    meaningEn = word
                    val translatedDe = translationApi.translateViaDeepL(word, "de", apiKey, "en")
                        ?: translationApi.translateText(word, "en", "de", apiKey)
                        ?: word
                    germanWord = cleanGermanArticle(translatedDe)
                    meaningTr = translationApi.translateViaDeepL(germanWord, "tr", apiKey, "de")
                        ?: translationApi.translateText(germanWord, "de", "tr", apiKey)
                        ?: ""
                } else {
                    germanWord = cleanGermanArticle(word)
                    meaningTr = translationApi.translateViaDeepL(word, "tr", apiKey, "de")
                        ?: translationApi.translateText(word, "de", "tr", apiKey)
                        ?: "Anlam bulunamadı"
                    meaningEn = translationApi.translateViaDeepL(word, "en", apiKey, "de")
                        ?: translationApi.translateText(word, "de", "en", apiKey)
                        ?: ""
                    if (meaningTr == word && lang == "auto") {
                        val toDe = translationApi.translateText(word, "auto", "de", apiKey)
                        if (!toDe.isNullOrBlank() && !toDe.equals(word, ignoreCase = true)) {
                            germanWord = cleanGermanArticle(toDe)
                            meaningTr = word
                            sourceLang = "tr"
                        }
                    }
                }

                var partOfSpeech = "OTHER"
                var article: String? = null
                var plural: String? = null
                var praesens: String? = null
                var praeteritum: String? = null
                var partizipZwei: String? = null
                var imperativ: String? = null
                var hilfsverb: String? = null
                val isSeparable = false
                val separablePrefix: String? = null

                if (germanWord.contains(" ")) {
                    partOfSpeech = "PHRASE"
                } else if (germanWord.firstOrNull()?.isUpperCase() == true) {
                    partOfSpeech = "NOUN"
                    val lower = germanWord.lowercase()
                    article = when {
                        lower.endsWith("ung") || lower.endsWith("heit") || lower.endsWith("keit") ||
                        lower.endsWith("schaft") || lower.endsWith("tät") || lower.endsWith("ion") ||
                        lower.endsWith("ik") || lower.endsWith("ei") || lower.endsWith("ur") -> "die"
                        lower.endsWith("chen") || lower.endsWith("lein") || lower.endsWith("ment") ||
                        lower.endsWith("um") || lower.endsWith("tum") -> "das"
                        lower.endsWith("ismus") || lower.endsWith("or") || lower.endsWith("ling") ||
                        lower.endsWith("ist") -> "der"
                        else -> null
                    }
                } else if (germanWord.endsWith("en") || germanWord.endsWith("eln") || germanWord.endsWith("ern")) {
                    partOfSpeech = "VERB"
                    val conj = com.wortmeister.app.data.remote.parser.GermanVerbHelper.conjugate(germanWord)
                    praesens = conj.praesens
                    praeteritum = conj.praeteritum
                    partizipZwei = conj.partizipZwei
                    imperativ = conj.imperativ
                    hilfsverb = if (conj.partizipZwei.startsWith("ist ")) "ist" else "hat"
                }

                val exampleGerman = if (partOfSpeech == "NOUN") {
                    val art = article ?: "das"
                    "Das ist $art $germanWord."
                } else if (partOfSpeech == "VERB") {
                    "Wir möchten heute $germanWord."
                } else {
                    "Das Wort „$germanWord“ ist sehr nützlich."
                }
                val exampleTr = translationApi.translateViaDeepL(exampleGerman, "tr", apiKey, "de")
                    ?: translationApi.translateText(exampleGerman, "de", "tr", apiKey)
                    ?: ""
                val examplePairs = listOf(ExamplePair(german = exampleGerman, turkish = exampleTr))

                val newResult = WordSearchResult(
                    query = word,
                    germanWord = germanWord,
                    sourceLanguage = sourceLang,
                    searchedTerm = word,
                    partOfSpeech = partOfSpeech,
                    article = article,
                    pluralForm = plural,
                    praesens = praesens,
                    praeteritum = praeteritum,
                    partizipZwei = partizipZwei,
                    imperativ = imperativ,
                    hilfsverb = hilfsverb,
                    isSeparable = isSeparable,
                    separablePrefix = separablePrefix,
                    meaningsTr = listOf(meaningTr).filter { it.isNotBlank() },
                    meaningsEn = listOf(meaningEn).filter { it.isNotBlank() },
                    exampleSentences = examplePairs,
                    isSuccess = true
                )

                _uiState.update { state ->
                    val defaultTr = setOf(meaningTr).filter { it.isNotBlank() }.toSet()
                    val defaultEn = setOf(meaningEn).filter { it.isNotBlank() }.toSet()
                    val defaultEx = examplePairs.firstOrNull()?.let {
                        if (it.turkish.isNotBlank()) "${it.german} (${it.turkish})" else it.german
                    }

                    state.copy(
                        isLoading = false,
                        hasNoMatch = false,
                        searchResult = newResult,
                        candidateWords = listOf(germanWord),
                        availableMeaningsTr = listOf(meaningTr),
                        selectedMeaningsTr = defaultTr,
                        availableMeaningsEn = listOf(meaningEn).filter { it.isNotBlank() },
                        selectedMeaningsEn = defaultEn,
                        availableExamples = examplePairs,
                        selectedExample = defaultEx,
                        liveSuggestions = emptyList()
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        hasNoMatch = true,
                        errorMessage = "DeepL çevirisi yapılamadı: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    private fun cleanGermanArticle(input: String): String {
        return input.trim().replace(Regex("^(der|die|das)\\s+", RegexOption.IGNORE_CASE), "").trim()
    }

    private fun isLikelyTurkish(text: String): Boolean {
        val trChars = setOf('ç', 'ğ', 'ı', 'ö', 'ş', 'ü', 'Ç', 'Ğ', 'İ', 'Ö', 'Ş', 'Ü')
        return text.any { it in trChars }
    }

    fun selectCandidateWord(candidate: String) {
        val currentCandidates = _uiState.value.candidateWords
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val result = wordRepository.searchWord(candidate, "de")
                _uiState.update { state ->
                    val initialTr = result.meaningsTr.filter { it.isNotBlank() }
                    val initialEn = result.meaningsEn.filter { it.isNotBlank() }
                    val defaultSelectedTr = initialTr.take(2).toSet()
                    val defaultSelectedEn = initialEn.take(1).toSet()

                    val defaultEx = result.exampleSentences.firstOrNull()?.let {
                        if (it.turkish.isNotBlank()) "${it.german} (${it.turkish})" else it.german
                    }

                    state.copy(
                        isLoading = false,
                        searchResult = result,
                        candidateWords = currentCandidates,
                        availableMeaningsTr = initialTr,
                        selectedMeaningsTr = defaultSelectedTr,
                        availableMeaningsEn = initialEn,
                        selectedMeaningsEn = defaultSelectedEn,
                        availableExamples = result.exampleSentences,
                        selectedExample = defaultEx,
                        liveSuggestions = result.suggestions
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Sonuç yüklenemedi: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun toggleMeaningTr(meaning: String) {
        _uiState.update { state ->
            val set = state.selectedMeaningsTr.toMutableSet()
            if (set.contains(meaning)) set.remove(meaning) else set.add(meaning)
            state.copy(selectedMeaningsTr = set)
        }
    }

    fun toggleMeaningEn(meaning: String) {
        _uiState.update { state ->
            val set = state.selectedMeaningsEn.toMutableSet()
            if (set.contains(meaning)) set.remove(meaning) else set.add(meaning)
            state.copy(selectedMeaningsEn = set)
        }
    }

    fun addCustomMeaningTr(customMeaning: String) {
        val trimmed = customMeaning.trim()
        if (trimmed.isBlank()) return
        _uiState.update { state ->
            val list = state.availableMeaningsTr.toMutableList()
            if (!list.contains(trimmed)) list.add(0, trimmed)
            val selected = state.selectedMeaningsTr.toMutableSet()
            selected.add(trimmed)
            state.copy(availableMeaningsTr = list, selectedMeaningsTr = selected)
        }
    }

    fun addCustomExample(germanText: String, turkishText: String) {
        val g = germanText.trim()
        val t = turkishText.trim()
        if (g.isBlank()) return
        val full = if (t.isNotBlank()) "$g ($t)" else g
        val pair = ExamplePair(german = g, turkish = t)
        _uiState.update { state ->
            val list = state.availableExamples.toMutableList()
            list.add(0, pair)
            state.copy(availableExamples = list, selectedExample = full)
        }
    }

    fun selectExample(example: String) {
        _uiState.update { it.copy(selectedExample = example) }
    }

    fun selectDeck(deckId: Long) {
        _uiState.update { it.copy(selectedDeckId = deckId) }
    }

    fun createAndSelectDeck(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val newId = deckRepository.createDeck(name)
            _uiState.update { it.copy(selectedDeckId = newId) }
        }
    }

    fun saveCard() {
        val state = _uiState.value
        val result = state.searchResult ?: return

        val trMeaningsCombined = state.selectedMeaningsTr.joinToString(", ")
        val enMeaningsCombined = state.selectedMeaningsEn.joinToString(", ")

        val selectedDeck = state.decks.find { it.id == state.selectedDeckId }
        val isEnglishDeck = selectedDeck?.name?.contains("İngilizce", ignoreCase = true) == true
        val cardTargetLang = if (isEnglishDeck) "en" else "de"

        val cleanWord = if (result.partOfSpeech == "NOUN") {
            var w = result.germanWord.trim()
            result.article?.let { art ->
                if (w.startsWith("$art ", ignoreCase = true)) {
                    w = w.substring(art.length + 1).trim()
                }
            }
            w.replaceFirstChar { it.uppercase() }
        } else {
            result.germanWord.trim()
        }

        val card = GermanCard(
            deckId = state.selectedDeckId,
            germanWord = cleanWord,
            partOfSpeech = result.partOfSpeech,
            article = result.article,
            pluralForm = result.pluralForm,
            praesens = result.praesens,
            praeteritum = result.praeteritum,
            partizipZwei = result.partizipZwei,
            imperativ = result.imperativ,
            hilfsverb = result.hilfsverb,
            isSeparable = result.isSeparable,
            separablePrefix = result.separablePrefix,
            meaningsTr = if (trMeaningsCombined.isBlank()) state.availableMeaningsTr.firstOrNull() ?: "" else trMeaningsCombined,
            meaningsEn = if (enMeaningsCombined.isBlank()) state.availableMeaningsEn.firstOrNull() ?: "" else enMeaningsCombined,
            exampleSentence = state.selectedExample,
            nextReviewDate = System.currentTimeMillis(),
            intervalDays = 0,
            isPriority = false,
            frontIncludes = state.frontIncludes.joinToString(","),
            backIncludes = state.backIncludes.joinToString(","),
            targetLanguage = cardTargetLang,
            sourceLanguage = result.sourceLanguage,
            originalQuery = result.searchedTerm
        )

        viewModelScope.launch {
            try {
                deckRepository.saveCard(card)
                _uiState.update {
                    it.copy(
                        query = "",
                        searchResult = null,
                        selectedMeaningsTr = emptySet(),
                        selectedMeaningsEn = emptySet(),
                        availableMeaningsTr = emptyList(),
                        availableMeaningsEn = emptyList(),
                        availableExamples = emptyList(),
                        selectedExample = null,
                        liveSuggestions = emptyList(),
                        saveSuccessMessage = "„${card.germanWord}“ desteye eklendi."
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Kart kaydedilemedi: ${e.localizedMessage}")
                }
            }
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(saveSuccessMessage = null, errorMessage = null) }
    }

    fun clearRecentSearches() {
        recentSearchesManager.clearAll()
        _uiState.update { it.copy(recentSearches = emptyList()) }
    }

    fun removeRecentSearch(term: String) {
        recentSearchesManager.removeSearch(term)
        _uiState.update { it.copy(recentSearches = recentSearchesManager.getRecentSearches()) }
    }
}
