package com.wortmeister.app.data.remote.parser

data class VerbConjugation(
    val praesens: String,
    val praeteritum: String,
    val partizipZwei: String,
    val imperativ: String,
    val hilfsverb: String = "hat",
    val isSeparable: Boolean = false,
    val separablePrefix: String? = null,
    val isReflexive: Boolean = false,
    val reflexiveType: String? = null,
    val regularity: String = "Düzenli (Regelmäßig)"
)

data class FullIrregularConjugation(
    val infinitiv: String,
    val hilfsverb: String, // "hat" or "ist"
    val vowelChange: String?, // e.g. "e → i (Kök Ünlü Değişimi)", "a → ä", etc.
    // Präsens
    val praesensIch: String,
    val praesensDu: String,
    val praesensEr: String,
    val praesensWir: String,
    val praesensIhr: String,
    val praesensSie: String,
    // Präteritum
    val praeteritumIch: String,
    val praeteritumDu: String,
    val praeteritumEr: String,
    val praeteritumWir: String,
    val praeteritumIhr: String,
    val praeteritumSie: String,
    // Perfekt
    val perfektEr: String,
    // Konjunktiv II
    val konjunktivZweiIch: String,
    val konjunktivZweiEr: String,
    // Imperativ
    val imperativDu: String,
    val imperativIhr: String,
    val imperativSie: String
)

object GermanVerbHelper {

    private val SEPARABLE_PREFIXES = listOf(
        "ab", "an", "auf", "aus", "bei", "ein", "fest", "fort", "her", "hin",
        "los", "mit", "nach", "vor", "weg", "weiter", "zu", "zurück", "zusammen"
    )

    private val INSEPARABLE_PREFIXES = listOf(
        "be", "emp", "ent", "er", "ge", "miss", "ver", "zer"
    )

    private val MOTION_OR_CHANGE_OF_STATE_VERBS = setOf(
        "gehen", "kommen", "fahren", "laufen", "fliegen", "bleiben", "sein", "werden",
        "sterben", "wachsen", "fallen", "reisen", "schwimmen", "springen", "rennen",
        "sinken", "steigen", "aufstehen", "einschlafen", "abfahren", "ankommen",
        "umsteigen", "einsteigen", "aussteigen", "geschehen", "passieren"
    )

