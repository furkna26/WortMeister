package com.wortmeister.app.ui.screens.game

import com.wortmeister.app.data.local.entity.GermanCard

object GameQuestionGenerator {

    fun filterEligibleCards(cards: List<GermanCard>, gameType: GameType): List<GermanCard> {
        return when (gameType) {
            GameType.ARTICLE -> cards.filter {
                (it.partOfSpeech.equals("NOUN", ignoreCase = true) || !it.article.isNullOrBlank()) &&
                !it.article.isNullOrBlank() &&
                it.article.lowercase().trim() in listOf("der", "die", "das")
            }
            GameType.VERB -> cards.filter {
                it.partOfSpeech.equals("VERB", ignoreCase = true) || !it.partizipZwei.isNullOrBlank() || !it.praeteritum.isNullOrBlank()
            }
            GameType.NOUN -> cards.filter {
                it.partOfSpeech.equals("NOUN", ignoreCase = true) || !it.article.isNullOrBlank() || !it.pluralForm.isNullOrBlank()
            }
            GameType.ADJECTIVE -> cards.filter {
                it.partOfSpeech.equals("ADJECTIVE", ignoreCase = true) || it.partOfSpeech.equals("ADJ", ignoreCase = true)
            }
            GameType.PHRASE -> cards.filter {
                it.partOfSpeech.uppercase() in listOf("PHRASE", "OTHER") || it.germanWord.trim().contains(" ")
            }
            GameType.MIXED -> cards.filter {
                it.meaningsTr.isNotBlank() || it.meaningsEn.isNotBlank()
            }
        }
    }

    fun generateQuestions(
        eligibleCards: List<GermanCard>,
        allPoolCards: List<GermanCard>,
        gameType: GameType,
        maxQuestions: Int = 15
    ): List<GameQuestion> {
        if (eligibleCards.isEmpty()) return emptyList()

        val shuffledCards = eligibleCards.shuffled().take(maxQuestions)
        val questions = mutableListOf<GameQuestion>()

        for (card in shuffledCards) {
            val q = when (gameType) {
                GameType.ARTICLE -> generateArticleQuestion(card)
                GameType.VERB -> generateVerbQuestion(card, eligibleCards, allPoolCards)
                GameType.NOUN -> generateNounQuestion(card, eligibleCards, allPoolCards)
                GameType.ADJECTIVE -> generateAdjectiveQuestion(card, eligibleCards, allPoolCards)
                GameType.PHRASE -> generatePhraseQuestion(card, allPoolCards)
                GameType.MIXED -> generateMixedQuestion(card, eligibleCards, allPoolCards)
            }
            if (q != null) questions.add(q)
        }

        return questions
    }

    private fun generateArticleQuestion(card: GermanCard): GameQuestion {
        val cleanWord = card.germanWord.trim()
            .replace(Regex("^(der|die|das)\\s+", RegexOption.IGNORE_CASE), "")
            .replaceFirstChar { it.uppercase() }

        val correctArticle = (card.article ?: "das").lowercase().trim()
        val options = listOf("der", "die", "das")
        val correctIndex = options.indexOf(correctArticle).coerceAtLeast(0)

        val pluralInfo = if (!card.pluralForm.isNullOrBlank()) " • Çoğul: ${card.pluralForm}" else ""
        val explanation = "${card.article} $cleanWord$pluralInfo"

        return GameQuestion(
            card = card,
            questionPrompt = "Doğru artikli seçin",
            questionSubject = cleanWord,
            subPrompt = card.meaningsTr.ifBlank { card.meaningsEn },
            options = options,
            correctIndex = correctIndex,
            explanation = explanation,
            audioWord = "${card.article} $cleanWord"
        )
    }

