package com.wortmeister.app.data.remote.parser

data class SanitizedQuery(
    val rawQuery: String,
    val cleanedTerm: String,
    val hintedArticle: String? = null,
    val separableVerbCandidate: String? = null,
    val phoneticCandidates: List<String> = emptyList()
)

object SearchNormalizer {

    private val GERMAN_ARTICLES = mapOf(
        "der" to "der",
        "die" to "die",
        "das" to "das",
        "den" to "der",
        "dem" to "der",
        "des" to "der",
        "ein" to null,
        "eine" to "die",
        "einen" to "der",
        "einem" to null,
        "einer" to "die"
    )

    private val OTHER_ARTICLES = listOf("the", "a", "an", "bir")

    val SEPARABLE_PREFIXES = listOf(
        "ab", "an", "auf", "aus", "bei", "ein", "fest", "fort",
        "her", "hin", "los", "mit", "nach", "vor", "weg", "weiter",
        "zu", "zurück", "zusammen"
    )

    fun normalize(raw: String): SanitizedQuery {
        var text = raw.trim()
            .replace(Regex("""^["'„“”«»\p{Punct}]+"""), "")
            .replace(Regex("""["'„“”«»\p{Punct}]+$"""), "")
            .replace(Regex("""\s+"""), " ")
            .trim()

        if (text.isBlank()) {
            return SanitizedQuery(rawQuery = raw, cleanedTerm = "")
        }

        var hintedArticle: String? = null
        val lower = text.lowercase()

        // 1. Extract and strip German articles
        for ((art, canonical) in GERMAN_ARTICLES) {
            val prefix = "$art "
            if (lower.startsWith(prefix)) {
                text = text.substring(prefix.length).trim()
                hintedArticle = canonical
                break
            }
        }

        // 2. Strip English / Turkish determiners
        for (art in OTHER_ARTICLES) {
            val prefix = "$art "
            if (lower.startsWith(prefix)) {
                text = text.substring(prefix.length).trim()
                break
            }
        }

        // 3. Separable verb checks (e.g., "bricht ein" -> "einbrechen")
        var separableCandidate: String? = null
        val words = text.split(" ")
        if (words.size == 2) {
            val w1 = words[0].lowercase()
            val w2 = words[1].lowercase()
            if (SEPARABLE_PREFIXES.contains(w2)) {
                separableCandidate = w2 + w1
            } else if (SEPARABLE_PREFIXES.contains(w1)) {
                separableCandidate = w1 + w2
            }
        }

        // 4. Generate Phonetic and Spelling Variations
        val phoneticList = generatePhoneticVariations(text)

        return SanitizedQuery(
            rawQuery = raw,
            cleanedTerm = text,
            hintedArticle = hintedArticle,
            separableVerbCandidate = separableCandidate,
            phoneticCandidates = phoneticList
        )
    }

    fun generatePhoneticVariations(term: String): List<String> {
        val candidates = mutableSetOf<String>()
        val lower = term.lowercase()

        // sh -> sch (e.g. shreiben -> schreiben, shlafen -> schlafen)
        if (lower.contains("sh")) {
            candidates.add(lower.replace("sh", "sch"))
        }

        // sch -> sh
        if (lower.contains("sch")) {
            candidates.add(lower.replace("sch", "sh"))
        }

        // ph -> f
        if (lower.contains("ph")) {
            candidates.add(lower.replace("ph", "f"))
        }

        // f -> ph
        if (lower.contains("f")) {
            candidates.add(lower.replace("f", "ph"))
        }

        // Digraphs: ae -> ä, oe -> ö, ue -> ü, ss -> ß
        if (lower.contains("ae") || lower.contains("oe") || lower.contains("ue") || lower.contains("ss")) {
            val germanized = lower.replace("ae", "ä")
                .replace("oe", "ö")
                .replace("ue", "ü")
                .replace("ss", "ß")
            candidates.add(germanized)
        }

        // Reverse: ä -> ae, ö -> oe, ü -> ue, ß -> ss
        if (lower.any { it in "äöüß" }) {
            val ascii = lower.replace("ä", "ae")
                .replace("ö", "oe")
                .replace("ü", "ue")
                .replace("ß", "ss")
            candidates.add(ascii)
        }

        // Missing umlauts substitutions:
        // qualitat -> qualität
        if (lower.endsWith("tat") && lower.contains("a")) {
            candidates.add(lower.dropLast(3) + "tät")
        }
        if (lower.contains("o")) {
            candidates.add(lower.replace("o", "ö"))
        }
        if (lower.contains("u")) {
            candidates.add(lower.replace("u", "ü"))
        }
        if (lower.contains("a") && !lower.contains("ä")) {
            candidates.add(lower.replaceFirst("a", "ä"))
        }

        return candidates.toList()
    }

    data class NounPluralInfo(
        val singular: String,
        val article: String,
        val pluralWithDie: String
    )