    // Base irregular / strong verbs: lemma -> (praesens 3.sg, praeteritum 3.sg, partizip 2, hilfsverb, imperativ du/ihr/Sie)
    private val IRREGULAR_BASE_VERBS = mapOf(
        "sein" to IrregularEntry("ist", "war", "gewesen", "ist", "Sei! / Seid! / Seien Sie!"),
        "haben" to IrregularEntry("hat", "hatte", "gehabt", "hat", "Hab! / Habt! / Haben Sie!"),
        "werden" to IrregularEntry("wird", "wurde", "geworden", "ist", "Werde! / Werdet! / Werden Sie!"),
        "gehen" to IrregularEntry("geht", "ging", "gegangen", "ist", "Geh! / Geht! / Gehen Sie!"),
        "kommen" to IrregularEntry("kommt", "kam", "gekommen", "ist", "Komm! / Kommt! / Kommen Sie!"),
        "sehen" to IrregularEntry("sieht", "sah", "gesehen", "hat", "Sieh! / Seht! / Sehen Sie!"),
        "geben" to IrregularEntry("gibt", "gab", "gegeben", "hat", "Gib! / Gebt! / Geben Sie!"),
        "nehmen" to IrregularEntry("nimmt", "nahm", "genommen", "hat", "Nimm! / Nehmt! / Nehmen Sie!"),
        "sprechen" to IrregularEntry("spricht", "sprach", "gesprochen", "hat", "Sprich! / Sprecht! / Sprechen Sie!"),
        "fahren" to IrregularEntry("fährt", "fuhr", "gefahren", "ist", "Fahr! / Fahrt! / Fahren Sie!"),
        "laufen" to IrregularEntry("läuft", "lief", "gelaufen", "ist", "Lauf! / Lauft! / Laufen Sie!"),
        "schlafen" to IrregularEntry("schläft", "schlief", "geschlafen", "hat", "Schlaf! / Schlaft! / Schlafen Sie!"),
        "lesen" to IrregularEntry("liest", "las", "gelesen", "hat", "Lies! / Lest! / Lesen Sie!"),
        "schreiben" to IrregularEntry("schreibt", "schrieb", "geschrieben", "hat", "Schreib! / Schreibt! / Schreiben Sie!"),
        "bleiben" to IrregularEntry("bleibt", "blieb", "geblieben", "ist", "Bleib! / Bleibt! / Bleiben Sie!"),
        "bringen" to IrregularEntry("bringt", "brachte", "gebracht", "hat", "Bring! / Bringt! / Bringen Sie!"),
        "denken" to IrregularEntry("denkt", "dachte", "gedacht", "hat", "Denk! / Denkt! / Denken Sie!"),
        "kennen" to IrregularEntry("kennt", "kannte", "gekannt", "hat", "Kenne! / Kennt! / Kennen Sie!"),
        "wissen" to IrregularEntry("weiß", "wusste", "gewusst", "hat", "Wisse! / Wisst! / Wissen Sie!"),
        "können" to IrregularEntry("kann", "konnte", "gekonnt", "hat", "Könne!"),
        "müssen" to IrregularEntry("muss", "musste", "gemusst", "hat", "Müsse!"),
        "wollen" to IrregularEntry("will", "wollte", "gewollt", "hat", "Wolle!"),
        "sollen" to IrregularEntry("soll", "sollte", "gesollt", "hat", "Solle!"),
        "dürfen" to IrregularEntry("darf", "durfte", "gedurft", "hat", "Dürfe!"),
        "mögen" to IrregularEntry("mag", "mochte", "gemocht", "hat", "Möge!"),
        "finden" to IrregularEntry("findet", "fand", "gefunden", "hat", "Finde! / Findet! / Finden Sie!"),
        "stehen" to IrregularEntry("steht", "stand", "gestanden", "hat", "Steh! / Steht! / Stehen Sie!"),
        "helfen" to IrregularEntry("hilft", "half", "geholfen", "hat", "Hilf! / Helft! / Helfen Sie!"),
        "treffen" to IrregularEntry("trifft", "traf", "getroffen", "hat", "Triff! / Trefft! / Treffen Sie!"),
        "tragen" to IrregularEntry("trägt", "trug", "getragen", "hat", "Trag! / Tragt! / Tragen Sie!"),
        "trinken" to IrregularEntry("trinkt", "trank", "getrunken", "hat", "Trink! / Trinkt! / Trinken Sie!"),
        "essen" to IrregularEntry("isst", "aß", "gegessen", "hat", "Iss! / Esst! / Essen Sie!"),
        "vergessen" to IrregularEntry("vergisst", "vergaß", "vergessen", "hat", "Vergiss! / Vergesst! / Vergessen Sie!"),
        "verlieren" to IrregularEntry("verliert", "verlor", "verloren", "hat", "Verlier! / Verliert! / Verlieren Sie!"),
        "gewinnen" to IrregularEntry("gewinnt", "gewann", "gewonnen", "hat", "Gewinn! / Gewinnt! / Gewinnen Sie!"),
        "beginnen" to IrregularEntry("beginnt", "begann", "begonnen", "hat", "Beginn! / Beginnt! / Beginnen Sie!"),
        "schwimmen" to IrregularEntry("schwimmt", "schwamm", "geschwommen", "ist", "Schwimm! / Schwimmt! / Schwimmen Sie!"),
        "sitzen" to IrregularEntry("sitzt", "saß", "gesessen", "hat", "Sitz! / Sitzt! / Sitzen Sie!"),
        "liegen" to IrregularEntry("liegt", "lag", "gelegen", "hat", "Lieg! / Liegt! / Liegen Sie!"),
        "rufen" to IrregularEntry("ruft", "rief", "gerufen", "hat", "Ruf! / Ruft! / Rufen Sie!"),
        "schneiden" to IrregularEntry("schneidet", "schnitt", "geschnitten", "hat", "Schneide! / Schneidet! / Schneiden Sie!"),
        "fliegen" to IrregularEntry("fliegt", "flog", "geflogen", "ist", "Flieg! / Fliegt! / Fliegen Sie!"),
        "ziehen" to IrregularEntry("zieht", "zog", "gezogen", "hat", "Zieh! / Zieht! / Ziehen Sie!"),
        "schließen" to IrregularEntry("schließt", "schloss", "geschlossen", "hat", "Schließ! / Schließt! / Schließen Sie!"),
        "halten" to IrregularEntry("hält", "hielt", "gehalten", "hat", "Halt! / Haltet! / Halten Sie!"),
        "lassen" to IrregularEntry("lässt", "ließ", "gelassen", "hat", "Lass! / Lasst! / Lassen Sie!"),
        "fallen" to IrregularEntry("fällt", "fiel", "gefallen", "ist", "Fall! / Fallt! / Fallen Sie!"),
        "gefallen" to IrregularEntry("gefällt", "gefiel", "gefallen", "hat", "Gefall! / Gefallt! / Gefallen Sie!"),
        "bieten" to IrregularEntry("bietet", "bot", "geboten", "hat", "Biete! / Bietet! / Bieten Sie!"),
        "bitten" to IrregularEntry("bittet", "bat", "gebeten", "hat", "Bitte! / Bittet! / Bitten Sie!"),
        "waschen" to IrregularEntry("wäscht", "wusch", "gewaschen", "hat", "Wasch! / Wascht! / Waschen Sie!"),
        "werfen" to IrregularEntry("wirft", "warf", "geworfen", "hat", "Wirf! / Werft! / Werfen Sie!"),
        "empfehlen" to IrregularEntry("empfiehlt", "empfahl", "empfohlen", "hat", "Empfiehl! / Empfehlt! / Empfehlen Sie!"),
        "rennen" to IrregularEntry("rennt", "rannte", "gerannt", "ist", "Renn! / Rennt! / Rennen Sie!"),
        "brennen" to IrregularEntry("brennt", "brannte", "gebrannt", "hat", "Brenn! / Brennt! / Brennen Sie!"),
        "nennen" to IrregularEntry("nennt", "nannte", "genannt", "hat", "Nenne! / Nennt! / Nennen Sie!"),
        "senden" to IrregularEntry("sendet", "sendete", "gesendet", "hat", "Sende! / Sendet! / Senden Sie!"),
        "riechen" to IrregularEntry("riecht", "roch", "gerochen", "hat", "Riech! / Riecht! / Riechen Sie!"),
        "streiten" to IrregularEntry("streitet", "stritt", "gestritten", "hat", "Streite! / Streitet! / Streiten Sie!"),
        "wachsen" to IrregularEntry("wächst", "wuchs", "gewachsen", "ist", "Wachse! / Wachst! / Wachsen Sie!"),
        "leiden" to IrregularEntry("leidet", "litt", "gelitten", "hat", "Leide! / Leidet! / Leiden Sie!"),
        "sterben" to IrregularEntry("stirbt", "starb", "gestorben", "ist", "Stirb! / Sterbt! / Sterben Sie!")
    )

