package com.wortmeister.app.data.repository

import androidx.collection.LruCache
import com.wortmeister.app.data.local.dao.GermanCardDao
import com.wortmeister.app.data.remote.api.TranslationApi
import com.wortmeister.app.data.remote.api.WiktionaryApi
import com.wortmeister.app.data.remote.model.ExamplePair
import com.wortmeister.app.data.remote.model.WordSearchResult
import com.wortmeister.app.data.remote.parser.FuzzyMatcher
import com.wortmeister.app.data.remote.parser.GermanVerbHelper
import com.wortmeister.app.data.remote.parser.SearchNormalizer
import com.wortmeister.app.data.remote.parser.VerbformenScraper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

import com.wortmeister.app.data.local.preferences.CardPreferencesManager

class WordRepository(
    private val cardDao: GermanCardDao,
    private val scraper: VerbformenScraper = VerbformenScraper(),
    private val wiktionaryApi: WiktionaryApi = WiktionaryApi(),
    private val translationApi: TranslationApi = TranslationApi(),
    private val prefsManager: CardPreferencesManager? = null
) {
    private val memoryCache = LruCache<String, WordSearchResult>(100)

    suspend fun getSuggestions(query: String, lang: String = "auto"): List<String> = withContext(Dispatchers.IO) {
        val sanitized = SearchNormalizer.normalize(query)
        val clean = sanitized.cleanedTerm
        if (clean.length < 2) return@withContext emptyList()
        val suggestions = wiktionaryApi.getSuggestions(clean).toMutableList()
        val offlineSug = com.wortmeister.app.data.local.dictionary.OfflineDictionaryEngine.getSuggestions(clean)
        for (os in offlineSug) {
            if (!suggestions.contains(os)) {
                suggestions.add(0, os)
            }
        }

        for (candidate in sanitized.phoneticCandidates) {
            val candSuggestions = wiktionaryApi.getSuggestions(candidate)
            for (cs in candSuggestions) {
                if (!suggestions.contains(cs)) {
                    suggestions.add(cs)
                }
            }
        }
        suggestions
    }

    suspend fun searchWord(query: String, selectedLang: String = "auto"): WordSearchResult = withContext(Dispatchers.IO) {
        // 1. INPUT SANITIZATION & NORMALIZATION
        val sanitized = SearchNormalizer.normalize(query)
        val cleanQuery = sanitized.cleanedTerm
        if (cleanQuery.isBlank()) {
            return@withContext WordSearchResult(
                query = query,
                germanWord = "",
                isSuccess = false,
                errorMessage = "Lütfen bir kelime yazın."
            )
        }

        val cacheKey = "${selectedLang}_${cleanQuery.lowercase()}"
        memoryCache[cacheKey]?.let { cached ->
            return@withContext cached
        }

        // 1a. CHECK OFFLINE DICTIONARY ENGINE FIRST (Instant, 100% verified, offline capable)
        val offlineHit = com.wortmeister.app.data.local.dictionary.OfflineDictionaryEngine.find(cleanQuery)
        if (offlineHit != null) {
            memoryCache.put(cacheKey, offlineHit)
            return@withContext offlineHit
        }

        var correctedFrom: String? = null
        var rootLemmaInfo: String? = null
        val hintedArticle = sanitized.hintedArticle

        // 2. CHECK SEPARABLE VERB CANDIDATE (e.g. "bricht ein" -> "einbrechen")
        var currentQuery = cleanQuery
        if (sanitized.separableVerbCandidate != null) {
            val sepCandidate = sanitized.separableVerbCandidate
            val sepData = scraper.scrape(sepCandidate)
            if (sepData.isFound) {
                currentQuery = sepCandidate
                rootLemmaInfo = "„$cleanQuery“ → Ayrılabilen Fiil: $sepCandidate"
            }
        }

        val deepLKey = prefsManager?.deepLApiKey

        // 3. MULTI-LANGUAGE RESOLUTION (Resolve Turkish/English to German lemma)
        val resolvedInfo = translationApi.resolveToGerman(currentQuery, selectedLang, deepLKey)
        val detectedLang = resolvedInfo.detectedSourceLang
        var germanTargetWord = resolvedInfo.germanTargetWord.trim().replace(Regex("^(der|die|das)\\s+", RegexOption.IGNORE_CASE), "")

        // 3b. RESOLVE PLURAL NOUN TO SINGULAR LEMMA (e.g., "Häuser" -> "Haus", "Wohnungen" -> "Wohnung")
        var hintedPlural: String? = null
        val pluralResolution = SearchNormalizer.resolvePluralToSingular(germanTargetWord)
        if (pluralResolution != null) {
            rootLemmaInfo = "„$germanTargetWord“ → Tekil: ${pluralResolution.article} ${pluralResolution.singular} (${pluralResolution.pluralWithDie})"
            germanTargetWord = pluralResolution.singular
            if (hintedArticle == null) {
                // Keep the singular article
            }
            hintedPlural = pluralResolution.pluralWithDie
        }

        // 4. LEMMA & FLEXION RESOLUTION (e.g., "ging" -> "gehen", "Häuser" -> "Haus", "geschrieben" -> "schreiben")
        if (detectedLang == "de") {
            val flexionFallback = wiktionaryApi.getWiktionaryFallback(germanTargetWord)
            if (!flexionFallback.baseLemma.isNullOrBlank() && !flexionFallback.baseLemma.equals(germanTargetWord, ignoreCase = true)) {
                val base = flexionFallback.baseLemma
                rootLemmaInfo = "„$germanTargetWord“ → Mastar/Kök: $base (${flexionFallback.inflectionInfo ?: "Çekimli"})"
                germanTargetWord = base
            }
        }

        // 5. PHONETIC & UMLAUT RESOLUTION (e.g., "shreiben" -> "schreiben", "qualitat" -> "Qualität")
        if (detectedLang == "de") {
            val spellingCandidate = resolveGermanSpelling(germanTargetWord, sanitized.phoneticCandidates)
            if (spellingCandidate != germanTargetWord) {
                // If it only differs by capitalization, it is standard orthography, NOT a typo correction!
                if (!spellingCandidate.equals(germanTargetWord, ignoreCase = true)) {
                    correctedFrom = germanTargetWord
                }
                germanTargetWord = spellingCandidate
            }
        }

        // 6. SCRAPE VERBFORMEN & FETCH TRANSLATIONS CONCURRENTLY
        val verbDataDeferred = async { scraper.scrape(germanTargetWord) }
        val suggestionsDeferred = async { wiktionaryApi.getSuggestions(germanTargetWord) }
        val trTranslationDeferred = async { translationApi.getTranslations(germanTargetWord, "tr", deepLKey) }
        val enTranslationDeferred = async { translationApi.getTranslations(germanTargetWord, "en", deepLKey) }

        var verbData = verbDataDeferred.await()
        var suggestions = suggestionsDeferred.await()

        // 7. FUZZY MATCHER / LEVENSHTEIN AUTO-CORRECTION (e.g., "spieeln" -> "spielen")
        if (!verbData.isFound && detectedLang == "de" && suggestions.isNotEmpty()) {
            val bestFuzzy = FuzzyMatcher.findBestMatch(germanTargetWord, suggestions)
            if (bestFuzzy != null && !bestFuzzy.matchedWord.equals(germanTargetWord, ignoreCase = true)) {
                val candidateWord = bestFuzzy.matchedWord
                val fuzzyVerbData = scraper.scrape(candidateWord)
                if (fuzzyVerbData.isFound) {
                    correctedFrom = germanTargetWord
                    germanTargetWord = candidateWord
                    verbData = fuzzyVerbData
                    suggestions = wiktionaryApi.getSuggestions(candidateWord)
                }
            }
        }

        val trResult = trTranslationDeferred.await()
        val enResult = enTranslationDeferred.await()

        val trMeanings = trResult.meanings.toMutableList()
        val enMeanings = enResult.meanings.toMutableList()

        if (detectedLang == "tr" && !trMeanings.contains(cleanQuery)) {
            trMeanings.add(0, cleanQuery)
        }
        if (detectedLang == "en" && !enMeanings.contains(cleanQuery)) {
            enMeanings.add(0, cleanQuery)
        }

        // Check canonical German word from translation if available
        val canonicalFromGoogle = trResult.canonicalGermanWord ?: enResult.canonicalGermanWord
        if (canonicalFromGoogle != null && canonicalFromGoogle.length > 2 && !verbData.isFound) {
            val secondScrape = scraper.scrape(canonicalFromGoogle)
            if (secondScrape.isFound) {
                verbData = secondScrape
                germanTargetWord = canonicalFromGoogle
            }
        }

        // 8. EXTRACT GRAMMAR, ARTICLE, TENSES
        var partOfSpeech = verbData.partOfSpeech
        var article = verbData.article ?: hintedArticle
        var plural = verbData.pluralForm ?: hintedPlural
        var praesens = verbData.praesens
        var praeteritum = verbData.praeteritum
        var partizipZwei = verbData.partizipZwei
        var imperativ = verbData.imperativ
        val hilfsverb = verbData.hilfsverb
        val isSeparable = verbData.isSeparable
        val separablePrefix = verbData.separablePrefix
        val isReflexive = verbData.isReflexive || (partOfSpeech == "VERB" && (GermanVerbHelper.isKnownReflexive(germanTargetWord) || cleanQuery.startsWith("sich ", true)))
        val reflexiveType = verbData.reflexiveType ?: if (isReflexive) "sich" else null
        val regularity = verbData.regularity
        val rawExamples = verbData.exampleSentences.toMutableList()

        // 9. WIKTIONARY FALLBACK IF VERBFORMEN DIDN'T FIND DATA OR ARTICLE IS MISSING
        if (!verbData.isFound || article == null) {
            val fallback = wiktionaryApi.getWiktionaryFallback(germanTargetWord)
            if (fallback.partOfSpeech != null && (!verbData.isFound || partOfSpeech == "OTHER")) {
                partOfSpeech = fallback.partOfSpeech
            }
            if (article == null && fallback.article != null) {
                article = fallback.article
            }
            if (plural == null && fallback.plural != null) {
                plural = fallback.plural
            }
            if (rawExamples.isEmpty() && fallback.examples.isNotEmpty()) {
                rawExamples.addAll(fallback.examples)
            }
        }

        // 9b. Suffix-based article deduction fallback for German nouns
        if (article == null && (partOfSpeech == "NOUN" || germanTargetWord.first().isUpperCase())) {
            val lower = germanTargetWord.lowercase()
            article = when {
                lower.endsWith("ette") || lower.endsWith("ung") || lower.endsWith("heit") ||
                lower.endsWith("keit") || lower.endsWith("schaft") || lower.endsWith("tät") ||
                lower.endsWith("ion") || lower.endsWith("ie") || lower.endsWith("euse") ||
                lower.endsWith("ei") || lower.endsWith("ik") || lower.endsWith("ur") -> "die"
                lower.endsWith("chen") || lower.endsWith("lein") || lower.endsWith("ment") ||
                lower.endsWith("um") || lower.endsWith("tum") -> "das"
                lower.endsWith("ismus") || lower.endsWith("or") || lower.endsWith("ling") ||
                lower.endsWith("ist") -> "der"
                else -> null
            }
            if (article != null && (!verbData.isFound || partOfSpeech == "OTHER")) {
                partOfSpeech = "NOUN"
            }
        }

        // 10. ENSURE VERB FORMS ARE POPULATED
        if (partOfSpeech == "VERB") {
            if (praesens.isNullOrBlank() || praeteritum.isNullOrBlank() || partizipZwei.isNullOrBlank() || imperativ.isNullOrBlank()) {
                val fallbackConj = GermanVerbHelper.conjugate(germanTargetWord)
                if (praesens.isNullOrBlank()) praesens = fallbackConj.praesens
                if (praeteritum.isNullOrBlank()) praeteritum = fallbackConj.praeteritum
                if (partizipZwei.isNullOrBlank()) partizipZwei = fallbackConj.partizipZwei
                if (imperativ.isNullOrBlank()) imperativ = fallbackConj.imperativ
            }
        }

        // 11. ENSURE CAPITALIZATION FOR GERMAN NOUNS
        if (partOfSpeech == "NOUN") {
            germanTargetWord = germanTargetWord.replaceFirstChar { it.uppercase() }
        }

        // 11b. DEEPL / BILINGUAL MEANING ENRICHMENT FALLBACK (Both Turkish and English)
        val effectiveDeepLKey = deepLKey ?: CardPreferencesManager.DEFAULT_DEEPL_KEY
        if (trMeanings.isEmpty() || trMeanings.all { it.contains("Anlam için", true) }) {
            val trResult = translationApi.translateText(germanTargetWord, sourceLang = "de", targetLang = "tr", apiKey = effectiveDeepLKey)
            if (!trResult.isNullOrBlank()) {
                trMeanings.clear()
                trMeanings.add(trResult.trim())
            }
        }

        if (enMeanings.isEmpty() || enMeanings.all { it.contains("Add definition", true) }) {
            val enResult = translationApi.translateText(germanTargetWord, sourceLang = "de", targetLang = "en", apiKey = effectiveDeepLKey)
            if (!enResult.isNullOrBlank()) {
                enMeanings.clear()
                enMeanings.add(enResult.trim())
            }
        }

        // 11c. UNIVERSAL NEVER-EMPTY FALLBACK:
        if (rawExamples.isEmpty()) {
            if (partOfSpeech == "NOUN") {
                val art = article ?: "das"
                rawExamples.add("Das ist $art $germanTargetWord.")
            } else if (partOfSpeech == "VERB") {
                if (isSeparable && separablePrefix != null) {
                    val stem = germanTargetWord.removePrefix(separablePrefix)
                    rawExamples.add("Er ${stem}t oft $separablePrefix.")
                } else {
                    rawExamples.add("Wir möchten heute $germanTargetWord.")
                }
            } else if (cleanQuery.contains(" ")) {
                partOfSpeech = "PHRASE"
                rawExamples.add("Wie sagt man „$germanTargetWord“ auf Deutsch?")
            } else {
                rawExamples.add("Das Wort „$germanTargetWord“ ist sehr nützlich.")
            }
        }

        // 12. TRANSLATE ALL EXAMPLE SENTENCES TO TURKISH
        val examplePairs = mutableListOf<ExamplePair>()
        for (deSentence in rawExamples.take(4)) {
            val trTranslation = translationApi.translateSentence(deSentence, "tr", deepLKey)
            examplePairs.add(ExamplePair(german = deSentence, turkish = trTranslation))
        }

        // 13. CANONICAL DISPLAY WORD (Always Singular Form for Nouns)
        val finalWordName = if (canonicalFromGoogle != null && !hasUmlaut(germanTargetWord) && hasUmlaut(canonicalFromGoogle)) {
            canonicalFromGoogle
        } else {
            germanTargetWord
        }

        val displayWord = if (partOfSpeech == "NOUN") {
            finalWordName.trim().replace(Regex("^(der|die|das)\\s+", RegexOption.IGNORE_CASE), "").replaceFirstChar { it.uppercase() }
        } else {
            finalWordName
        }

        val candidates = mutableListOf<String>()
        val primaryClean = germanTargetWord.trim().replace(Regex("^(der|die|das)\\s+", RegexOption.IGNORE_CASE), "")
        if (primaryClean.isNotBlank()) candidates.add(primaryClean)
        for (alt in resolvedInfo.alternativeGermanWords) {
            val cleanAlt = alt.trim().replace(Regex("^(der|die|das)\\s+", RegexOption.IGNORE_CASE), "")
            if (cleanAlt.isNotBlank() && !candidates.any { it.equals(cleanAlt, ignoreCase = true) }) {
                candidates.add(cleanAlt)
            }
        }
        if (detectedLang == "de") {
            for (sug in suggestions.take(4)) {
                val cleanSug = sug.trim().replace(Regex("^(der|die|das)\\s+", RegexOption.IGNORE_CASE), "")
                if (cleanSug.isNotBlank() && !candidates.any { it.equals(cleanSug, ignoreCase = true) }) {
                    candidates.add(cleanSug)
                }
            }
        }

        val irregularConj = if (partOfSpeech == "VERB") {
            GermanVerbHelper.getFullIrregularConjugation(
                verb = germanTargetWord,
                praesens3Sg = praesens,
                praeteritum3Sg = praeteritum,
                partizip2 = partizipZwei,
                hilfsverb = hilfsverb,
                imperativ = imperativ,
                isSeparable = isSeparable,
                separablePrefix = separablePrefix
            )
        } else null

        val finalRegularity = if (irregularConj != null) "Düzensiz (Unregelmäßig)" else regularity

        val result = WordSearchResult(
            query = cleanQuery,
            germanWord = displayWord,
            sourceLanguage = detectedLang,
            searchedTerm = cleanQuery,
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
            isReflexive = isReflexive,
            reflexiveType = reflexiveType,
            regularity = finalRegularity,
            meaningsTr = if (trMeanings.isEmpty()) listOf("Anlam bulunamadı") else trMeanings,
            meaningsEn = if (enMeanings.isEmpty()) listOf("Definition unavailable") else enMeanings,
            exampleSentences = examplePairs,
            suggestions = suggestions.filter { !it.equals(cleanQuery, ignoreCase = true) && !it.equals(germanTargetWord, ignoreCase = true) },
            correctedFrom = correctedFrom,
            rootLemmaInfo = rootLemmaInfo,
            candidateWords = candidates.take(6),
            irregularConjugation = irregularConj,
            isSuccess = true
        )

        memoryCache.put(cacheKey, result)
        result
    }

    private suspend fun resolveGermanSpelling(
        input: String,
        extraCandidates: List<String> = emptyList()
    ): String {
        val trimmed = input.trim()
        if (hasUmlaut(trimmed) && !trimmed.contains("sh") && !trimmed.contains("ph")) return trimmed

        val prefix = if (trimmed.length > 5) trimmed.take(5) else trimmed
        val suggestions = wiktionaryApi.getSuggestions(prefix)
        for (s in suggestions) {
            val normalizedSuggestion = stripUmlauts(s).lowercase()
            if (normalizedSuggestion == trimmed.lowercase()) {
                return s
            }
        }

        val candidates = (generateUmlautCandidates(trimmed) + extraCandidates).distinct()
        for (cand in candidates) {
            val testSuggestions = wiktionaryApi.getSuggestions(cand)
            val match = testSuggestions.firstOrNull { it.equals(cand, ignoreCase = true) }
            if (match != null) {
                return match
            }
        }

        return trimmed
    }

    private fun hasUmlaut(str: String): Boolean = str.any { it in "äöüÄÖÜß" }

    private fun stripUmlauts(str: String): String {
        return str.replace("ä", "a", true)
            .replace("ö", "o", true)
            .replace("ü", "u", true)
            .replace("ß", "ss", true)
    }

    private fun generateUmlautCandidates(word: String): List<String> {
        val list = mutableListOf<String>()
        val lower = word.lowercase()

        if (lower.endsWith("tat") && lower.contains("a")) {
            val candidate = word.dropLast(3) + "tät"
            list.add(candidate.replaceFirstChar { it.uppercase() })
        }
        if (lower.contains("o")) {
            list.add(lower.replace("o", "ö"))
        }
        if (lower.contains("u")) {
            list.add(lower.replace("u", "ü"))
        }
        if (lower.contains("a") && !lower.contains("ä")) {
            list.add(lower.replaceFirst("a", "ä").replaceFirstChar { it.uppercase() })
        }
        if (lower.contains("ae") || lower.contains("oe") || lower.contains("ue") || lower.contains("ss")) {
            val candidate = lower.replace("ae", "ä")
                .replace("oe", "ö")
                .replace("ue", "ü")
                .replace("ss", "ß")
            list.add(candidate)
        }

        return list
    }
}