    private fun generatePhraseQuestion(
        card: GermanCard,
        allPoolCards: List<GermanCard>
    ): GameQuestion? {
        val correctMeaning = card.meaningsTr.split(",").firstOrNull()?.trim()
            ?.ifBlank { card.meaningsEn.split(",").firstOrNull()?.trim() } ?: return null

        val otherMeanings = allPoolCards
            .filter { it.id != card.id }
            .mapNotNull { it.meaningsTr.split(",").firstOrNull()?.trim() }
            .filter { it.isNotBlank() && !it.equals(correctMeaning, ignoreCase = true) }
            .distinct()
            .shuffled()
            .take(3)

        if (otherMeanings.size < 3) {
            val fallbacks = listOf("dikkat çekmek", "önlem almak", "göz önünde bulundurmak", "anlamına gelmek")
                .filter { !it.equals(correctMeaning, ignoreCase = true) }
            val combined = (otherMeanings + fallbacks).distinct().take(3)
            val options = (combined + correctMeaning).shuffled()
            return GameQuestion(
                card = card,
                questionPrompt = "Bu kalıbın Türkçe karşılığı nedir?",
                questionSubject = card.germanWord,
                subPrompt = card.exampleSentence,
                options = options,
                correctIndex = options.indexOf(correctMeaning),
                explanation = if (!card.exampleSentence.isNullOrBlank()) "Örnek: ${card.exampleSentence}" else "Anlam: $correctMeaning",
                audioWord = card.germanWord
            )
        }

        val options = (otherMeanings + correctMeaning).shuffled()
        return GameQuestion(
            card = card,
            questionPrompt = "Bu kalıbın Türkçe karşılığı nedir?",
            questionSubject = card.germanWord,
            subPrompt = card.exampleSentence,
            options = options,
            correctIndex = options.indexOf(correctMeaning),
            explanation = if (!card.exampleSentence.isNullOrBlank()) "Örnek: ${card.exampleSentence}" else "Anlam: $correctMeaning",
            audioWord = card.germanWord
        )
    }

    private fun generateVerbQuestion(
        card: GermanCard,
        verbPool: List<GermanCard>,
        allPoolCards: List<GermanCard>
    ): GameQuestion? {
        val hasPartizip = !card.partizipZwei.isNullOrBlank()
        val randomChoice = (0..2).random()

        if (hasPartizip && randomChoice == 0) {
            val correctAns = "${card.hilfsverb ?: "hat"} ${card.partizipZwei}".trim()
            val otherPartizips = verbPool
                .filter { it.id != card.id && !it.partizipZwei.isNullOrBlank() }
                .map { "${it.hilfsverb ?: "hat"} ${it.partizipZwei}".trim() }
                .filter { !it.equals(correctAns, ignoreCase = true) }
                .distinct()
                .shuffled()
                .take(3)

            if (otherPartizips.size >= 2) {
                val options = (otherPartizips + correctAns).distinct().shuffled()
                val praet = if (!card.praeteritum.isNullOrBlank()) " • Präteritum: ${card.praeteritum}" else ""
                return GameQuestion(
                    card = card,
                    questionPrompt = "Bu fiilin Partizip II (Perfekt) hali nedir?",
                    questionSubject = card.germanWord,
                    subPrompt = card.meaningsTr,
                    options = options,
                    correctIndex = options.indexOf(correctAns),
                    explanation = "Perfekt: $correctAns$praet",
                    audioWord = card.germanWord
                )
            }
        }

        if (!card.hilfsverb.isNullOrBlank() && randomChoice == 1) {
            val correctAux = if (card.hilfsverb.equals("ist", ignoreCase = true)) "ist (sein)" else "hat (haben)"
            val options = listOf("hat (haben)", "ist (sein)")
            return GameQuestion(
                card = card,
                questionPrompt = "Bu fiilin Perfekt yardımcı fiili hangisidir?",
                questionSubject = card.germanWord,
                subPrompt = if (!card.partizipZwei.isNullOrBlank()) "Partizip II: ${card.partizipZwei}" else card.meaningsTr,
                options = options,
                correctIndex = options.indexOf(correctAux),
                explanation = "${card.germanWord} fiili „${card.hilfsverb}“ ile çekimlenir.",
                audioWord = card.germanWord
            )
        }

        // Meaning question
        val correctMeaning = card.meaningsTr.split(",").firstOrNull()?.trim()
            ?.ifBlank { card.meaningsEn.split(",").firstOrNull()?.trim() } ?: return null

        val otherMeanings = verbPool
            .filter { it.id != card.id }
            .mapNotNull { it.meaningsTr.split(",").firstOrNull()?.trim() }
            .filter { it.isNotBlank() && !it.equals(correctMeaning, ignoreCase = true) }
            .distinct()
            .shuffled()
            .take(3)

        val finalDistractors = if (otherMeanings.size < 3) {
            val generic = listOf("öğrenmek", "çalışmak", "gitmek", "başlamak", "istemek")
                .filter { !it.equals(correctMeaning, ignoreCase = true) }
            (otherMeanings + generic).distinct().take(3)
        } else otherMeanings

        val options = (finalDistractors + correctMeaning).shuffled()
        val grammarExtra = buildList {
            card.praesens?.let { add("3. Tekil: $it") }
            card.praeteritum?.let { add("Präteritum: $it") }
            card.partizipZwei?.let { add("Partizip II: ${card.hilfsverb ?: "hat"} $it") }
        }.joinToString(" • ")

        return GameQuestion(
            card = card,
            questionPrompt = "Bu fiilin Türkçe karşılığı nedir?",
            questionSubject = card.germanWord,
            subPrompt = grammarExtra.ifBlank { null },
            options = options,
            correctIndex = options.indexOf(correctMeaning),
            explanation = if (grammarExtra.isNotBlank()) "$grammarExtra\nAnlam: $correctMeaning" else "Anlam: $correctMeaning",
            audioWord = card.germanWord
        )
    }