    private data class IrregularEntry(
        val praesens: String,
        val praeteritum: String,
        val partizipZwei: String,
        val hilfsverb: String,
        val imperativ: String
    )

    private val REFLEXIVE_VERBS = setOf(
        "erinnern", "freuen", "fühlen", "waschen", "setzen", "beeilen", "befinden",
        "interessieren", "treffen", "vorstellen", "entschuldigen", "verlieben",
        "gewöhnen", "beschäftigen", "wundern", "ärgern", "kämmen", "anziehen",
        "umziehen", "ausziehen", "ausruhen", "bedanken", "bewerben", "entscheiden",
        "erholen", "fürchten", "schämen", "verlaufen", "verspäten", "vorbereiten",
        "konzentrieren", "erkälten", "eignen", "streiten"
    )

    fun isKnownReflexive(verb: String): Boolean {
        val clean = verb.lowercase().trim()
            .removePrefix("sich ")
            .removeSuffix(" sich")
            .replace("(sich)", "")
            .trim()
        return verb.lowercase().startsWith("sich ") || REFLEXIVE_VERBS.contains(clean)
    }

    fun conjugate(rawWord: String): VerbConjugation {
        val rawLower = rawWord.lowercase().trim()
        val isExplicitReflexive = rawLower.startsWith("sich ") || rawLower.endsWith(" sich") || rawLower.contains("(sich)")
        val word = rawLower
            .removePrefix("sich ")
            .removeSuffix(" sich")
            .replace("(sich)", "")
            .trim()

        val isReflexive = isExplicitReflexive || REFLEXIVE_VERBS.contains(word)
        val reflexiveType = if (isReflexive) "sich" else null

        // 1. Check direct irregular match
        IRREGULAR_BASE_VERBS[word]?.let { entry ->
            val hilfsverb = if (MOTION_OR_CHANGE_OF_STATE_VERBS.contains(word)) "ist" else entry.hilfsverb
            val prs = if (isReflexive && !entry.praesens.contains("sich")) "${entry.praesens} sich" else entry.praesens
            val imp = if (isReflexive) {
                "${entry.imperativ.substringBefore("!")} dich! / ${entry.imperativ.substringAfter("/ ").substringBefore("!")} euch! / ${word} Sie sich!"
            } else {
                entry.imperativ
            }
            return VerbConjugation(
                praesens = prs,
                praeteritum = entry.praeteritum,
                partizipZwei = entry.partizipZwei,
                imperativ = imp,
                hilfsverb = hilfsverb,
                isSeparable = false,
                separablePrefix = null,
                isReflexive = isReflexive,
                reflexiveType = reflexiveType,
                regularity = "Düzensiz (Unregelmäßig)"
            )
        }

        // 2. Check compound with separable prefix (e.g., aufstehen, einbrechen, mitbringen)
        val sepPrefix = SEPARABLE_PREFIXES.firstOrNull { word.startsWith(it) && word.length > it.length + 2 }
        if (sepPrefix != null) {
            val baseLemma = word.removePrefix(sepPrefix)
            val baseIrregular = IRREGULAR_BASE_VERBS[baseLemma]

            if (baseIrregular != null) {
                val hilfsverb = if (MOTION_OR_CHANGE_OF_STATE_VERBS.contains(word) || MOTION_OR_CHANGE_OF_STATE_VERBS.contains(baseLemma)) "ist" else baseIrregular.hilfsverb
                val p2 = "$sepPrefix${baseIrregular.partizipZwei}"
                val rawPrs = "${baseIrregular.praesens} $sepPrefix"
                val prs = if (isReflexive && !rawPrs.contains("sich")) "${baseIrregular.praesens} sich $sepPrefix" else rawPrs
                val prt = "${baseIrregular.praeteritum} $sepPrefix"
                val baseImp = baseIrregular.imperativ.split(" / ")
                val imp = if (isReflexive) {
                    baseImp.joinToString(" / ") { "$it dich/euch $sepPrefix" }
                } else {
                    baseImp.joinToString(" / ") { "$it $sepPrefix" }
                }
                return VerbConjugation(
                    praesens = prs,
                    praeteritum = prt,
                    partizipZwei = p2,
                    imperativ = imp,
                    hilfsverb = hilfsverb,
                    isSeparable = true,
                    separablePrefix = sepPrefix,
                    isReflexive = isReflexive,
                    reflexiveType = reflexiveType,
                    regularity = "Düzensiz (Ayrılabilen)"
                )
            } else {
                // Regular separable verb (e.g. einkaufen, aufmachen, vorbereiten)
                val baseStem = getStem(baseLemma)
                val hilfsverb = if (MOTION_OR_CHANGE_OF_STATE_VERBS.contains(word)) "ist" else "hat"
                val basePrs = conjugatePraesens(baseStem)
                val prs = if (isReflexive) "$basePrs sich $sepPrefix" else "$basePrs $sepPrefix"
                val prt = "${conjugatePraeteritum(baseStem)} $sepPrefix"
                val p2 = "$sepPrefix${conjugatePartizipZwei(baseStem, baseLemma)}"
                val imp = if (isReflexive) {
                    "$baseStem dich $sepPrefix! / ${baseStem}t euch $sepPrefix! / $baseLemma Sie sich $sepPrefix!"
                } else {
                    "$baseStem $sepPrefix! / ${baseStem}t $sepPrefix! / $baseLemma Sie $sepPrefix!"
                }
                return VerbConjugation(
                    praesens = prs,
                    praeteritum = prt,
                    partizipZwei = p2,
                    imperativ = imp,
                    hilfsverb = hilfsverb,
                    isSeparable = true,
                    separablePrefix = sepPrefix,
                    isReflexive = isReflexive,
                    reflexiveType = reflexiveType,
                    regularity = "Düzenli (Ayrılabilen)"
                )
            }
        }

        // 3. Check compound with inseparable prefix (e.g., verstehen, beginnen, entkommen)
        val insepPrefix = INSEPARABLE_PREFIXES.firstOrNull { word.startsWith(it) && word.length > it.length + 2 }
        if (insepPrefix != null) {
            val baseLemma = word.removePrefix(insepPrefix)
            val baseIrregular = IRREGULAR_BASE_VERBS[baseLemma]
            if (baseIrregular != null) {
                val hilfsverb = if (MOTION_OR_CHANGE_OF_STATE_VERBS.contains(word)) "ist" else baseIrregular.hilfsverb
                val p2Raw = baseIrregular.partizipZwei
                val p2Clean = if (p2Raw.startsWith("ge")) p2Raw.removePrefix("ge") else p2Raw
                val p2 = "$insepPrefix$p2Clean"
                val basePrs = "$insepPrefix${baseIrregular.praesens}"
                val prs = if (isReflexive) "$basePrs sich" else basePrs
                val prt = "$insepPrefix${baseIrregular.praeteritum}"
                val baseImp = baseIrregular.imperativ.split(" / ")
                val imp = if (isReflexive) {
                    baseImp.joinToString(" / ") { "$insepPrefix$it dich/euch" }
                } else {
                    baseImp.joinToString(" / ") { "$insepPrefix$it" }
                }
                return VerbConjugation(
                    praesens = prs,
                    praeteritum = prt,
                    partizipZwei = p2,
                    imperativ = imp,
                    hilfsverb = hilfsverb,
                    isSeparable = false,
                    separablePrefix = null,
                    isReflexive = isReflexive,
                    reflexiveType = reflexiveType,
                    regularity = "Düzensiz (Ayrılamayan)"
                )
            }
        }

        // 4. Default Regular Conjugation (e.g., machen, lernen, erinnern, freuen)
        val stem = getStem(word)
        val hilfsverb = if (MOTION_OR_CHANGE_OF_STATE_VERBS.contains(word)) "ist" else "hat"
        val basePrs = conjugatePraesens(stem)
        val prs = if (isReflexive) "$basePrs sich" else basePrs
        val prt = conjugatePraeteritum(stem)
        val p2 = conjugatePartizipZwei(stem, word)
        val imp = if (isReflexive) {
            "${stem}e dich! / ${stem}t euch! / $word Sie sich!"
        } else {
            "$stem! / ${stem}t! / $word Sie!"
        }

        return VerbConjugation(
            praesens = prs,
            praeteritum = prt,
            partizipZwei = p2,
            imperativ = imp,
            hilfsverb = hilfsverb,
            isSeparable = false,
            separablePrefix = null,
            isReflexive = isReflexive,
            reflexiveType = reflexiveType,
            regularity = "Düzenli (Regelmäßig)"
        )
    }

