package com.wortmeister.app.data.remote.model

data class WordSearchResult(
    val query: String,
    val germanWord: String,
    val sourceLanguage: String = "de",  // "tr", "en", "de"
    val searchedTerm: String = "",       // original searched term
    val partOfSpeech: String = "OTHER", // NOUN, VERB, ADJECTIVE, PREPOSITION, OTHER
    val article: String? = null,        // der, die, das
    val pluralForm: String? = null,
    val praesens: String? = null,
    val praeteritum: String? = null,
    val partizipZwei: String? = null,
    val imperativ: String? = null,
    val hilfsverb: String? = null,
    val isSeparable: Boolean = false,
    val separablePrefix: String? = null,
    val isReflexive: Boolean = false,
    val reflexiveType: String? = null,
    val regularity: String? = null,
    val meaningsTr: List<String> = emptyList(),
    val meaningsEn: List<String> = emptyList(),
    val exampleSentences: List<ExamplePair> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val correctedFrom: String? = null,
    val rootLemmaInfo: String? = null,
    val candidateWords: List<String> = emptyList(),
    val irregularConjugation: com.wortmeister.app.data.remote.parser.FullIrregularConjugation? = null,
    val isSuccess: Boolean = true,
    val errorMessage: String? = null
)

data class ExamplePair(
    val german: String,
    val turkish: String = ""
)
