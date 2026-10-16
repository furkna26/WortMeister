package com.wortmeister.app.data.remote.parser

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.URLDecoder
import java.net.URLEncoder

data class VerbformenData(
    val word: String,
    val partOfSpeech: String, // NOUN, VERB, ADJECTIVE, PREPOSITION, OTHER
    val article: String? = null,
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
    val exampleSentences: List<String> = emptyList(),
    val isFound: Boolean = false
)

class VerbformenScraper {

    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
    private val timeoutMillis = 8000

    suspend fun scrape(query: String): VerbformenData = withContext(Dispatchers.IO) {
        val cleanQuery = query.trim().replace(Regex("^(der|die|das)\\s+", RegexOption.IGNORE_CASE), "")
        val isLikelyNoun = query.trim().startsWith("der ", true) ||
                query.trim().startsWith("die ", true) ||
                query.trim().startsWith("das ", true) ||
                (cleanQuery.isNotEmpty() && cleanQuery[0].isUpperCase())

        if (isLikelyNoun) {
            val nounResult = scrapeNoun(cleanQuery)
            if (nounResult.isFound) return@withContext nounResult

            val verbResult = scrapeVerb(cleanQuery)
            if (verbResult.isFound) return@withContext verbResult
        } else {
            val verbResult = scrapeVerb(cleanQuery)
            if (verbResult.isFound) return@withContext verbResult

            val nounResult = scrapeNoun(cleanQuery)
            if (nounResult.isFound) return@withContext nounResult
        }

        VerbformenData(
            word = cleanQuery,
            partOfSpeech = if (isLikelyNoun) "NOUN" else "OTHER",
            isFound = false
        )
    }

    private fun scrapeNoun(word: String): VerbformenData {
        val capitalized = word.replaceFirstChar { it.uppercase() }
        val result = doScrapeNoun(capitalized)
        if (result.isFound) return result
        if (word != capitalized) {
            val lowercaseResult = doScrapeNoun(word)
            if (lowercaseResult.isFound) return lowercaseResult
        }
        return VerbformenData(word = capitalized, partOfSpeech = "NOUN", isFound = false)
    }

    private fun doScrapeNoun(word: String): VerbformenData {
        try {
            val encoded = URLEncoder.encode(word, "UTF-8")
            val url = "https://www.verbformen.de/deklination/substantive/?w=$encoded"
            val doc = Jsoup.connect(url)
                .userAgent(userAgent)
                .timeout(timeoutMillis)
                .followRedirects(true)
                .get()

            val text = doc.text()
            val isNoun = doc.select("span[title=Substantiv], h1:contains(Substantiv)").isNotEmpty() ||
                    text.contains("Deklination des Substantivs", true)

            if (!isNoun && !text.contains("Der Artikel lautet", true)) {
                return VerbformenData(word = word, partOfSpeech = "NOUN", isFound = false)
            }

            // Article extraction
            var article: String? = null

            // 1. Exact sentence from Verbformen: "Der Artikel lautet „die Zigarette“"
            val artikelSentenceRegex = Regex("""Der Artikel lautet\s*[„"'](der|die|das)\b""", RegexOption.IGNORE_CASE)
            val artikelMatch = artikelSentenceRegex.find(text)
            if (artikelMatch != null) {
                article = artikelMatch.groupValues[1].lowercase()
            }

            // 2. Genus declaration: "ist feminin / ist maskulin / ist neutral"
            if (article == null) {
                val istGenusRegex = Regex("""ist\s+(feminin|maskulin|neutral)\b""", RegexOption.IGNORE_CASE)
                val genusMatch = istGenusRegex.find(text)
                if (genusMatch != null) {
                    article = when (genusMatch.groupValues[1].lowercase()) {
                        "feminin" -> "die"
                        "maskulin" -> "der"
                        "neutral" -> "das"
                        else -> null
                    }
                }
            }

            // 3. HTML Genus badge or title attribute
            if (article == null) {
                val badgeTitle = doc.select("span[title*='Genus'], span[title*='feminin'], span[title*='maskulin'], span[title*='neutral']").firstOrNull()?.attr("title")?.lowercase()
                if (badgeTitle != null) {
                    if (badgeTitle.contains("feminin")) article = "die"
                    else if (badgeTitle.contains("maskulin")) article = "der"
                    else if (badgeTitle.contains("neutral")) article = "das"
                }
            }

            // 4. Netzverb CSS classes
            if (article == null) {
                if (doc.select(".wF").isNotEmpty()) article = "die"
                else if (doc.select(".wN").isNotEmpty()) article = "das"
                else if (doc.select(".wM").isNotEmpty()) article = "der"
            }

            // 5. Morphological suffix rules fallback
            if (article == null) {
                val lower = word.lowercase()
                article = when {
                    lower.endsWith("ung") || lower.endsWith("heit") || lower.endsWith("keit") ||
                    lower.endsWith("schaft") || lower.endsWith("tät") || lower.endsWith("ion") ||
                    lower.endsWith("ie") || lower.endsWith("ette") || lower.endsWith("euse") ||
                    lower.endsWith("ei") || lower.endsWith("ik") || lower.endsWith("ur") -> "die"
                    lower.endsWith("chen") || lower.endsWith("lein") || lower.endsWith("ment") ||
                    lower.endsWith("um") || lower.endsWith("tum") -> "das"
                    lower.endsWith("ismus") || lower.endsWith("or") || lower.endsWith("ling") ||
                    lower.endsWith("ist") -> "der"
                    else -> null
                }
            }

            // Plural form extraction
            var pluralForm: String? = null
            val pluralRegex = Regex("""(?:Nominativ\s+Plural|Plural)\s*[„"']([^„"']+)["'”]""")
            val pluralMatch = pluralRegex.find(text)
            if (pluralMatch != null) {
                val form = pluralMatch.groupValues[1].trim()
                if (form.length in 2..35 && !form.contains("\u2605") && !form.contains("http")) {
                    pluralForm = if (form.startsWith("die ", true)) form else "die $form"
                }
            }

            // Example sentences extraction
            val examples = extractSatzappExamples(doc, word)

            return VerbformenData(
                word = word.replaceFirstChar { it.uppercase() },
                partOfSpeech = "NOUN",
                article = article,
                pluralForm = pluralForm,
                exampleSentences = examples,
                isFound = true
            )
        } catch (e: Exception) {
            return VerbformenData(word = word, partOfSpeech = "NOUN", isFound = false)
        }
    }