    private fun getStem(lemma: String): String {
        return when {
            lemma.endsWith("en") -> lemma.removeSuffix("en")
            lemma.endsWith("n") -> lemma.removeSuffix("n")
            else -> lemma
        }
    }

    private fun conjugatePraesens(stem: String): String {
        return if (stem.endsWith("t") || stem.endsWith("d") || stem.endsWith("tm") || stem.endsWith("chn") || stem.endsWith("fn")) {
            "${stem}et"
        } else {
            "${stem}t"
        }
    }

    private fun conjugatePraeteritum(stem: String): String {
        return if (stem.endsWith("t") || stem.endsWith("d")) {
            "${stem}ete"
        } else {
            "${stem}te"
        }
    }

    private fun conjugatePartizipZwei(stem: String, fullLemma: String): String {
        // -ieren verbs don't take ge- (e.g. studieren -> studiert, reparieren -> repariert)
        if (fullLemma.endsWith("ieren")) {
            return "${stem}t"
        }
        val prefix = INSEPARABLE_PREFIXES.firstOrNull { fullLemma.startsWith(it) }
        val ending = if (stem.endsWith("t") || stem.endsWith("d")) "et" else "t"
        return if (prefix != null) {
            "$stem$ending"
        } else {
            "ge$stem$ending"
        }
    }