    private val PLURAL_TO_SINGULAR_MAP: Map<String, Pair<String, String>> = mapOf(
        "häuser" to Pair("Haus", "das"),
        "bücher" to Pair("Buch", "das"),
        "kinder" to Pair("Kind", "das"),
        "männer" to Pair("Mann", "der"),
        "frauen" to Pair("Frau", "die"),
        "autos" to Pair("Auto", "das"),
        "tische" to Pair("Tisch", "der"),
        "stühle" to Pair("Stuhl", "der"),
        "städte" to Pair("Stadt", "die"),
        "länder" to Pair("Land", "das"),
        "wörter" to Pair("Wort", "das"),
        "worte" to Pair("Wort", "das"),
        "menschen" to Pair("Mensch", "der"),
        "tage" to Pair("Tag", "der"),
        "jahre" to Pair("Jahr", "das"),
        "nächte" to Pair("Nacht", "die"),
        "augen" to Pair("Auge", "das"),
        "ohren" to Pair("Ohr", "das"),
        "hände" to Pair("Hand", "die"),
        "freunde" to Pair("Freund", "der"),
        "freundinnen" to Pair("Freundin", "die"),
        "ärzte" to Pair("Arzt", "der"),
        "ärztinnen" to Pair("Ärztin", "die"),
        "züge" to Pair("Zug", "der"),
        "flaschen" to Pair("Flasche", "die"),
        "schulen" to Pair("Schule", "die"),
        "fenster" to Pair("Fenster", "das"),
        "mädchen" to Pair("Mädchen", "das"),
        "brötchen" to Pair("Brötchen", "das"),
        "studenten" to Pair("Student", "der"),
        "studentinnen" to Pair("Studentin", "die"),
        "hunde" to Pair("Hund", "der"),
        "katzen" to Pair("Katze", "die"),
        "bäume" to Pair("Baum", "der"),
        "vögel" to Pair("Vogel", "der"),
        "äpfel" to Pair("Apfel", "der"),
        "töchter" to Pair("Tochter", "die"),
        "mütter" to Pair("Mutter", "die"),
        "väter" to Pair("Vater", "der"),
        "brüder" to Pair("Bruder", "der"),
        "schwestern" to Pair("Schwester", "die"),
        "bilder" to Pair("Bild", "das"),
        "kleider" to Pair("Kleid", "das"),
        "gläser" to Pair("Glas", "das"),
        "dörfer" to Pair("Dorf", "das"),
        "fahrräder" to Pair("Fahrrad", "das"),
        "uhren" to Pair("Uhr", "die"),
        "zeitungen" to Pair("Zeitung", "die"),
        "wohnungen" to Pair("Wohnung", "die"),
        "blumen" to Pair("Blume", "die"),
        "fragen" to Pair("Frage", "die"),
        "probleme" to Pair("Problem", "das"),
        "straßen" to Pair("Straße", "die"),
        "briefe" to Pair("Brief", "der"),
        "berufe" to Pair("Beruf", "der"),
        "betten" to Pair("Bett", "das"),
        "zimmer" to Pair("Zimmer", "das")
    )

    fun resolvePluralToSingular(word: String): NounPluralInfo? {
        val clean = word.trim().lowercase().removePrefix("die ").trim()
        PLURAL_TO_SINGULAR_MAP[clean]?.let { (sing, art) ->
            return NounPluralInfo(
                singular = sing,
                article = art,
                pluralWithDie = "die ${clean.replaceFirstChar { it.uppercase() }}"
            )
        }

        // Rule 1: -innen (feminine job plurals -> -in)
        if (clean.endsWith("innen") && clean.length > 5) {
            val sing = clean.removeSuffix("nen").replaceFirstChar { it.uppercase() }
            return NounPluralInfo(singular = sing, article = "die", pluralWithDie = "die ${clean.replaceFirstChar { it.uppercase() }}")
        }
        // Rule 2: -schaften -> -schaft (die)
        if (clean.endsWith("schaften") && clean.length > 8) {
            val sing = clean.removeSuffix("en").replaceFirstChar { it.uppercase() }
            return NounPluralInfo(singular = sing, article = "die", pluralWithDie = "die ${clean.replaceFirstChar { it.uppercase() }}")
        }
        // Rule 3: -heiten -> -heit (die)
        if (clean.endsWith("heiten") && clean.length > 6) {
            val sing = clean.removeSuffix("en").replaceFirstChar { it.uppercase() }
            return NounPluralInfo(singular = sing, article = "die", pluralWithDie = "die ${clean.replaceFirstChar { it.uppercase() }}")
        }
        // Rule 4: -keiten -> -keit (die)
        if (clean.endsWith("keiten") && clean.length > 6) {
            val sing = clean.removeSuffix("en").replaceFirstChar { it.uppercase() }
            return NounPluralInfo(singular = sing, article = "die", pluralWithDie = "die ${clean.replaceFirstChar { it.uppercase() }}")
        }
        // Rule 5: -ungen -> -ung (die)
        if (clean.endsWith("ungen") && clean.length > 5) {
            val sing = clean.removeSuffix("en").replaceFirstChar { it.uppercase() }
            return NounPluralInfo(singular = sing, article = "die", pluralWithDie = "die ${clean.replaceFirstChar { it.uppercase() }}")
        }
        // Rule 6: -täten -> -tät (die)
        if (clean.endsWith("täten") && clean.length > 5) {
            val sing = clean.removeSuffix("en").replaceFirstChar { it.uppercase() }
            return NounPluralInfo(singular = sing, article = "die", pluralWithDie = "die ${clean.replaceFirstChar { it.uppercase() }}")
        }
        // Rule 7: -ionen -> -ion (die)
        if (clean.endsWith("ionen") && clean.length > 5) {
            val sing = clean.removeSuffix("en").replaceFirstChar { it.uppercase() }
            return NounPluralInfo(singular = sing, article = "die", pluralWithDie = "die ${clean.replaceFirstChar { it.uppercase() }}")
        }
        return null
    }
}