    private fun scrapeVerb(word: String): VerbformenData {
        try {
            val encoded = URLEncoder.encode(word, "UTF-8")
            val url = "https://www.verbformen.de/konjugation/?w=$encoded"
            val doc = Jsoup.connect(url)
                .userAgent(userAgent)
                .timeout(timeoutMillis)
                .followRedirects(true)
                .get()

            val text = doc.text()
            val isVerb = text.contains("Konjugieren des Verbs", true) ||
                    text.contains("Die Stammformen von", true) ||
                    doc.select("h2:contains(Infinitiv), h2:contains(Präsens)").isNotEmpty()

            if (!isVerb) {
                return VerbformenData(word = word, partOfSpeech = "VERB", isFound = false)
            }

            // 1. Stammformen: Präsens, Präteritum, Partizip II, Hilfsverb
            var praesens: String? = null
            var praeteritum: String? = null
            var partizipZwei: String? = null
            var hilfsverb: String? = null

            // Primary: parse #stammformen, .vStm, p.vStm
            val stammElem = doc.select("#stammformen, p.vStm, .vStm").firstOrNull()
            if (stammElem != null) {
                val rawStamm = stammElem.text()
                val parts = rawStamm.split(Regex("""[·•\u00B7\u2022\-]""")).map { it.trim() }.filter { it.isNotEmpty() }
                if (parts.size >= 3) {
                    praesens = sanitizeField(parts[0])
                    praeteritum = sanitizeField(parts[1])
                    val p2Raw = parts[2]
                    if (p2Raw.startsWith("ist ", true) || p2Raw.startsWith("er ist ", true)) {
                        hilfsverb = "ist"
                        partizipZwei = sanitizeField(p2Raw.replace(Regex("^(er )?ist ", RegexOption.IGNORE_CASE), ""))
                    } else if (p2Raw.startsWith("hat ", true) || p2Raw.startsWith("er hat ", true)) {
                        hilfsverb = "hat"
                        partizipZwei = sanitizeField(p2Raw.replace(Regex("^(er )?hat ", RegexOption.IGNORE_CASE), ""))
                    } else {
                        partizipZwei = sanitizeField(p2Raw)
                    }
                }
            }

            // Secondary: regex for "Die Stammformen sind ..." or "Die Stammformen von ... sind ..."
            if (praesens.isNullOrBlank() || praeteritum.isNullOrBlank() || partizipZwei.isNullOrBlank()) {
                val altStammRegex = Regex("""(?:Die Stammformen|Stammformen)(?:\s+von\s+[^sind]+)?\s+sind\s+([^,]+),\s+([^\s]+)\s+und\s+([^.\n\u2605]+)""")
                val stammMatch = altStammRegex.find(text)
                if (stammMatch != null) {
                    if (praesens.isNullOrBlank()) praesens = sanitizeField(stammMatch.groupValues[1].replace(Regex("""[„"'\u00BB\u00AB]"""), ""))
                    if (praeteritum.isNullOrBlank()) praeteritum = sanitizeField(stammMatch.groupValues[2].replace(Regex("""[„"'\u00BB\u00AB]"""), ""))
                    val p3 = stammMatch.groupValues[3].replace(Regex("""[„"'\u00BB\u00AB]"""), "").trim()
                    if (hilfsverb.isNullOrBlank()) {
                        if (p3.contains("ist", true)) hilfsverb = "ist"
                        else if (p3.contains("hat", true)) hilfsverb = "hat"
                    }
                    if (partizipZwei.isNullOrBlank()) {
                        partizipZwei = sanitizeField(p3.replace("ist ", "").replace("hat ", ""))
                    }
                }
            }

            // 2. Hilfsverb: sein / haben
            if (hilfsverb.isNullOrBlank()) {
                val hilfsverbRegex = Regex("""Als Hilfsverb von\s+[^ ]+\s+wird\s+["„]([^"”]+)["”]\s+verwendet""")
                val hilfsMatch = hilfsverbRegex.find(text)
                if (hilfsMatch != null) {
                    hilfsverb = sanitizeField(hilfsMatch.groupValues[1])
                }
            }

            // 3. Separable Verb (Ayrılabilen Fiil / trennbar)
            val isSeparable = text.contains("trennbares Verb", true) ||
                    text.contains("· trennbar", true) ||
                    doc.select("span[title*='trennbar']").isNotEmpty()
            
            var separablePrefix: String? = null
            if (isSeparable) {
                val prefixRegex = Regex("""Die Vorsilbe\s+[„"'](.+?)[“"']\s+wird im Satz abgetrennt""")
                val pMatch = prefixRegex.find(text)
                if (pMatch != null) {
                    separablePrefix = sanitizeField(pMatch.groupValues[1])
                } else {
                    val knownPrefixes = listOf("ab", "an", "auf", "aus", "bei", "ein", "fest", "fort", "her", "hin", "los", "mit", "nach", "vor", "weg", "weiter", "zu", "zurück", "zusammen")
                    separablePrefix = knownPrefixes.firstOrNull { word.lowercase().startsWith(it) }
                }
            }

            // 4. Regularity (Regelmäßig / Unregelmäßig)
            val regularity = when {
                text.contains("erfolgt unregelmäßig", true) -> "Düzensiz (Unregelmäßig)"
                text.contains("erfolgt regelmäßig", true) -> "Düzenli (Regelmäßig)"
                else -> null
            }

            // 5. Imperativ extraction from specific DOM table to prevent footer leak!
            var imperativ: String? = null
            val imperativTable = doc.select("div.vTbl:has(h2:contains(Imperativ)) table").firstOrNull()
            if (imperativTable != null) {
                val rows = imperativTable.select("tr")
                val duForm = rows.find { it.text().contains("(du)") }?.select("b")?.text()?.trim()
                val ihrForm = rows.find { it.text().contains("(ihr)") }?.select("b")?.text()?.trim()
                val sieForm = rows.find { it.text().contains("Sie") }?.select("b")?.text()?.trim()

                val parts = mutableListOf<String>()
                if (!duForm.isNullOrBlank()) parts.add("$duForm!")
                if (!ihrForm.isNullOrBlank()) parts.add("$ihrForm!")
                if (!sieForm.isNullOrBlank()) parts.add("$sieForm Sie!")

                if (parts.isNotEmpty()) {
                    imperativ = parts.joinToString(" / ")
                }
            }

            // Fallback for Imperativ if DOM table not found
            if (imperativ.isNullOrBlank()) {
                val answerElem = doc.select("p[itemprop=text]:contains(Der Imperativ)").firstOrNull()
                if (answerElem != null) {
                    val rawAnswer = answerElem.text()
                        .substringAfter("Präsens:", "")
                        .substringBefore("Antworten")
                        .substringBefore("Kommentar")
                        .replace("(du)", "")
                        .replace("(ihr)", "")
                        .replace(" wir,", "")
                        .replace("  ", " ")
                        .trim()
                    imperativ = sanitizeField(rawAnswer)
                }
            }

            // Heuristic & Dictionary fallback with GermanVerbHelper
            val fallbackConj = GermanVerbHelper.conjugate(word)
            if (praesens.isNullOrBlank()) praesens = fallbackConj.praesens
            if (praeteritum.isNullOrBlank()) praeteritum = fallbackConj.praeteritum
            if (partizipZwei.isNullOrBlank()) partizipZwei = fallbackConj.partizipZwei
            if (hilfsverb.isNullOrBlank()) hilfsverb = fallbackConj.hilfsverb
            if (imperativ.isNullOrBlank()) imperativ = fallbackConj.imperativ

            // 6. Reflexive Verb detection (Reflexiv: sich)
            val isReflexive = text.contains("reflexives Verb", true) ||
                    text.contains("ist reflexiv", true) ||
                    text.contains("· reflexiv", true) ||
                    text.contains("sich ", true) ||
                    doc.select("span[title*='reflexiv'], a:contains(reflexiv)").isNotEmpty() ||
                    word.lowercase().startsWith("sich ") ||
                    word.lowercase().endsWith(" sich") ||
                    word.lowercase().contains("(sich)")

            val reflexiveType = when {
                text.contains("reflexiv mit Akkusativ", true) || (text.contains("Akkusativ", true) && isReflexive) -> "sich (Akkusativ)"
                text.contains("reflexiv mit Dativ", true) || (text.contains("Dativ", true) && isReflexive) -> "sich (Dativ)"
                isReflexive -> "sich"
                else -> null
            }

            // 7. Example sentences extraction
            val examples = extractSatzappExamples(doc, word)

            return VerbformenData(
                word = word,
                partOfSpeech = "VERB",
                praesens = praesens,
                praeteritum = praeteritum,
                partizipZwei = partizipZwei,
                imperativ = imperativ,
                hilfsverb = hilfsverb,
                isSeparable = isSeparable || fallbackConj.isSeparable,
                separablePrefix = separablePrefix ?: fallbackConj.separablePrefix,
                isReflexive = isReflexive || fallbackConj.isReflexive,
                reflexiveType = reflexiveType ?: fallbackConj.reflexiveType,
                regularity = regularity ?: fallbackConj.regularity,
                exampleSentences = examples,
                isFound = true
            )
        } catch (e: Exception) {
            val fallbackConj = GermanVerbHelper.conjugate(word)
            return VerbformenData(
                word = word,
                partOfSpeech = "VERB",
                praesens = fallbackConj.praesens,
                praeteritum = fallbackConj.praeteritum,
                partizipZwei = fallbackConj.partizipZwei,
                imperativ = fallbackConj.imperativ,
                hilfsverb = fallbackConj.hilfsverb,
                isSeparable = fallbackConj.isSeparable,
                separablePrefix = fallbackConj.separablePrefix,
                isReflexive = fallbackConj.isReflexive,
                reflexiveType = fallbackConj.reflexiveType,
                regularity = fallbackConj.regularity,
                isFound = true
            )
        }
    }

