package com.wortmeister.app.data.remote.api

import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class WiktionaryApi(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()
) {
    suspend fun getSuggestions(query: String): List<String> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        val suggestions = mutableListOf<String>()
        try {
            val encoded = URLEncoder.encode(query.trim(), "UTF-8")
            val url = "https://de.wiktionary.org/w/api.php?action=opensearch&search=$encoded&limit=6&format=json"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "WortMeisterApp/1.0 (Android; Language Learning)")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext emptyList()
                val body = response.body?.string() ?: return@withContext emptyList()
                val jsonElement = JsonParser.parseString(body)
                if (jsonElement.isJsonArray) {
                    val rootArray = jsonElement.asJsonArray
                    if (rootArray.size() > 1 && rootArray.get(1).isJsonArray) {
                        val items = rootArray.get(1).asJsonArray
                        for (item in items) {
                            val text = item.asString.trim()
                            if (text.isNotBlank() && !suggestions.contains(text)) {
                                suggestions.add(text)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        suggestions
    }

    suspend fun getWiktionaryFallback(word: String): WiktionaryFallbackData = withContext(Dispatchers.IO) {
        var partOfSpeech: String? = null
        var article: String? = null
        var plural: String? = null
        val examples = mutableListOf<String>()

        try {
            val encoded = URLEncoder.encode(word.trim(), "UTF-8")
            val url = "https://de.wiktionary.org/w/api.php?action=parse&page=$encoded&format=json&prop=wikitext"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "WortMeisterApp/1.0 (Android; Language Learning)")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext WiktionaryFallbackData()
                val body = response.body?.string() ?: return@withContext WiktionaryFallbackData()
                val root = JsonParser.parseString(body).asJsonObject
                val parseObj = root.getAsJsonObject("parse") ?: return@withContext WiktionaryFallbackData()
                val wikitextObj = parseObj.getAsJsonObject("wikitext") ?: return@withContext WiktionaryFallbackData()
                val wikitext = wikitextObj.get("*")?.asString ?: ""

                var baseLemma: String? = null
                var inflectionInfo: String? = null

                // Check for conjugated or declined flexion forms (e.g. ging -> gehen, Häuser -> Haus)
                val isFlexion = wikitext.contains("{{Wortart|Konjugierte Form|Deutsch}}") ||
                        wikitext.contains("{{Wortart|Deklinierte Form|Deutsch}}") ||
                        wikitext.contains("{{Grundformverweis")

                if (isFlexion) {
                    val grundformRegex = Regex("""\{\{Grundformverweis\s*(?:Konj|Dekl)\s*\|\s*([^}]+)\}""")
                    val gMatch = grundformRegex.find(wikitext)
                    if (gMatch != null) {
                        baseLemma = gMatch.groupValues[1].trim()
                    }

                    if (baseLemma.isNullOrBlank()) {
                        val lemmaRegex = Regex("""(?:des Verbs|des Substantivs|des Adjektivs)\s+'''\[\[([^\]]+)\]\]'''|(?:des Verbs|des Substantivs)\s+\[\[([^\]]+)\]\]""")
                        val lMatch = lemmaRegex.find(wikitext)
                        if (lMatch != null) {
                            baseLemma = lMatch.groupValues[1].ifEmpty { lMatch.groupValues[2] }.trim()
                        }
                    }

                    inflectionInfo = when {
                        wikitext.contains("Präteritum", true) -> "Präteritum"
                        wikitext.contains("Partizip II", true) || wikitext.contains("Partizip Perfekt", true) -> "Partizip II"
                        wikitext.contains("Plural", true) -> "Çoğul (Plural)"
                        wikitext.contains("Konjunktiv", true) -> "Konjunktiv"
                        wikitext.contains("Präsens", true) -> "Präsens"
                        wikitext.contains("Imperativ", true) -> "Imperativ"
                        else -> "Çekimli Form"
                    }

                    if (wikitext.contains("des Verbs") || wikitext.contains("Konjugierte Form")) {
                        partOfSpeech = "VERB"
                    } else if (wikitext.contains("des Substantivs") || wikitext.contains("Deklinierte Form")) {
                        partOfSpeech = "NOUN"
                    }
                }

                // Extract Part of Speech if not already set by flexion
                if (partOfSpeech == null) {
                    if (wikitext.contains("{{Wortart|Substantiv|Deutsch}}")) {
                        partOfSpeech = "NOUN"
                    } else if (wikitext.contains("{{Wortart|Verb|Deutsch}}")) {
                        partOfSpeech = "VERB"
                    } else if (wikitext.contains("{{Wortart|Adjektiv|Deutsch}}")) {
                        partOfSpeech = "ADJECTIVE"
                    } else if (wikitext.contains("{{Wortart|Präposition|Deutsch}}")) {
                        partOfSpeech = "PREPOSITION"
                    }
                }

                // Extract Article
                if (partOfSpeech == "NOUN") {
                    if (wikitext.contains("Genus=m") || wikitext.contains("{{m}}")) {
                        article = "der"
                    } else if (wikitext.contains("Genus=f") || wikitext.contains("{{f}}")) {
                        article = "die"
                    } else if (wikitext.contains("Genus=n") || wikitext.contains("{{n}}")) {
                        article = "das"
                    }

                    // Extract Plural
                    val pluralRegex = Regex("""(?:Plural|Pluralform|Pl.)\s*=\s*\[\[([^\]]+)\]\]|(?:Plural|Pl.)\s*=\s*([a-zA-ZäöüÄÖÜß]+)""")
                    val pluralMatch = pluralRegex.find(wikitext)
                    if (pluralMatch != null) {
                        plural = pluralMatch.groupValues[1].ifEmpty { pluralMatch.groupValues[2] }
                        if (!plural.startsWith("die ") && plural.isNotBlank()) {
                            plural = "die $plural"
                        }
                    }
                }

                // Extract Examples from {{Beispiele}}
                val exampleSectionRegex = Regex("""\{\{Beispiele\}\}([\s\S]*?)(?:\{\{|\n\n)""")
                val exampleMatch = exampleSectionRegex.find(wikitext)
                if (exampleMatch != null) {
                    val lines = exampleMatch.groupValues[1].lines()
                    for (line in lines) {
                        val cleaned = line.replace(Regex("""^:[0-9\[\], ]*"""), "")
                            .replace(Regex("""\[\[(?:[^|\]]*\|)?([^\]]+)\]\]"""), "$1")
                            .replace("'''", "")
                            .replace("''", "")
                            .trim()
                        if (cleaned.length > 5 && cleaned.contains(word, ignoreCase = true)) {
                            examples.add(cleaned)
                            if (examples.size >= 3) break
                        }
                    }
                }

                return@withContext WiktionaryFallbackData(
                    partOfSpeech = partOfSpeech,
                    article = article,
                    plural = plural,
                    examples = examples,
                    baseLemma = baseLemma,
                    inflectionInfo = inflectionInfo
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        WiktionaryFallbackData(
            partOfSpeech = partOfSpeech,
            article = article,
            plural = plural,
            examples = examples
        )
    }
}

data class WiktionaryFallbackData(
    val partOfSpeech: String? = null,
    val article: String? = null,
    val plural: String? = null,
    val examples: List<String> = emptyList(),
    val baseLemma: String? = null,
    val inflectionInfo: String? = null
)