    private fun generateNounQuestion(
        card: GermanCard,
        nounPool: List<GermanCard>,
        allPoolCards: List<GermanCard>
    ): GameQuestion? {
        val hasPlural = !card.pluralForm.isNullOrBlank() && card.pluralForm != "-"
        val randomChoice = (0..1).random()

        val fullWord = "${card.article ?: ""} ${card.germanWord}".trim()

        if (hasPlural && randomChoice == 0) {
            val correctPlural = card.pluralForm!!.trim()
            val otherPlurals = nounPool
                .filter { it.id != card.id && !it.pluralForm.isNullOrBlank() && it.pluralForm != "-" }
                .map { it.pluralForm!!.trim() }
                .filter { !it.equals(correctPlural, ignoreCase = true) }
                .distinct()
                .shuffled()
                .take(3)

            if (otherPlurals.size >= 2) {
                val options = (otherPlurals + correctPlural).distinct().shuffled()
                return GameQuestion(
                    card = card,
                    questionPrompt = "Bu ismin çoğul (Plural) hali hangisidir?",
                    questionSubject = fullWord,
                    subPrompt = card.meaningsTr,
                    options = options,
                    correctIndex = options.indexOf(correctPlural),
                    explanation = "$fullWord • Çoğul: $correctPlural",
                    audioWord = fullWord
                )
            }
        }

        // Meaning question
        val correctMeaning = card.meaningsTr.split(",").firstOrNull()?.trim()
            ?.ifBlank { card.meaningsEn.split(",").firstOrNull()?.trim() } ?: return null

        val otherMeanings = nounPool
            .filter { it.id != card.id }
            .mapNotNull { it.meaningsTr.split(",").firstOrNull()?.trim() }
            .filter { it.isNotBlank() && !it.equals(correctMeaning, ignoreCase = true) }
            .distinct()
            .shuffled()
            .take(3)

        val finalDistractors = if (otherMeanings.size < 3) {
            val generic = listOf("ev", "kitap", "araba", "şehir", "masa")
                .filter { !it.equals(correctMeaning, ignoreCase = true) }
            (otherMeanings + generic).distinct().take(3)
        } else otherMeanings

        val options = (finalDistractors + correctMeaning).shuffled()
        val pluralInfo = if (!card.pluralForm.isNullOrBlank()) " • Çoğul: ${card.pluralForm}" else ""

        return GameQuestion(
            card = card,
            questionPrompt = "Bu ismin Türkçe karşılığı nedir?",
            questionSubject = fullWord,
            subPrompt = card.pluralForm?.let { "Çoğul: $it" },
            options = options,
            correctIndex = options.indexOf(correctMeaning),
            explanation = "$fullWord$pluralInfo • Anlam: $correctMeaning",
            audioWord = fullWord
        )
    }