    private fun sanitizeField(value: String?): String? {
        if (value.isNullOrBlank()) return null
        var clean = value.trim()
        val garbageTokens = listOf(
            "\u2605", "Kommentar", "Netzverb", "Wiktionary", "Lizenz", "Veranstaltung",
            "http", "CC BY", "Urheber", "Nutzungsbedingungen", "Ausführlichkeit",
            "Antworten", "Überprüft", "v3."
        )
        for (token in garbageTokens) {
            if (clean.contains(token, ignoreCase = true)) {
                clean = clean.substringBefore(token).trim()
            }
        }
        clean = clean.replace(Regex("""\s+"""), " ").trim()
        if (clean.length > 55) {
            clean = clean.take(55).trim()
        }
        return clean.ifBlank { null }
    }

    private fun extractSatzappExamples(doc: org.jsoup.nodes.Document, targetWord: String): List<String> {
        val examples = mutableListOf<String>()
        val satzLinks = doc.select("a[href*='satzapp.de/?t=']")
        for (link in satzLinks) {
            val href = link.attr("href")
            val tParam = href.substringAfter("?t=", "")
            if (tParam.isNotEmpty()) {
                val decoded = try {
                    URLDecoder.decode(tParam, "UTF-8").trim()
                } catch (e: Exception) {
                    tParam.trim()
                }
                // Check that it doesn't contain web garbage
                if (decoded.length in 6..120 &&
                    !decoded.contains("\u2605") &&
                    !decoded.contains("http") &&
                    !decoded.contains("Lizenz") &&
                    !examples.contains(decoded)
                ) {
                    examples.add(decoded)
                    if (examples.size >= 4) break
                }
            }
        }
        return examples
    }
}