    fun getFullIrregularConjugation(
        verb: String,
        praesens3Sg: String?,
        praeteritum3Sg: String?,
        partizip2: String?,
        hilfsverb: String?,
        imperativ: String?,
        isSeparable: Boolean = false,
        separablePrefix: String? = null
    ): FullIrregularConjugation? {
        val word = verb.lowercase().trim()
        val sep = if (isSeparable) (separablePrefix ?: SEPARABLE_PREFIXES.firstOrNull { word.startsWith(it) && word.length > it.length + 2 }) else null
        val baseLemma = if (sep != null) word.removePrefix(sep) else word

        val isKnownIrregular = IRREGULAR_BASE_VERBS.containsKey(baseLemma) ||
                (praeteritum3Sg != null && !praeteritum3Sg.endsWith("te") && !praeteritum3Sg.endsWith("ete")) ||
                (partizip2 != null && partizip2.endsWith("en"))

        if (!isKnownIrregular && !word.endsWith("en")) return null

        val baseEntry = IRREGULAR_BASE_VERBS[baseLemma]

        val hv = when {
            hilfsverb?.contains("ist", true) == true || hilfsverb?.contains("sein", true) == true -> "ist"
            baseEntry?.hilfsverb == "ist" -> "ist"
            MOTION_OR_CHANGE_OF_STATE_VERBS.contains(word) || MOTION_OR_CHANGE_OF_STATE_VERBS.contains(baseLemma) -> "ist"
            else -> "hat"
        }

        val baseStem = getStem(baseLemma)

        // Raw 3.Sg forms without separable prefix
        val cleanPrs3 = (praesens3Sg ?: baseEntry?.praesens ?: conjugatePraesens(baseStem))
            .replace(Regex("^(er|sie|es)\\s+", RegexOption.IGNORE_CASE), "")
            .removeSuffix(" $sep").trim()

        val cleanPrt3 = (praeteritum3Sg ?: baseEntry?.praeteritum ?: conjugatePraeteritum(baseStem))
            .replace(Regex("^(er|sie|es)\\s+", RegexOption.IGNORE_CASE), "")
            .removeSuffix(" $sep").trim()

        val cleanP2 = (partizip2 ?: baseEntry?.partizipZwei ?: conjugatePartizipZwei(baseStem, baseLemma))
            .replace(Regex("^(hat|ist)\\s+", RegexOption.IGNORE_CASE), "")
            .removePrefix(sep ?: "").trim()

        val p2Full = if (sep != null) "$sep$cleanP2" else cleanP2

        // Determine Vowel Shift
        val vowelChange = when {
            baseLemma.contains("e") && (cleanPrs3.contains("i") || cleanPrs3.contains("ie")) -> {
                if (cleanPrs3.contains("ie")) "e → ie (Kök Ünlü Değişimi)" else "e → i (Kök Ünlü Değişimi)"
            }
            baseLemma.contains("a") && cleanPrs3.contains("ä") -> "a → ä (Umlaut)"
            baseLemma.contains("au") && cleanPrs3.contains("äu") -> "au → äu (Umlaut)"
            cleanPrt3 != "${baseStem}te" && cleanPrt3 != "${baseStem}ete" -> "Vokalwechsel (Ablaut: $baseLemma → $cleanPrt3)"
            else -> null
        }

        fun withSep(form: String): String = if (sep != null) "$form $sep" else form

        // Präsens forms
        val prsIch: String
        val prsDu: String
        val prsEr: String = withSep(cleanPrs3)
        val prsWir: String = withSep(baseLemma)
        val prsIhr: String
        val prsSie: String = withSep(baseLemma)

        when (baseLemma) {
            "sein" -> {
                prsIch = withSep("bin")
                prsDu = withSep("bist")
                prsIhr = withSep("seid")
            }
            "haben" -> {
                prsIch = withSep("habe")
                prsDu = withSep("hast")
                prsIhr = withSep("habt")
            }
            "werden" -> {
                prsIch = withSep("werde")
                prsDu = withSep("wirst")
                prsIhr = withSep("werdet")
            }
            "wissen" -> {
                prsIch = withSep("weiß")
                prsDu = withSep("weißt")
                prsIhr = withSep("wisst")
            }
            else -> {
                prsIch = withSep("${baseStem}e")
                prsDu = withSep(when {
                    cleanPrs3.endsWith("t") && !cleanPrs3.endsWith("st") && !cleanPrs3.endsWith("dt") -> {
                        cleanPrs3.removeSuffix("t") + "st"
                    }
                    cleanPrs3.endsWith("s") || cleanPrs3.endsWith("ß") || cleanPrs3.endsWith("z") -> {
                        "${cleanPrs3}t"
                    }
                    else -> "${cleanPrs3}st"
                })
                prsIhr = withSep(when {
                    baseStem.endsWith("t") || baseStem.endsWith("d") -> "${baseStem}et"
                    else -> "${baseStem}t"
                })
            }
        }

        // Präteritum forms
        val prtIch: String
        val prtDu: String
        val prtEr: String = withSep(cleanPrt3)
        val prtWir: String
        val prtIhr: String
        val prtSie: String

        when (baseLemma) {
            "sein" -> {
                prtIch = withSep("war")
                prtDu = withSep("warst")
                prtWir = withSep("waren")
                prtIhr = withSep("wart")
                prtSie = withSep("waren")
            }
            "haben" -> {
                prtIch = withSep("hatte")
                prtDu = withSep("hattest")
                prtWir = withSep("hatten")
                prtIhr = withSep("hattet")
                prtSie = withSep("hatten")
            }
            "werden" -> {
                prtIch = withSep("wurde")
                prtDu = withSep("wurdest")
                prtWir = withSep("wurden")
                prtIhr = withSep("wurdet")
                prtSie = withSep("wurden")
            }
            else -> {
                prtIch = withSep(cleanPrt3)
                prtDu = withSep(when {
                    cleanPrt3.endsWith("t") || cleanPrt3.endsWith("d") || cleanPrt3.endsWith("s") || cleanPrt3.endsWith("ß") -> "${cleanPrt3}est"
                    else -> "${cleanPrt3}st"
                })
                val plStem = if (cleanPrt3.endsWith("e")) cleanPrt3 else "${cleanPrt3}e"
                prtWir = withSep("${plStem}n")
                prtIhr = withSep(when {
                    cleanPrt3.endsWith("t") || cleanPrt3.endsWith("d") -> "${cleanPrt3}et"
                    else -> "${cleanPrt3}t"
                })
                prtSie = withSep("${plStem}n")
            }
        }

        // Konjunktiv II forms
        val kj2Stem = when {
            baseLemma == "sein" -> "wär"
            baseLemma == "haben" -> "hätt"
            baseLemma == "werden" -> "würd"
            baseLemma == "wissen" -> "wüsst"
            baseLemma == "können" -> "könnt"
            baseLemma == "müssen" -> "müsst"
            baseLemma == "dürfen" -> "dürft"
            baseLemma == "mögen" -> "möcht"
            cleanPrt3.contains("a") -> cleanPrt3.replace("a", "ä")
            cleanPrt3.contains("o") -> cleanPrt3.replace("o", "ö")
            cleanPrt3.contains("u") -> cleanPrt3.replace("u", "ü")
            cleanPrt3.endsWith("e") -> cleanPrt3.removeSuffix("e")
            else -> cleanPrt3
        }
        val kj2Ich = withSep(if (kj2Stem.endsWith("e")) kj2Stem else "${kj2Stem}e")
        val kj2Er = kj2Ich

        // Imperativ forms
        val impParts = (imperativ ?: baseEntry?.imperativ ?: "$baseStem! / ${baseStem}t! / $baseLemma Sie!").split(" / ")
        val impDu = if (impParts.isNotEmpty()) impParts[0].trim() else withSep("$baseStem!")
        val impIhr = if (impParts.size > 1) impParts[1].trim() else withSep("${baseStem}t!")
        val impSie = if (impParts.size > 2) impParts[2].trim() else "$baseLemma Sie${if (sep != null) " $sep" else ""}!"

        return FullIrregularConjugation(
            infinitiv = word,
            hilfsverb = hv,
            vowelChange = vowelChange,
            praesensIch = prsIch,
            praesensDu = prsDu,
            praesensEr = prsEr,
            praesensWir = prsWir,
            praesensIhr = prsIhr,
            praesensSie = prsSie,
            praeteritumIch = prtIch,
            praeteritumDu = prtDu,
            praeteritumEr = prtEr,
            praeteritumWir = prtWir,
            praeteritumIhr = prtIhr,
            praeteritumSie = prtSie,
            perfektEr = "$hv $p2Full",
            konjunktivZweiIch = kj2Ich,
            konjunktivZweiEr = kj2Er,
            imperativDu = impDu,
            imperativIhr = impIhr,
            imperativSie = impSie
        )
    }
}
