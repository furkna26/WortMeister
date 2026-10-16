package com.wortmeister.app.data.remote.api

import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

data class TranslationResult(
    val meanings: List<String> = emptyList(),
    val canonicalGermanWord: String? = null
)

data class ResolvedLanguageResult(
    val detectedSourceLang: String, // "tr", "de", "en"
    val originalQuery: String,
    val germanTargetWord: String,
    val alternativeGermanWords: List<String> = emptyList()
)

class TranslationApi(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()
) {
    suspend fun translateText(
        text: String,
        sourceLang: String = "auto",
        targetLang: String = "de",
        apiKey: String? = null
    ): String? = withContext(Dispatchers.IO) {
        val cleanText = text.trim().take(150)
        if (cleanText.isBlank()) return@withContext ""

        // 1. Try DeepL if apiKey provided
        if (!apiKey.isNullOrBlank()) {
            val deepLResult = translateViaDeepL(cleanText, targetLang, apiKey, sourceLang)
            if (!deepLResult.isNullOrBlank()) {
                return@withContext deepLResult
            }
        }

        // 2. Google Translate fallback
        try {
            val encoded = URLEncoder.encode(cleanText, "UTF-8")
            val sl = if (sourceLang == "auto") "auto" else sourceLang.lowercase()
            val tl = targetLang.lowercase()
            val url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=$sl&tl=$tl&dt=t&q=$encoded"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .header("Accept", "*/*")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrBlank()) {
                        val rootArray = JsonParser.parseString(body).asJsonArray
                        if (rootArray.size() > 0 && rootArray.get(0).isJsonArray) {
                            val sentArray = rootArray.get(0).asJsonArray
                            val sb = StringBuilder()
                            for (item in sentArray) {
                                if (item.isJsonArray && item.asJsonArray.size() > 0) {
                                    sb.append(item.asJsonArray.get(0).asString)
                                }
                            }
                            val res = sb.toString().trim()
                            if (res.isNotBlank()) return@withContext res
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 3. MyMemory fallback
        val myMem = translateViaMyMemory(cleanText, if (sourceLang == "auto") "auto" else sourceLang, targetLang)
        if (myMem.isNotBlank()) return@withContext myMem

        null
    }

    suspend fun translateViaDeepL(
        text: String,
        targetLang: String,
        apiKey: String,
        sourceLang: String? = null
    ): String? = withContext(Dispatchers.IO) {
        val trimmedKey = apiKey.trim()
        val trimmedText = text.trim().take(150)
        if (trimmedKey.isBlank() || trimmedText.isBlank()) return@withContext null

        try {
            val endpoint = if (trimmedKey.endsWith(":fx")) {
                "https://api-free.deepl.com/v2/translate"
            } else {
                "https://api.deepl.com/v2/translate"
            }

            val targetCode = when (targetLang.lowercase()) {
                "tr" -> "TR"
                "en", "gb" -> "EN-US"
                "de" -> "DE"
                else -> targetLang.uppercase()
            }

            val formBuilder = FormBody.Builder()
                .add("text", trimmedText)
                .add("target_lang", targetCode)

            if (!sourceLang.isNullOrBlank() && sourceLang != "auto") {
                val sourceCode = when (sourceLang.lowercase()) {
                    "tr" -> "TR"
                    "en", "gb" -> "EN"
                    "de" -> "DE"
                    else -> sourceLang.uppercase()
                }
                formBuilder.add("source_lang", sourceCode)
            }

            val formBody = formBuilder.build()

            val request = Request.Builder()
                .url(endpoint)
                .header("Authorization", "DeepL-Auth-Key $trimmedKey")
                .header("User-Agent", "WortMeister/1.0 (Android)")
                .post(formBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrBlank()) {
                        val parsed = JsonParser.parseString(body).asJsonObject
                        val translations = parsed.getAsJsonArray("translations")
                        if (translations != null && translations.size() > 0) {
                            val res = translations[0].asJsonObject.get("text")?.asString?.trim()
                            if (!res.isNullOrBlank()) {
                                return@withContext res
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        null
    }

    suspend fun resolveToGerman(
        query: String,
        selectedLang: String = "auto",
        deepLApiKey: String? = null
    ): ResolvedLanguageResult = withContext(Dispatchers.IO) {
        val clean = query.trim()
        val encoded = URLEncoder.encode(clean, "UTF-8")
        var detectedLang = if (selectedLang != "auto") selectedLang else "de"
        var germanWord = clean
        val altGerman = mutableListOf<String>()

        try {
            val sl = if (selectedLang == "auto") "auto" else selectedLang
            val url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=$sl&tl=de&dt=t&dt=bd&dt=qc&dt=rm&q=$encoded"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .header("Accept", "*/*")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrBlank()) {
                        val jsonElement = JsonParser.parseString(body)
                        if (jsonElement.isJsonArray) {
                            val rootArray = jsonElement.asJsonArray

                            // Detect language
                            if (rootArray.size() > 2 && rootArray.get(2).isJsonPrimitive) {
                                val detected = rootArray.get(2).asString.lowercase()
                                detectedLang = when (detected) {
                                    "tr" -> "tr"
                                    "en" -> "en"
                                    "de" -> "de"
                                    else -> if (selectedLang != "auto") selectedLang else "de"
                                }
                            }

                            // Translation section
                            if (rootArray.size() > 0 && rootArray.get(0).isJsonArray) {
                                val transArray = rootArray.get(0).asJsonArray
                                if (transArray.size() > 0 && transArray.get(0).isJsonArray) {
                                    val translatedDe = transArray.get(0).asJsonArray.get(0).asString.trim()
                                    if (translatedDe.isNotBlank()) {
                                        germanWord = translatedDe
                                    }
                                }
                            }

                            // Dictionary alternate German translations
                            if (rootArray.size() > 1 && rootArray.get(1).isJsonArray) {
                                val dictArray = rootArray.get(1).asJsonArray
                                for (entry in dictArray) {
                                    if (entry.isJsonArray) {
                                        val sub = entry.asJsonArray
                                        if (sub.size() > 1 && sub.get(1).isJsonArray) {
                                            for (syn in sub.get(1).asJsonArray) {
                                                val synText = syn.asString.trim()
                                                if (synText.isNotBlank() && !altGerman.contains(synText)) {
                                                    altGerman.add(synText)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Fallback for TR or EN if German target is still empty or failed
        if (detectedLang == "tr" && germanWord.equals(clean, ignoreCase = true)) {
            val fallbackDe = translateViaMyMemory(clean, "tr", "de")
            if (fallbackDe.isNotBlank()) germanWord = fallbackDe
        } else if (detectedLang == "en" && germanWord.equals(clean, ignoreCase = true)) {
            val fallbackDe = translateViaMyMemory(clean, "en", "de")
            if (fallbackDe.isNotBlank()) germanWord = fallbackDe
        }

        ResolvedLanguageResult(
            detectedSourceLang = detectedLang,
            originalQuery = clean,
            germanTargetWord = germanWord,
            alternativeGermanWords = altGerman
        )
    }

    suspend fun getTranslations(
        word: String,
        targetLang: String,
        deepLApiKey: String? = null
    ): TranslationResult = withContext(Dispatchers.IO) {
        val results = mutableListOf<String>()
        var canonicalWord: String? = null

        fun addMeaning(rawText: String) {
            val trimmed = rawText.trim()
            if (trimmed.isBlank()) return
            if (trimmed.contains("http", true) || trimmed.contains("{") || trimmed.contains("}")) return
            // Clean artifacts like "(+)" or trailing punctuation
            val cleaned = trimmed.replace(Regex("""\s*\([+]+\)"""), "").trim()
            if (cleaned.isBlank() || cleaned.length > 40) return

            // Split multiple comma/semicolon-separated meanings
            val parts = cleaned.split(Regex("[,;]"))
            for (p in parts) {
                val cleanPart = p.trim().removePrefix("-").removeSuffix("-").trim()
                if (cleanPart.isNotBlank() && cleanPart.length in 2..35 && cleanPart.split(" ").size <= 3 && !results.any { it.equals(cleanPart, ignoreCase = true) }) {
                    results.add(cleanPart)
                }
            }
        }

        // 0. Primary: DeepL official translation if API key is provided
        if (!deepLApiKey.isNullOrBlank()) {
            val deepLMeaning = translateViaDeepL(word.trim(), targetLang, deepLApiKey)
            if (!deepLMeaning.isNullOrBlank()) {
                addMeaning(deepLMeaning)
            }
        }

        // 1. Google Translate for the word
        suspend fun queryGoogle(term: String) {
            try {
                val encoded = URLEncoder.encode(term.trim(), "UTF-8")
                val url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=de&tl=$targetLang&dt=t&dt=bd&dt=qc&dt=rm&q=$encoded"

                val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .header("Accept", "*/*")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body?.string()
                        if (!body.isNullOrBlank()) {
                            val jsonElement = JsonParser.parseString(body)
                            if (jsonElement.isJsonArray) {
                                val rootArray = jsonElement.asJsonArray

                                // 1a. Primary translation (highest frequency, most natural meaning)
                                if (rootArray.size() > 0 && rootArray.get(0).isJsonArray) {
                                    val sentArray = rootArray.get(0).asJsonArray
                                    for (item in sentArray) {
                                        if (item.isJsonArray && item.asJsonArray.size() > 0) {
                                            val text = item.asJsonArray.get(0).asString.trim()
                                            addMeaning(text)
                                        }
                                    }
                                }

                                // 1b. Dictionary synonyms & Canonical Lemma (e.g. "Qualität" for "qualitat")
                                if (rootArray.size() > 1 && rootArray.get(1).isJsonArray) {
                                    val dictArray = rootArray.get(1).asJsonArray
                                    for (entry in dictArray) {
                                        if (entry.isJsonArray) {
                                            val sub = entry.asJsonArray
                                            if (sub.size() > 1 && sub.get(1).isJsonArray) {
                                                // Take top 4 most common dictionary synonyms
                                                for (syn in sub.get(1).asJsonArray.take(4)) {
                                                    addMeaning(syn.asString)
                                                }
                                            }
                                            // Detect canonical lemma
                                            if (sub.size() > 3 && sub.get(3).isJsonPrimitive) {
                                                val detected = sub.get(3).asString.trim()
                                                if (detected.isNotBlank() && detected.any { it in "äöüÄÖÜß" }) {
                                                    canonicalWord = detected
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        queryGoogle(word.trim())

        // If results are sparse and word has uppercase, also query lowercase to get rich dictionary
        val cleanWord = word.trim().replace(Regex("^(der|die|das)\\s+", RegexOption.IGNORE_CASE), "")
        if (results.size < 2 && cleanWord.any { it.isUpperCase() }) {
            queryGoogle(cleanWord.lowercase())
        }

        // 2. Secondary Fallback: MyMemory API ONLY if Google returned fewer than 2 results!
        if (results.size < 2) {
            val myMemoryMeanings = fetchAllMyMemoryMeanings(cleanWord, "de", targetLang)
            for (m in myMemoryMeanings) {
                addMeaning(m)
            }
        }

        TranslationResult(
            meanings = results,
            canonicalGermanWord = canonicalWord
        )
    }

    private fun fetchAllMyMemoryMeanings(text: String, fromLang: String, toLang: String): List<String> {
        val list = mutableListOf<String>()
        try {
            val encoded = URLEncoder.encode(text.trim(), "UTF-8")
            val myMemoryUrl = "https://api.mymemory.translated.net/get?q=$encoded&langpair=$fromLang|$toLang"
            val request = Request.Builder()
                .url(myMemoryUrl)
                .header("User-Agent", "WortMeister/1.0 (Android)")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrBlank()) {
                        val json = JsonParser.parseString(body).asJsonObject

                        // Primary translatedText
                        val respData = json.getAsJsonObject("responseData")
                        val primary = respData?.get("translatedText")?.asString?.trim() ?: ""
                        if (primary.isNotBlank() && !primary.contains("http", true)) {
                            val cleanPrimary = primary.replace(Regex("""\s*\([+]+\)"""), "").trim()
                            if (cleanPrimary.length in 2..35 && cleanPrimary.split(" ").size <= 3) {
                                list.add(cleanPrimary)
                            }
                        }

                        // Parse verified matches only: exact segment match, quality >= 70, match >= 0.95
                        val matches = json.getAsJsonArray("matches")
                        if (matches != null) {
                            for (m in matches) {
                                if (m.isJsonObject) {
                                    val mObj = m.asJsonObject
                                    val segment = mObj.get("segment")?.asString?.trim() ?: ""
                                    val matchScore = mObj.get("match")?.asDouble ?: 0.0
                                    val qualityStr = mObj.get("quality")?.asString ?: "0"
                                    val quality = qualityStr.toIntOrNull() ?: 0

                                    if (segment.equals(text, ignoreCase = true) && quality >= 70 && matchScore >= 0.95) {
                                        val trans = mObj.get("translation")?.asString?.trim() ?: ""
                                        val clean = trans
                                            .replace(Regex("""\s*\([+]+\)"""), "")
                                            .removePrefix("-")
                                            .removeSuffix("-")
                                            .removeSuffix("?")
                                            .trim()
                                        if (clean.isNotBlank() && clean.length in 2..35 && clean.split(" ").size <= 3 && !list.any { it.equals(clean, ignoreCase = true) }) {
                                            list.add(clean)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun translateViaMyMemory(text: String, fromLang: String, toLang: String): String {
        val list = fetchAllMyMemoryMeanings(text, fromLang, toLang)
        return list.firstOrNull() ?: ""
    }

    suspend fun translateSentence(
        sentence: String,
        targetLang: String = "tr",
        deepLApiKey: String? = null
    ): String = withContext(Dispatchers.IO) {
        if (sentence.isBlank()) return@withContext ""

        fun sanitizeSentenceTranslation(raw: String): String {
            var cleaned = raw.trim()
            // Fix tennis/sport "Einzel" mistranslation into Turkish (MT error: single -> bekar)
            if (targetLang == "tr" && sentence.contains("Einzel", ignoreCase = true)) {
                cleaned = cleaned
                    .replace(Regex("(?i)\\bbekarları\\b"), "tekler maçını")
                    .replace(Regex("(?i)\\bbekarlar\\b"), "tekler maçı")
                    .replace(Regex("(?i)\\bbekarlık\\b"), "bireysel")
            }
            return cleaned
        }

        // 0. Primary: DeepL official translation if API key is provided
        if (!deepLApiKey.isNullOrBlank()) {
            val deepLSentence = translateViaDeepL(sentence.trim(), targetLang, deepLApiKey)
            if (!deepLSentence.isNullOrBlank()) {
                return@withContext sanitizeSentenceTranslation(deepLSentence)
            }
        }

        // 1. Google Translate
        try {
            val encoded = URLEncoder.encode(sentence.trim(), "UTF-8")
            val url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=de&tl=$targetLang&dt=t&q=$encoded"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrBlank()) {
                        val rootArray = JsonParser.parseString(body).asJsonArray
                        if (rootArray.size() > 0 && rootArray.get(0).isJsonArray) {
                            val sentArray = rootArray.get(0).asJsonArray
                            val sb = StringBuilder()
                            for (item in sentArray) {
                                if (item.isJsonArray && item.asJsonArray.size() > 0) {
                                    sb.append(item.asJsonArray.get(0).asString)
                                }
                            }
                            val res = sb.toString().trim()
                            if (res.isNotBlank()) return@withContext sanitizeSentenceTranslation(res)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. MyMemory Fallback for sentence
        val myMemoryRes = translateViaMyMemory(sentence, "de", targetLang)
        if (myMemoryRes.isNotBlank()) {
            return@withContext sanitizeSentenceTranslation(myMemoryRes)
        }

        ""
    }
}