    private fun generateAdjectiveQuestion(
        card: GermanCard,
        adjectivePool: List<GermanCard>,
        allPoolCards: List<GermanCard>
    ): GameQuestion? {
        val adjectiveGrading = mapOf(
            "gut" to "besser / am besten",
            "viel" to "mehr / am meisten",
            "gern" to "lieber / am liebsten",
            "groß" to "größer / am größten",
            "klein" to "kleiner / am kleinsten",
            "schnell" to "schneller / am schnellsten",
            "langsam" to "langsamer / am langsamsten",
            "alt" to "älter / am ältesten",
            "jung" to "jünger / am jüngsten",
            "hoch" to "höher / am höchsten",
            "nah" to "näher / am nächsten",
            "warm" to "wärmer / am wärmsten",
            "kalt" to "kälter / am kältesten",
            "teuer" to "teurer / am teuersten",
            "billig" to "billiger / am billigsten",
            "stark" to "stärker / am stärksten",
            "schwach" to "schwächer / am schwächsten",
            "hell" to "heller / am hellsten",
            "dunkel" to "dunkler / am dunkelsten",
            "schön" to "schöner / am schönsten",
            "schwer" to "schwerer / am schwersten",
            "leicht" to "leichter / am leichtesten"
        )

        val cleanWord = card.germanWord.lowercase().trim()
        val hasGrading = adjectiveGrading.containsKey(cleanWord)

        if (hasGrading && (0..1).random() == 0) {
            val correctGrading = adjectiveGrading[cleanWord]!!
            val otherGradings = adjectiveGrading.values
                .filter { it != correctGrading }
                .shuffled()
                .take(3)

            val options = (otherGradings + correctGrading).shuffled()
            return GameQuestion(
                card = card,
                questionPrompt = "Bu sıfatın derecelendirmesi (Komparativ / Superlativ) hangisidir?",
                questionSubject = card.germanWord,
                subPrompt = card.meaningsTr.ifBlank { card.meaningsEn },
                options = options,
                correctIndex = options.indexOf(correctGrading),
                explanation = "${card.germanWord} ➔ $correctGrading",
                audioWord = card.germanWord
            )
        }

        // Meaning question
        val correctMeaning = card.meaningsTr.split(",").firstOrNull()?.trim()
            ?.ifBlank { card.meaningsEn.split(",").firstOrNull()?.trim() } ?: return null

        val otherMeanings = adjectivePool
            .filter { it.id != card.id }
            .mapNotNull { it.meaningsTr.split(",").firstOrNull()?.trim() }
            .filter { it.isNotBlank() && !it.equals(correctMeaning, ignoreCase = true) }
            .distinct()
            .shuffled()
            .take(3)

        val finalDistractors = if (otherMeanings.size < 3) {
            val generic = listOf("hızlı", "güzel", "büyük", "akıllı", "sıcak", "kolay", "zor", "pahalı")
                .filter { !it.equals(correctMeaning, ignoreCase = true) }
            (otherMeanings + generic).distinct().take(3)
        } else otherMeanings

        val options = (finalDistractors + correctMeaning).shuffled()
        return GameQuestion(
            card = card,
            questionPrompt = "Bu sıfatın Türkçe karşılığı nedir?",
            questionSubject = card.germanWord,
            subPrompt = if (hasGrading) "Derecelendirme: ${adjectiveGrading[cleanWord]}" else null,
            options = options,
            correctIndex = options.indexOf(correctMeaning),
            explanation = "${card.germanWord} • Anlam: $correctMeaning",
            audioWord = card.germanWord
        )
    }

    private fun generateMixedQuestion(
        card: GermanCard,
        eligibleCards: List<GermanCard>,
        allPoolCards: List<GermanCard>
    ): GameQuestion? {
        val pos = card.partOfSpeech.uppercase()
        return when {
            pos == "NOUN" || !card.article.isNullOrBlank() -> {
                if (!card.article.isNullOrBlank() && (0..1).random() == 0) {
                    generateArticleQuestion(card)
                } else {
                    generateNounQuestion(card, eligibleCards, allPoolCards)
                }
            }
            pos == "VERB" -> generateVerbQuestion(card, eligibleCards, allPoolCards)
            pos == "ADJECTIVE" || pos == "ADJ" -> generateAdjectiveQuestion(card, eligibleCards, allPoolCards)
            pos == "PHRASE" || card.germanWord.contains(" ") -> generatePhraseQuestion(card, allPoolCards)
            else -> generateNounQuestion(card, eligibleCards, allPoolCards)
        }
    }
}
