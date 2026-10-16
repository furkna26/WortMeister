package com.wortmeister.app.data.local.seed

import com.wortmeister.app.data.local.entity.GermanCard
import com.wortmeister.app.data.repository.DeckRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

data class CuratedCardData(
    val word: String,
    val partOfSpeech: String, // NOUN, VERB, ADJECTIVE, PHRASE
    val article: String? = null,
    val plural: String? = null,
    val praesens: String? = null,
    val praeteritum: String? = null,
    val partizipZwei: String? = null,
    val hilfsverb: String? = null,
    val imperativ: String? = null,
    val isSeparable: Boolean = false,
    val separablePrefix: String? = null,
    val meaningsTr: String,
    val meaningsEn: String,
    val exampleSentence: String,
    val isPriority: Boolean = false
)

data class CuratedDeckBundle(
    val name: String,
    val cards: List<CuratedCardData>
)

data class CuratedBundleInfo(
    val name: String,
    val cardCount: Int,
    val language: String,
    val description: String
)

object CuratedDecksSeeder {

    fun getAllBundles(): List<CuratedDeckBundle> {
        val list = mutableListOf<CuratedDeckBundle>()

        // =========================================================================
        // 1. ALMANCA A1: TEMEL BAŞLANGIÇ
        // =========================================================================
        list.add(
            CuratedDeckBundle(
                name = "Almanca A1: Temel Başlangıç",
                cards = listOf(
                    CuratedCardData(
                        word = "sein",
                        partOfSpeech = "VERB",
                        praesens = "ist",
                        praeteritum = "war",
                        partizipZwei = "gewesen",
                        hilfsverb = "ist",
                        imperativ = "Sei! / Seid! / Seien Sie!",
                        meaningsTr = "olmak, bulunmak",
                        meaningsEn = "to be",
                        exampleSentence = "Ich bin Student in Berlin. (Berlin'de öğrenciyim.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "haben",
                        partOfSpeech = "VERB",
                        praesens = "hat",
                        praeteritum = "hatte",
                        partizipZwei = "gehabt",
                        hilfsverb = "hat",
                        imperativ = "Hab! / Habt!",
                        meaningsTr = "sahip olmak",
                        meaningsEn = "to have",
                        exampleSentence = "Ich habe zwei Brüder. (İki erkek kardeşim var.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "kommen",
                        partOfSpeech = "VERB",
                        praesens = "kommt",
                        praeteritum = "kam",
                        partizipZwei = "gekommen",
                        hilfsverb = "ist",
                        imperativ = "Komm!",
                        meaningsTr = "gelmek",
                        meaningsEn = "to come",
                        exampleSentence = "Woher kommst du? (Nerelisin / Nereden geliyorsun?)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "wohnen",
                        partOfSpeech = "VERB",
                        praesens = "wohnt",
                        praeteritum = "wohnte",
                        partizipZwei = "gewohnt",
                        hilfsverb = "hat",
                        imperativ = "Wohn!",
                        meaningsTr = "ikamet etmek, yaşamak",
                        meaningsEn = "to reside, live",
                        exampleSentence = "Wir wohnen in München. (Münih'te yaşıyoruz.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "das Buch",
                        partOfSpeech = "NOUN",
                        article = "das",
                        plural = "die Bücher",
                        meaningsTr = "kitap",
                        meaningsEn = "book",
                        exampleSentence = "Das Buch liegt auf dem Tisch. (Kitap masanın üzerinde duruyor.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "der Hund",
                        partOfSpeech = "NOUN",
                        article = "der",
                        plural = "die Hunde",
                        meaningsTr = "köpek",
                        meaningsEn = "dog",
                        exampleSentence = "Der Hund spielt im Garten. (Köpek bahçede oynuyor.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "die Katze",
                        partOfSpeech = "NOUN",
                        article = "die",
                        plural = "die Katzen",
                        meaningsTr = "kedi",
                        meaningsEn = "cat",
                        exampleSentence = "Die Katze schläft auf dem Sofa. (Kedi koltukta uyuyor.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "das Haus",
                        partOfSpeech = "NOUN",
                        article = "das",
                        plural = "die Häuser",
                        meaningsTr = "ev, bina",
                        meaningsEn = "house",
                        exampleSentence = "Unser Haus ist sehr gemütlich. (Evimiz çok sıcacık ve rahat.)",
                        isPriority = false
                    )
                )
            )
        )

        // =========================================================================
        // 2. ALMANCA A2: GÜNLÜK İLETİŞİM & FİİL ÇEKİMLERİ
        // =========================================================================
        list.add(
            CuratedDeckBundle(
                name = "Almanca A2: Günlük İletişim & Fiil Çekimleri",
                cards = listOf(
                    CuratedCardData(
                        word = "aufstehen",
                        partOfSpeech = "VERB",
                        praesens = "steht auf",
                        praeteritum = "stand auf",
                        partizipZwei = "aufgestanden",
                        hilfsverb = "ist",
                        imperativ = "Steh auf!",
                        isSeparable = true,
                        separablePrefix = "auf",
                        meaningsTr = "yataktan kalkmak, ayağa kalkmak",
                        meaningsEn = "to stand up, get out of bed",
                        exampleSentence = "Ich stehe jeden Tag um 7 Uhr auf. (Her gün saat 7'de kalkarım.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "einkaufen",
                        partOfSpeech = "VERB",
                        praesens = "kauft ein",
                        praeteritum = "kaufte ein",
                        partizipZwei = "eingekauft",
                        hilfsverb = "hat",
                        imperativ = "Kauf ein!",
                        isSeparable = true,
                        separablePrefix = "ein",
                        meaningsTr = "alışveriş yapmak",
                        meaningsEn = "to shop, buy groceries",
                        exampleSentence = "Am Samstag kaufen wir im Supermarkt ein. (Cumartesi süpermarketten alışveriş yaparız.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "mitbringen",
                        partOfSpeech = "VERB",
                        praesens = "bringt mit",
                        praeteritum = "brachte mit",
                        partizipZwei = "mitgebracht",
                        hilfsverb = "hat",
                        imperativ = "Bring mit!",
                        isSeparable = true,
                        separablePrefix = "mit",
                        meaningsTr = "yanında getirmek",
                        meaningsEn = "to bring along",
                        exampleSentence = "Bring bitte deine Notizen mit! (Lütfen notlarını yanında getir!)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "erzählen",
                        partOfSpeech = "VERB",
                        praesens = "erzählt",
                        praeteritum = "erzählte",
                        partizipZwei = "erzählt",
                        hilfsverb = "hat",
                        imperativ = "Erzähl!",
                        meaningsTr = "anlatmak, bahsetmek",
                        meaningsEn = "to tell, narrate",
                        exampleSentence = "Die Großmutter erzählt eine schöne Geschichte. (Büyükanne güzel bir hikaye anlatıyor.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "die Reise",
                        partOfSpeech = "NOUN",
                        article = "die",
                        plural = "die Reisen",
                        meaningsTr = "seyahat, gezi",
                        meaningsEn = "trip, journey",
                        exampleSentence = "Die Reise nach Wien war wunderschön. (Viyana seyahati çok güzeldi.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "der Urlaub",
                        partOfSpeech = "NOUN",
                        article = "der",
                        plural = "die Urlaube",
                        meaningsTr = "tatil, izin",
                        meaningsEn = "vacation, holiday",
                        exampleSentence = "Im Sommer machen wir Urlaub in der Türkei. (Yazın Türkiye'de tatil yapıyoruz.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "helfen",
                        partOfSpeech = "VERB",
                        praesens = "hilft",
                        praeteritum = "half",
                        partizipZwei = "geholfen",
                        hilfsverb = "hat",
                        imperativ = "Hilf!",
                        meaningsTr = "yardım etmek",
                        meaningsEn = "to help",
                        exampleSentence = "Kannst du mir bitte bei den Aufgaben helfen? (Ödevlerde bana yardım edebilir misin?)",
                        isPriority = true
                    )
                )
            )
        )

        // =========================================================================
        // 3. ALMANCA B1: ORTA DÜZEY & GEÇMİŞ ZAMAN
        // =========================================================================
        list.add(
            CuratedDeckBundle(
                name = "Almanca B1: Orta Düzey & Fiil Çekimleri",
                cards = listOf(
                    CuratedCardData(
                        word = "entscheiden",
                        partOfSpeech = "VERB",
                        praesens = "entscheidet",
                        praeteritum = "entschied",
                        partizipZwei = "entschieden",
                        hilfsverb = "hat",
                        imperativ = "Entscheide!",
                        meaningsTr = "karar vermek",
                        meaningsEn = "to decide",
                        exampleSentence = "Wir müssen uns bald für einen Weg entscheiden. (Yakında bir yola karar vermeliyiz.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "teilnehmen",
                        partOfSpeech = "VERB",
                        praesens = "nimmt teil",
                        praeteritum = "nahm teil",
                        partizipZwei = "teilgenommen",
                        hilfsverb = "hat",
                        imperativ = "Nimm teil!",
                        isSeparable = true,
                        separablePrefix = "teil",
                        meaningsTr = "katılmak (an + Dativ)",
                        meaningsEn = "to participate, take part in",
                        exampleSentence = "Alle Schüler nehmen am Sprachwettbewerb teil. (Tüm öğrenciler dil yarışmasına katılıyor.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "bestehen",
                        partOfSpeech = "VERB",
                        praesens = "besteht",
                        praeteritum = "bestand",
                        partizipZwei = "bestanden",
                        hilfsverb = "hat",
                        imperativ = "Bestehe!",
                        meaningsTr = "sınavı geçmek, başarmak, var olmak",
                        meaningsEn = "to pass exam, exist",
                        exampleSentence = "Sie hat die B1-Prüfung mit Bravour bestanden. (B1 sınavını üstün başarıyla geçti.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "die Erfahrung",
                        partOfSpeech = "NOUN",
                        article = "die",
                        plural = "die Erfahrungen",
                        meaningsTr = "tecrübe, deneyim",
                        meaningsEn = "experience",
                        exampleSentence = "Er hat bereits viel Erfahrung im Unterrichten gesammelt. (Ders vermede çoktan epey tecrübe kazandı.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "die Verantwortung",
                        partOfSpeech = "NOUN",
                        article = "die",
                        plural = "die Verantwortungen",
                        meaningsTr = "sorumluluk",
                        meaningsEn = "responsibility",
                        exampleSentence = "Als Lehrer trägt man eine hohe pädagogische Verantwortung. (Bir öğretmen olarak yüksek pedagojik sorumluluk taşınır.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "unterstützen",
                        partOfSpeech = "VERB",
                        praesens = "unterstützt",
                        praeteritum = "unterstützte",
                        partizipZwei = "unterstützt",
                        hilfsverb = "hat",
                        imperativ = "Unterstütze!",
                        meaningsTr = "desteklemek, arka çıkmak",
                        meaningsEn = "to support, assist",
                        exampleSentence = "Die Lehrkraft unterstützt schwächere Schüler individuell. (Öğretmen zayıf öğrencileri bireysel olarak destekler.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "verbessern",
                        partOfSpeech = "VERB",
                        praesens = "verbessert",
                        praeteritum = "verbesserte",
                        partizipZwei = "verbessert",
                        hilfsverb = "hat",
                        imperativ = "Verbessere!",
                        meaningsTr = "geliştirmek, düzeltmek, iyileştirmek",
                        meaningsEn = "to improve, enhance",
                        exampleSentence = "Regelmäßiges Lesen verbessert den Wortschatz deutlich. (Düzenli okumak kelime haznesini belirgin şekilde geliştirir.)",
                        isPriority = false
                    )
                )
            )
        )

        // =========================================================================
        // 4. ALMANCA B2: İLERİ DÜZEY & AKADEMİK KELİMELER
        // =========================================================================
        list.add(
            CuratedDeckBundle(
                name = "Almanca B2: İleri Düzey & Akademik Kelimeler",
                cards = listOf(
                    CuratedCardData(
                        word = "berücksichtigen",
                        partOfSpeech = "VERB",
                        praesens = "berücksichtigt",
                        praeteritum = "berücksichtigte",
                        partizipZwei = "berücksichtigt",
                        hilfsverb = "hat",
                        imperativ = "Berücksichtige!",
                        meaningsTr = "göz önünde bulundurmak, dikkate almak",
                        meaningsEn = "to consider, take into account",
                        exampleSentence = "Bei der Unterrichtsplanung müssen wir das Sprachniveau der Lerner berücksichtigen. (Ders planlamasında öğrencilerin dil seviyesini dikkate almalıyız.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "beeinflussen",
                        partOfSpeech = "VERB",
                        praesens = "beeinflusst",
                        praeteritum = "beeinflusste",
                        partizipZwei = "beeinflusst",
                        hilfsverb = "hat",
                        imperativ = "Beeinflusse!",
                        meaningsTr = "etkilemek, tesir etmek",
                        meaningsEn = "to influence, affect",
                        exampleSentence = "Die Motivation der Lernenden beeinflusst den Lernerfolg maßgeblich. (Öğrenenlerin motivasyonu öğrenme başarısını belirleyici derecede etkiler.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "vertreten",
                        partOfSpeech = "VERB",
                        praesens = "vertritt",
                        praeteritum = "vertrat",
                        partizipZwei = "vertreten",
                        hilfsverb = "hat",
                        imperativ = "Vertritt!",
                        meaningsTr = "savunmak, temsil etmek, vekalet etmek",
                        meaningsEn = "to represent, advocate",
                        exampleSentence = "Der Dozent vertritt eine moderne didaktische Auffassung. (Öğretim görevlisi modern bir didaktik yaklaşımı savunuyor.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "die Herausforderung",
                        partOfSpeech = "NOUN",
                        article = "die",
                        plural = "die Herausforderungen",
                        meaningsTr = "meydan okuma, güçlük, zor görev",
                        meaningsEn = "challenge",
                        exampleSentence = "Heterogene Klassen stellen eine didaktische Herausforderung dar. (Farklı seviyedeki heterojen sınıflar didaktik bir zorluk teşkil eder.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "der Zusammenhang",
                        partOfSpeech = "NOUN",
                        article = "der",
                        plural = "die Zusammenhänge",
                        meaningsTr = "bağlam, ilişki, bağlantı",
                        meaningsEn = "context, connection",
                        exampleSentence = "In diesem Zusammenhang sollten wir die Grammatikregeln vertiefen. (Bu bağlamda dilbilgisi kurallarını derinleştirmeliyiz.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "beitragen",
                        partOfSpeech = "VERB",
                        praesens = "trägt bei",
                        praeteritum = "trug bei",
                        partizipZwei = "beigetragen",
                        hilfsverb = "hat",
                        imperativ = "Trag bei!",
                        isSeparable = true,
                        separablePrefix = "bei",
                        meaningsTr = "katkı sağlamak (zu + Dativ)",
                        meaningsEn = "to contribute to",
                        exampleSentence = "Interaktive Methoden tragen zur aktiven Unterrichtsbeteiligung bei. (Etkileşimli yöntemler derse aktif katılıma katkı sağlar.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "überzeugen",
                        partOfSpeech = "VERB",
                        praesens = "überzeugt",
                        praeteritum = "überzeugte",
                        partizipZwei = "überzeugt",
                        hilfsverb = "hat",
                        imperativ = "Überzeuge!",
                        meaningsTr = "ikna etmek, inandırmak",
                        meaningsEn = "to convince, persuade",
                        exampleSentence = "Die wissenschaftlichen Argumente haben uns vollkommen überzeugt. (Bilimsel argümanlar bizi tamamen ikna etti.)",
                        isPriority = false
                    )
                )
            )
        )

        // =========================================================================
        // 5. ALMANCA C1 / C1+: İLERİ DÜZEY & AKADEMİK (C1/C1+)
        // =========================================================================
        list.add(
            CuratedDeckBundle(
                name = "Almanca C1/C1+: İleri Düzey & Akademik",
                cards = listOf(
                    CuratedCardData(
                        word = "unterrichten",
                        partOfSpeech = "VERB",
                        praesens = "unterrichtet",
                        praeteritum = "unterrichtete",
                        partizipZwei = "unterrichtet",
                        hilfsverb = "hat",
                        imperativ = "Unterrichte!",
                        meaningsTr = "ders vermek, öğretmek, eğitmek",
                        meaningsEn = "to teach, instruct",
                        exampleSentence = "Sie unterrichtet Deutsch als Fremdsprache am Institut. (Enstitüde Yabancı Dil Olarak Almanca dersi veriyor.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "vermitteln",
                        partOfSpeech = "VERB",
                        praesens = "vermittelt",
                        praeteritum = "vermittelte",
                        partizipZwei = "vermittelt",
                        hilfsverb = "hat",
                        imperativ = "Vermittle!",
                        meaningsTr = "aktarmak, öğretmek, kazandırmak",
                        meaningsEn = "to convey, impart, teach",
                        exampleSentence = "Ein guter Dozent vermittelt nicht nur Fachwissen, sondern Methodenkompetenz. (İyi bir eğitmen sadece alan bilgisi değil, yöntem yetkinliği de kazandırır.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "der Spracherwerb",
                        partOfSpeech = "NOUN",
                        article = "der",
                        plural = "-",
                        meaningsTr = "dil edinimi",
                        meaningsEn = "language acquisition",
                        exampleSentence = "Der frühkindliche Spracherwerb verläuft nach universellen linguistischen Prinzipien. (Erken çocukluk dil edinimi evrensel dilbilimsel ilkelere göre işler.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "die Fachsprache",
                        partOfSpeech = "NOUN",
                        article = "die",
                        plural = "die Fachsprachen",
                        meaningsTr = "uzmanlık dili, mesleki terminoloji",
                        meaningsEn = "specialized language, technical terminology",
                        exampleSentence = "In der akademischen Fachsprache werden präzise Begriffe verwendet. (Akademik uzmanlık dilinde kesin terimler kullanılır.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "die Sprachbeherrschung",
                        partOfSpeech = "NOUN",
                        article = "die",
                        plural = "-",
                        meaningsTr = "dil hakimiyeti, dilde yetkinlik",
                        meaningsEn = "language proficiency, mastery of language",
                        exampleSentence = "Eine exzellente Sprachbeherrschung auf C1-Niveau ist für Akademiker unerlässlich. (C1 seviyesinde kusursuz bir dil hakimiyeti araştırmacılar için vazgeçilmezdir.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "hervorheben",
                        partOfSpeech = "VERB",
                        praesens = "hebt hervor",
                        praeteritum = "hob hervor",
                        partizipZwei = "hervorgehoben",
                        hilfsverb = "hat",
                        imperativ = "Heb hervor!",
                        isSeparable = true,
                        separablePrefix = "hervor",
                        meaningsTr = "vurgulamak, dikkat çekmek, altını çizmek",
                        meaningsEn = "to emphasize, highlight, stress",
                        exampleSentence = "Die Studie hob die Relevanz moderner Kommunikationsmethoden hervor. (Çalışma, modern iletişim yöntemlerinin önemini özellikle vurguladı.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "reflektieren",
                        partOfSpeech = "VERB",
                        praesens = "reflektiert",
                        praeteritum = "reflektierte",
                        partizipZwei = "reflektiert",
                        hilfsverb = "hat",
                        imperativ = "Reflektiere!",
                        meaningsTr = "öz değerlendirme yapmak, derinlemesine düşünmek",
                        meaningsEn = "to reflect upon, ponder",
                        exampleSentence = "Fachkräfte sollten ihr eigenes Handeln kontinuierlich reflektieren. (Uzmanlar kendi eylemlerini sürekli olarak öz değerlendirmeye tabi tutmalıdır.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "die Kompetenz",
                        partOfSpeech = "NOUN",
                        article = "die",
                        plural = "die Kompetenzen",
                        meaningsTr = "yetkinlik, beceri, uzmanlık",
                        meaningsEn = "competence, proficiency",
                        exampleSentence = "Kommunikative Kompetenz steht im Mittelpunkt internationaler Zusammenarbeit. (İletişimsel yetkinlik uluslararası işbirliğinin merkezinde yer alır.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "gewährleisten",
                        partOfSpeech = "VERB",
                        praesens = "gewährleistet",
                        praeteritum = "gewährleistete",
                        partizipZwei = "gewährleistet",
                        hilfsverb = "hat",
                        imperativ = "Gewährleiste!",
                        meaningsTr = "temin etmek, garanti altına almak, güvenceye almak",
                        meaningsEn = "to guarantee, ensure, safeguard",
                        exampleSentence = "Strukturierte Abläufe gewährleisten einen reibungslosen Prozess. (Yapılandırılmış süreçler akışın sorunsuz işlemesini temin eder.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "die Methodik",
                        partOfSpeech = "NOUN",
                        article = "die",
                        plural = "die Methodiken",
                        meaningsTr = "yöntembilim, metodoloji",
                        meaningsEn = "methodology, systematic approach",
                        exampleSentence = "Wissenschaftliche Methodik setzt auf systematisches und kritisches Arbeiten. (Bilimsel metodoloji sistematik ve eleştirel çalışmaya dayanır.)",
                        isPriority = false
                    )
                )
            )
        )

        // =========================================================================
        // 6. ALMANCA REDEWENDUNGEN: SIK KULLANILAN DEYİMLER (B1-C1)
        // =========================================================================
        list.add(
            CuratedDeckBundle(
                name = "Almanca Redewendungen (Deyimler & Kalıplar)",
                cards = listOf(
                    CuratedCardData(
                        word = "die Daumen drücken",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "şans dilemek, başarılar dilemek",
                        meaningsEn = "to keep one's fingers crossed",
                        exampleSentence = "Ich drücke dir ganz fest die Daumen für die Prüfung! (Sınavın için sana yürekten şans diliyorum!)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "den Faden verlieren",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "lafın ucunu kaçırmak, konudan sapmak",
                        meaningsEn = "to lose the thread / train of thought",
                        exampleSentence = "Mitten im Vortrag hat der Sprecher vor Aufregung den Faden verloren. (Sunumun ortasında heyecandan lafın ucunu kaçırdı.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "um den heißen Brei herumreden",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "lafı gevelemek, sadede gelmemek, dolandırmak",
                        meaningsEn = "to beat around the bush",
                        exampleSentence = "Komm bitte sofort zur Sache und rede nicht lange um den heißen Brei herum! (Lütfen hemen sadede gel ve lafı geveleyip durma!)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "Schwein haben",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "şansı yaver gitmek, dört ayak üstüne düşmek",
                        meaningsEn = "to be lucky / have a stroke of luck",
                        exampleSentence = "Er hat Schwein gehabt, dass genau dieses Thema drankam. (Tam da bu konu çıktığı için şansı çok yaver gitti.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "zwei Fliegen mit einer Klappe schlagen",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "bir taşla iki kuş vurmak",
                        meaningsEn = "to kill two birds with one stone",
                        exampleSentence = "Durch das Vokabeltraining im Kontext schlagen wir zwei Fliegen mit einer Klappe. (Bağlam içinde kelime çalışarak bir taşla iki kuş vuruyoruz.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "ins Schwarze treffen",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "tam on ikiden vurmak, tam yerinde tespit yapmak",
                        meaningsEn = "to hit the bullseye / hit the nail on the head",
                        exampleSentence = "Mit deiner Analyse hast du voll ins Schwarze getroffen. (Analizinle tam on ikiden vurdun.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "auf den Keks gehen",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "sinirlerine dokunmak, can sıkmak, bıktırmak",
                        meaningsEn = "to get on someone's nerves",
                        exampleSentence = "Der ständige Lärm geht mir tierisch auf den Keks. (Sürekli gürültü fena halde canımı sıkıyor.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "alles unter einen Hut bringen",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "her şeyi bir arada yürütmek, uzlaştırmak, dengelemek",
                        meaningsEn = "to juggle everything, bring under one roof",
                        exampleSentence = "Es ist eine Kunst, Beruf, Familie und Hobbys unter einen Hut zu bringen. (İş, aile ve hobileri bir arada yürütmek bir sanattır.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "ins Fettnäpfchen treten",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "çam devirmek, pot kırmak, gaf yapmak",
                        meaningsEn = "to put one's foot in one's mouth",
                        exampleSentence = "Mit seiner unüberlegten Bemerkung ist er voll ins Fettnäpfchen getreten. (Düşüncesizce yaptığı yorumla fena halde çam devirdi.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "Tomaten auf den Augen haben",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "gözünün önündekini görmemek, kör olmak",
                        meaningsEn = "to be oblivious / not see what is right in front of you",
                        exampleSentence = "Hast du Tomaten auf den Augen? Der Schlüssel liegt direkt vor dir! (Gözünün önündekini görmüyor musun? Anahtar tam önünde duruyor!)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "Ich verstehe nur Bahnhof",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "hiçbir şey anlamıyorum, konuya Fransızım",
                        meaningsEn = "it's all Greek to me / I don't understand a thing",
                        exampleSentence = "Wenn die Physiker fachsimpeln, verstehe ich nur Bahnhof. (Fizikçiler teknik konuşunca hiçbir şey anlamıyorum.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "die Katze im Sack kaufen",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "görmeden / bilmeden satın almak, gözü kapalı işe girmek",
                        meaningsEn = "to buy a pig in a poke",
                        exampleSentence = "Man sollte das Auto erst probefahren, um nicht die Katze im Sack zu kaufen. (Görmeden satın almamak için aracı önce test sürüşüne çıkarmalısın.)",
                        isPriority = false
                    )
                )
            )
        )

        // =========================================================================
        // 7. ALMANCA NOMEN-VERB-VERBINDUNGEN: B2-C1 KALIPLARI
        // =========================================================================
        list.add(
            CuratedDeckBundle(
                name = "Nomen-Verb-Verbindungen (B2-C1 Kalıplar)",
                cards = listOf(
                    CuratedCardData(
                        word = "zur Verfügung stehen",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "kullanıma hazır olmak, emrine amade olmak",
                        meaningsEn = "to be available, at disposal",
                        exampleSentence = "Für Ihre Fragen stehe ich Ihnen jederzeit gerne zur Verfügung. (Sorularınız için her zaman hizmetinizdeyim.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "zur Verfügung stellen",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "kullanıma sunmak, tahsis etmek, sağlamak",
                        meaningsEn = "to provide, make available",
                        exampleSentence = "Das Institut stellt den Studierenden moderne Lehrmaterialien zur Verfügung. (Enstitü öğrencilere modern ders materyalleri sunuyor.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "in Betracht ziehen",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "göz önünde bulundurmak, hesaba katmak",
                        meaningsEn = "to take into consideration",
                        exampleSentence = "Wir müssen alle didaktischen Alternativen ernsthaft in Betracht ziehen. (Tüm didaktik alternatifleri ciddi şekilde hesaba katmalıyız.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "eine Rolle spielen",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "rol oynamak, belirleyici olmak, önemli olmak",
                        meaningsEn = "to play a role / be important",
                        exampleSentence = "Die Lernatmosphäre spielt für den Spracherwerb eine fundamentale Rolle. (Öğrenme ortamı dil edinimi için temel bir rol oynar.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "in Frage kommen",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "söz konusu olmak, ihtimal dahilinde olmak",
                        meaningsEn = "to come into question / be possible",
                        exampleSentence = "Ein Abbruch des Studiums kommt für mich keinesfalls in Frage. (Üniversiteyi bırakmak benim için asla söz konusu olamaz.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "eine Entscheidung treffen",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "karar vermek",
                        meaningsEn = "to make a decision",
                        exampleSentence = "Die Prüfungskommission hat gestern eine endgültige Entscheidung getroffen. (Sınav komisyonu dün nihai bir karar verdi.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "Maßnahmen ergreifen",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "önlem almak, tedbir uygulamak",
                        meaningsEn = "to take measures / action",
                        exampleSentence = "Die Leitung muss umgehend Maßnahmen gegen Verzögerungen ergreifen. (Yönetim gecikmelere karşı derhal önlem almalıdır.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "einen Beitrag leisten",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "katkı sağlamak, katkıda bulunmak (zu + Dativ)",
                        meaningsEn = "to make a contribution",
                        exampleSentence = "Jeder konstruktive Diskussionsbeitrag leistet einen wertvollen Beitrag zum Seminar. (Her yapıcı fikir seminere değerli bir katkı sağlar.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "unter Beweis stellen",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "ispat etmek, yeteneğini kanıtlamak",
                        meaningsEn = "to demonstrate, prove",
                        exampleSentence = "Im Abschlussprojekt stellte das Team seine Kompetenz unter Beweis. (Bitirme projesinde ekip yetkinliğini kanıtladı.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "Kritik üben",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "eleştiri yöneltmek, eleştirmek (an + Dativ)",
                        meaningsEn = "to level criticism, criticize",
                        exampleSentence = "Die Abteilungsleitung übte sachliche und konstruktive Kritik an der Konzeption. (Bölüm yönetimi tasarıma dair yapıcı eleştirilerde bulundu.)",
                        isPriority = false
                    )
                )
            )
        )

        // =========================================================================
        // 8. İNGİLİZCE A1-A2: ESSENTIAL ENGLISH
        // =========================================================================
        list.add(
            CuratedDeckBundle(
                name = "İngilizce A1-A2: Essential English",
                cards = listOf(
                    CuratedCardData(
                        word = "to learn",
                        partOfSpeech = "VERB",
                        praesens = "learns",
                        praeteritum = "learned / learnt",
                        partizipZwei = "learned / learnt",
                        imperativ = "Learn!",
                        meaningsTr = "öğrenmek, bilgi edinmek",
                        meaningsEn = "to learn, acquire knowledge",
                        exampleSentence = "She wants to learn German and English. (O Almanca ve İngilizce öğrenmek istiyor.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "book",
                        partOfSpeech = "NOUN",
                        plural = "books",
                        meaningsTr = "kitap",
                        meaningsEn = "book",
                        exampleSentence = "Reading books improves language skills. (Kitap okumak dil becerilerini geliştirir.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "to speak",
                        partOfSpeech = "VERB",
                        praesens = "speaks",
                        praeteritum = "spoke",
                        partizipZwei = "spoken",
                        imperativ = "Speak!",
                        meaningsTr = "konuşmak",
                        meaningsEn = "to speak, talk",
                        exampleSentence = "Do you speak German fluently? (Akıcı şekilde Almanca konuşuyor musunuz?)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "friend",
                        partOfSpeech = "NOUN",
                        plural = "friends",
                        meaningsTr = "arkadaş, dost",
                        meaningsEn = "friend, companion",
                        exampleSentence = "A good friend always supports you. (İyi bir arkadaş her zaman seni destekler.)",
                        isPriority = false
                    )
                )
            )
        )

        // =========================================================================
        // 9. İNGİLİZCE B1-B2: INTERMEDIATE & PROFESSIONAL
        // =========================================================================
        list.add(
            CuratedDeckBundle(
                name = "İngilizce B1-B2: Intermediate & Professional",
                cards = listOf(
                    CuratedCardData(
                        word = "to achieve",
                        partOfSpeech = "VERB",
                        praesens = "achieves",
                        praeteritum = "achieved",
                        partizipZwei = "achieved",
                        imperativ = "Achieve!",
                        meaningsTr = "başarmak, elde etmek, ulaşmak",
                        meaningsEn = "to achieve, attain, accomplish",
                        exampleSentence = "Hard work helps you achieve your academic goals. (Çok çalışmak akademik hedeflerinize ulaşmanızı sağlar.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "opportunity",
                        partOfSpeech = "NOUN",
                        plural = "opportunities",
                        meaningsTr = "fırsat, olanak",
                        meaningsEn = "opportunity, chance",
                        exampleSentence = "Studying abroad is an incredible opportunity. (Yurtdışında eğitim almak inanılmaz bir fırsattır.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "to encourage",
                        partOfSpeech = "VERB",
                        praesens = "encourages",
                        praeteritum = "encouraged",
                        partizipZwei = "encouraged",
                        imperativ = "Encourage!",
                        meaningsTr = "cesaretlendirmek, teşvik etmek",
                        meaningsEn = "to encourage, motivate",
                        exampleSentence = "Mentors should always encourage active participation. (Rehberler her zaman aktif katılımı teşvik etmelidir.)",
                        isPriority = false
                    )
                )
            )
        )

        // =========================================================================
        // 10. İNGİLİZCE C1/C1+: ADVANCED & ACADEMIC
        // =========================================================================
        list.add(
            CuratedDeckBundle(
                name = "İngilizce C1/C1+: Advanced & Academic",
                cards = listOf(
                    CuratedCardData(
                        word = "methodology",
                        partOfSpeech = "NOUN",
                        plural = "methodologies",
                        meaningsTr = "yöntembilim, metodoloji",
                        meaningsEn = "methodology, a system of methods used in a particular area",
                        exampleSentence = "Innovative methodology fosters critical thinking in researchers. (Yenilikçi metodoloji araştırmacılarda eleştirel düşünmeyi besler.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "to implement",
                        partOfSpeech = "VERB",
                        praesens = "implements",
                        praeteritum = "implemented",
                        partizipZwei = "implemented",
                        imperativ = "Implement!",
                        meaningsTr = "uygulamaya koymak, icra etmek",
                        meaningsEn = "to implement, put into effect",
                        exampleSentence = "We must implement contemporary strategies in research. (Araştırmalarda çağdaş stratejileri uygulamaya koymalıyız.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "comprehensive",
                        partOfSpeech = "ADJECTIVE",
                        meaningsTr = "kapsamlı, etraflı",
                        meaningsEn = "comprehensive, complete and including everything",
                        exampleSentence = "The curriculum provides a comprehensive overview of linguistics. (Müfredat, dilbilimine kapsamlı bir bakış sunar.)",
                        isPriority = false
                    )
                )
            )
        )

        // =========================================================================
        // 11. İNGİLİZCE IDIOMS & PHRASAL VERBS
        // =========================================================================
        list.add(
            CuratedDeckBundle(
                name = "İngilizce Idioms & Phrasal Verbs",
                cards = listOf(
                    CuratedCardData(
                        word = "break the ice",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "buzları eritmek, ilk çekingenliği kırmak",
                        meaningsEn = "to make people feel more relaxed with each other",
                        exampleSentence = "The speaker told a lighthearted joke to break the ice. (Konuşmacı buzları eritmek için neşeli bir şaka yaptı.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "piece of cake",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "çocuk oyuncağı, çok kolay",
                        meaningsEn = "something very easy to do",
                        exampleSentence = "With sufficient preparation, the grammar test was a piece of cake. (Yeterli hazırlıkla dilbilgisi sınavı çocuk oyuncağıydı.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "call it a day",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "paydos etmek, bugünkü çalışmayı sonlandırmak",
                        meaningsEn = "to decide to stop working for the day",
                        exampleSentence = "We have covered five grammar chapters, so let's call it a day. (Beş dilbilgisi ünitesini işledik, artık paydos edelim.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "figure out",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "çözmek, anlamak, kavramak",
                        meaningsEn = "to understand or solve something",
                        exampleSentence = "Students eventually figured out the complex verb rules. (Öğrenciler sonunda karmaşık fiil kurallarını kavradılar.)",
                        isPriority = true
                    )
                )
            )
        )

        // =========================================================================
        // 12. ALMANCA - TÜRKÇE KARŞILIKLI DEYİMLER & ATASÖZLERİ
        // =========================================================================
        list.add(
            CuratedDeckBundle(
                name = "Almanca - Türkçe Karşılıklı Deyimler & Atasözleri",
                cards = listOf(
                    CuratedCardData(
                        word = "Sand in die Augen streuen",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "göz boyamak, yanıltmak, gerçeği gizlemek",
                        meaningsEn = "to pull the wool over someone's eyes, deceive",
                        exampleSentence = "Er versuchte, uns mit schönen Worten Sand in die Augen zu streuen. (Güzel sözlerle gözümüzü boyamaya çalıştı.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "sein Scherflein beitragen",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "çorbada tuzu bulunmak, katkı sağlamak",
                        meaningsEn = "to do one's bit, contribute one's modest share",
                        exampleSentence = "Jeder konnte sein Scherflein zum gemeinsamen Erfolg beitragen. (Herkes ortak başarıya çorbada tuzu bulunacak şekilde katkı sağladı.)",
                        isPriority = true
                    ),
                    CuratedCardData(
                        word = "sich etwas hinter die Ohren schreiben",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "kulağına küpe etmek, aklından hiç çıkarmamak",
                        meaningsEn = "to take something to heart, remember well",
                        exampleSentence = "Diesen wichtigen Hinweis solltest du dir gut hinter die Ohren schreiben. (Bu önemli uyarıyı kulağına iyi küpe etmelisin.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "Steter Tropfen höhlt den Stein",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "damlaya damlaya göl olur, sabırla her şey başarılır",
                        meaningsEn = "constant dripping wears away the stone, patience brings results",
                        exampleSentence = "Tägliches Üben bringt den Durchbruch: Steter Tropfen höhlt den Stein. (Günlük pratik başarıyı getirir: Damlaya damlaya göl olur.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "Öl ins Feuer gießen",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "yangına körükle gitmek, ortalığı kızıştırmak",
                        meaningsEn = "to add fuel to the fire",
                        exampleSentence = "Mit provokanten Aussagen gießt man nur noch Öl ins Feuer. (Kışkırtıcı ifadelerle yalnızca yangına körükle gidilir.)",
                        isPriority = false
                    ),
                    CuratedCardData(
                        word = "die Zähne zusammenbeißen",
                        partOfSpeech = "PHRASE",
                        meaningsTr = "dişini sıkmak, katlanmak, pes etmemek",
                        meaningsEn = "to grit one's teeth, bite the bullet",
                        exampleSentence = "In anstrengenden Phasen muss man einfach die Zähne zusammenbeißen. (Yorucu dönemlerde insan sadece dişini sıkmalıdır.)",
                        isPriority = false
                    )
                )
            )
        )


        // =========================================================================
        // 9. B2 EDEBİYAT & METİN ANALİZİ: BERTOLT BRECHT (HAIFISCHE)
        // =========================================================================
        list.add(
            CuratedDeckBundle(
                name = "Bertolt Brecht: Wenn die Haifische Menschen wären (B2)",
                cards = listOf(
                    CuratedCardData(
                        word = "Kasten",
                        partOfSpeech = "NOUN",
                        article = "der",
                        plural = "die Kästen",
                        meaningsTr = "kutu, sandık, kafes",
                        meaningsEn = "box, crate, cage",
                        exampleSentence = "Die Verkäuferin findet heute in den großen Kästen viele graue Mäuse."
                    ),
                    CuratedCardData(
                        word = "sanitärisch",
                        partOfSpeech = "ADJECTIVE",
                        meaningsTr = "sıhhi, sağlıkla ilgili, hijyenik",
                        meaningsEn = "sanitary, hygienic",
                        exampleSentence = "Die Verletzten am Unfallort brauchen dringend sanitärische Hilfe."
                    ),
                    CuratedCardData(
                        word = "Maßnahmen treffen",
                        partOfSpeech = "PHRASE",
                        praesens = "trifft Maßnahmen",
                        praeteritum = "traf Maßnahmen",
                        partizipZwei = "Maßnahmen getroffen",
                        hilfsverb = "hat",
                        meaningsTr = "önlem almak, tedbir almak",
                        meaningsEn = "to take measures",
                        exampleSentence = "Der Staat will für die Gesundheit der Schüler Maßnahmen gegen Drogen treffen."
                    ),
                    CuratedCardData(
                        word = "Flosse",
                        partOfSpeech = "NOUN",
                        article = "die",
                        plural = "die Flossen",
                        meaningsTr = "yüzgeç",
                        meaningsEn = "fin, flipper",
                        exampleSentence = "Die Schüler zeigen eine Präsentation über die Flossen großer Haifische."
                    ),
                    CuratedCardData(
                        word = "sich verletzen",
                        partOfSpeech = "VERB",
                        praesens = "verletzt sich",
                        praeteritum = "verletzte sich",
                        partizipZwei = "sich verletzt",
                        hilfsverb = "hat",
                        meaningsTr = "yaralanmak, incitmek",
                        meaningsEn = "to injure oneself",
                        exampleSentence = "Die Katze hat sich zu Hause wegen des Erdbebens an der Pfote verletzt."
                    ),
                    CuratedCardData(
                        word = "jemandem einen Verband machen",
                        partOfSpeech = "PHRASE",
                        praesens = "macht einen Verband",
                        praeteritum = "machte einen Verband",
                        partizipZwei = "einen Verband gemacht",
                        hilfsverb = "hat",
                        meaningsTr = "birine pansuman / sargı yapmak",
                        meaningsEn = "to bandage someone",
                        exampleSentence = "Der Arzt konnte dem verwundeten Kind nach dem Unfall einen Verband machen."
                    ),
                    CuratedCardData(
                        word = "trübsinnig",
                        partOfSpeech = "ADJECTIVE",
                        meaningsTr = "hüzünlü, kederli, melankolik",
                        meaningsEn = "gloomy, melancholy",
                        exampleSentence = "Nach der Pensionierung wartet auf den alten Mann ein trübsinniges Leben."
                    ),
                    CuratedCardData(
                        word = "schmecken",
                        partOfSpeech = "VERB",
                        praesens = "schmeckt",
                        praeteritum = "schmeckte",
                        partizipZwei = "geschmeckt",
                        hilfsverb = "hat",
                        meaningsTr = "tadında olmak, lezzeti olmak",
                        meaningsEn = "to taste, to taste good",
                        exampleSentence = "Die schön aussehende Torte mit Erdbeeren schmeckt der Oma nicht gut."
                    ),
                    CuratedCardData(
                        word = "Rachen",
                        partOfSpeech = "NOUN",
                        article = "der",
                        plural = "die Rachen",
                        meaningsTr = "boğaz, yutak, gırtlak",
                        meaningsEn = "throat, jaws",
                        exampleSentence = "Es ist gefährlich für kleine Fische, in den Rachen der Haifische zu schwimmen."
                    ),
                    CuratedCardData(
                        word = "sich aufopfern",
                        partOfSpeech = "VERB",
                        praesens = "opfert sich auf",
                        praeteritum = "opferte sich auf",
                        partizipZwei = "sich aufgeopfert",
                        hilfsverb = "hat",
                        isSeparable = true,
                        separablePrefix = "auf",
                        meaningsTr = "kendini feda etmek, canını adamak",
                        meaningsEn = "to sacrifice oneself",
                        exampleSentence = "Der junge Soldat wollte sich im Krieg für sein Volk aufopfern."
                    ),
                    CuratedCardData(
                        word = "jemandem etwas beibringen",
                        partOfSpeech = "VERB",
                        praesens = "bringt bei",
                        praeteritum = "brachte bei",
                        partizipZwei = "beigebracht",
                        hilfsverb = "hat",
                        isSeparable = true,
                        separablePrefix = "bei",
                        meaningsTr = "birine bir şeyi öğretmek, belletmek",
                        meaningsEn = "to teach someone something",
                        exampleSentence = "Der Lehrer bringt den Schülern im Unterricht die schweren Präpositionen bei."
                    ),
                    CuratedCardData(
                        word = "Gehorsam",
                        partOfSpeech = "NOUN",
                        article = "der",
                        meaningsTr = "itaat, boyun eğme",
                        meaningsEn = "obedience",
                        exampleSentence = "In manchen Ländern müssen die armen Leute schnellen Gehorsam lernen."
                    ),
                    CuratedCardData(
                        word = "sich vor etwas hüten",
                        partOfSpeech = "VERB",
                        praesens = "hütet sich vor",
                        praeteritum = "hütete sich vor",
                        partizipZwei = "sich gehütet",
                        hilfsverb = "hat",
                        meaningsTr = "bir şeyden sakınmak, korunmak",
                        meaningsEn = "to beware of, to guard against",
                        exampleSentence = "Das Kind soll lernen, sich vor tollwütigen Hunden auf der Straße zu hüten."
                    ),
                    CuratedCardData(
                        word = "melden",
                        partOfSpeech = "VERB",
                        praesens = "meldet",
                        praeteritum = "meldete",
                        partizipZwei = "gemeldet",
                        hilfsverb = "hat",
                        meaningsTr = "bildirmek, ihbar etmek, haber vermek",
                        meaningsEn = "to report, to notify",
                        exampleSentence = "Der Schüler meldete dem Direktor den gefährlichen Unfall im Unterricht."
                    ),
                    CuratedCardData(
                        word = "Neigung",
                        partOfSpeech = "NOUN",
                        article = "die",
                        plural = "die Neigungen",
                        meaningsTr = "eğilim, meyil, heves",
                        meaningsEn = "inclination, tendency",
                        exampleSentence = "Der süße Hund zeigt eine neue Neigung zum Spiel mit dem Ball."
                    ),
                    CuratedCardData(
                        word = "verraten",
                        partOfSpeech = "VERB",
                        praesens = "verrät",
                        praeteritum = "verriet",
                        partizipZwei = "verraten",
                        hilfsverb = "hat",
                        meaningsTr = "ihanet etmek, ele vermek, sırrı açığa vurmak",
                        meaningsEn = "to betray, to disclose",
                        exampleSentence = "Der Dieb wollte der Polizei den Diebstahl seines Freundes verraten."
                    ),
                    CuratedCardData(
                        word = "Orden",
                        partOfSpeech = "NOUN",
                        article = "der",
                        plural = "die Orden",
                        meaningsTr = "madalya, nişan",
                        meaningsEn = "medal, decoration, order",
                        exampleSentence = "Der junge Professor bekommt einen Orden für wichtige Verdienste im Ausland."
                    ),
                    CuratedCardData(
                        word = "Seetang",
                        partOfSpeech = "NOUN",
                        article = "der",
                        meaningsTr = "deniz yosunu",
                        meaningsEn = "seaweed, kelp",
                        exampleSentence = "Viele Menschen essen zu Mittag leckere asiatische Gerichte mit Seetang."
                    ),
                    CuratedCardData(
                        word = "jemandem etwas anheften",
                        partOfSpeech = "VERB",
                        praesens = "heftet an",
                        praeteritum = "heftete an",
                        partizipZwei = "angeheftet",
                        hilfsverb = "hat",
                        isSeparable = true,
                        separablePrefix = "an",
                        meaningsTr = "birine bir şeyi iliştirmek, takmak",
                        meaningsEn = "to pin on, to attach",
                        exampleSentence = "Der Lehrer will den fleißigen Schülern eine rote Schleife anheften."
                    ),
                    CuratedCardData(
                        word = "den Titel verleihen",
                        partOfSpeech = "PHRASE",
                        praesens = "verleiht den Titel",
                        praeteritum = "verlieh den Titel",
                        partizipZwei = "den Titel verliehen",
                        hilfsverb = "hat",
                        meaningsTr = "unvan bahşetmek, unvan vermek",
                        meaningsEn = "to confer a title",
                        exampleSentence = "Der Schuldirektor will der Lehrkraft den Titel Doktor verleihen."
                    ),
                    CuratedCardData(
                        word = "prächtig",
                        partOfSpeech = "ADJECTIVE",
                        meaningsTr = "görkemli, şahane, muhteşem",
                        meaningsEn = "splendid, magnificent",
                        exampleSentence = "Manche reiche Sänger leben im Ausland in prächtigen Häusern."
                    ),
                    CuratedCardData(
                        word = "sich tummeln",
                        partOfSpeech = "VERB",
                        praesens = "tummelt sich",
                        praeteritum = "tummelte sich",
                        partizipZwei = "sich getummelt",
                        hilfsverb = "hat",
                        meaningsTr = "oynaşmak, kaynaşmak, cıvıldaşmak",
                        meaningsEn = "to frolic, to bustle about",
                        exampleSentence = "Die kleinen Fische tummeln sich abends im See."
                    ),
                    CuratedCardData(
                        word = "Meeresgrund",
                        partOfSpeech = "NOUN",
                        article = "der",
                        plural = "die Meeresgründe",
                        meaningsTr = "deniz tabanı, deniz dibi",
                        meaningsEn = "seabed, ocean floor",
                        exampleSentence = "Auf dem Meeresgrund befinden sich Seetang und bunte Fische."
                    ),
                    CuratedCardData(
                        word = "einlullen",
                        partOfSpeech = "VERB",
                        praesens = "lullt ein",
                        praeteritum = "lullte ein",
                        partizipZwei = "eingelullt",
                        hilfsverb = "hat",
                        isSeparable = true,
                        separablePrefix = "ein",
                        meaningsTr = "ninniyle uyutmak, uyuşturmak, aldatıcı huzur vermek",
                        meaningsEn = "to lull to sleep, to lull",
                        exampleSentence = "Die Mutter will das Baby in der Wohnung mit Spielzeugen einlullen."
                    ),
                    CuratedCardData(
                        word = "strömen",
                        partOfSpeech = "VERB",
                        praesens = "strömt",
                        praeteritum = "strömte",
                        partizipZwei = "geströmt",
                        hilfsverb = "ist",
                        meaningsTr = "akın etmek, oluk oluk akmak",
                        meaningsEn = "to stream, to pour",
                        exampleSentence = "Die kleinen Fische strömen im Meer in die falsche Richtung."
                    ),
                    CuratedCardData(
                        word = "Ämter bekommen",
                        partOfSpeech = "PHRASE",
                        praesens = "bekommt Ämter",
                        praeteritum = "bekam Ämter",
                        partizipZwei = "Ämter bekommen",
                        hilfsverb = "hat",
                        meaningsTr = "resmi makam / mevki elde etmek, göreve atanmak",
                        meaningsEn = "to obtain official posts",
                        exampleSentence = "Die Absolventen bekommen später wichtige Ämter in den Behörden."
                    ),
                    CuratedCardData(
                        word = "Brocken",
                        partOfSpeech = "NOUN",
                        article = "der",
                        plural = "die Brocken",
                        meaningsTr = "iri lokma, parça, kırıntı",
                        meaningsEn = "chunk, morsel, lump",
                        exampleSentence = "Die Katzen finden in den Mülltonnen Brocken vom Brot, um satt zu werden."
                    ),
                    CuratedCardData(
                        word = "Ordnung",
                        partOfSpeech = "NOUN",
                        article = "die",
                        plural = "die Ordnungen",
                        meaningsTr = "düzen, asayiş, intizam",
                        meaningsEn = "order, discipline",
                        exampleSentence = "In der Klasse muss während des Unterrichts stets Ordnung herrschen."
                    ),
                    CuratedCardData(
                        word = "sorgen für etwas",
                        partOfSpeech = "VERB",
                        praesens = "sorgt für",
                        praeteritum = "sorgte für",
                        partizipZwei = "für ... gesorgt",
                        hilfsverb = "hat",
                        meaningsTr = "bir şeyi sağlamak, temin etmek (+ Akk.)",
                        meaningsEn = "to provide for, to ensure (+ Akk.)",
                        exampleSentence = "Die Lehrer sollen für dauerhafte Stille in der Klasse sorgen."
                    ),
                    CuratedCardData(
                        word = "etwas zu fressen bekommen",
                        partOfSpeech = "PHRASE",
                        praesens = "bekommt zu fressen",
                        praeteritum = "bekam zu fressen",
                        partizipZwei = "zu fressen bekommen",
                        hilfsverb = "hat",
                        meaningsTr = "yiyecek / yem bulmak, karnı doyurulmak",
                        meaningsEn = "to get fed (animals)",
                        exampleSentence = "Wegen der Tierliebhaber bekommen die Straßenhunde jeden Tag Brot zu fressen."
                    ),
                    CuratedCardData(
                        word = "Haifisch",
                        partOfSpeech = "NOUN",
                        article = "der",
                        plural = "die Haifische",
                        meaningsTr = "köpek balığı",
                        meaningsEn = "shark",
                        exampleSentence = "Wenn die Haifische Menschen wären, wären sie dann netter zu den kleinen Fischen?"
                    ),
                    CuratedCardData(
                        word = "Wirtin",
                        partOfSpeech = "NOUN",
                        article = "die",
                        plural = "die Wirtinnen",
                        meaningsTr = "ev sahibesi, kadın pansiyoncu",
                        meaningsEn = "landlady, hostess",
                        exampleSentence = "Herr K. wurde von der kleinen Tochter seiner Wirtin nach den Haifischen gefragt."
                    ),
                    CuratedCardData(
                        word = "gewaltig",
                        partOfSpeech = "ADJECTIVE",
                        meaningsTr = "muazzam, devasa, heybetli",
                        meaningsEn = "enormous, vast, tremendous",
                        exampleSentence = "Sie würden im Meer für die kleinen Fische gewaltige Kästen bauen lassen."
                    ),
                    CuratedCardData(
                        word = "allerhand",
                        partOfSpeech = "ADJECTIVE",
                        meaningsTr = "türlü türlü, her çeşit, pek çok",
                        meaningsEn = "all kinds of, various",
                        exampleSentence = "In den Kästen gäbe es allerhand Nahrung drin, sowohl Pflanzen als auch Tierzeug."
                    ),
                    CuratedCardData(
                        word = "wegsterben",
                        partOfSpeech = "VERB",
                        praesens = "stirbt weg",
                        praeteritum = "starb weg",
                        partizipZwei = "weggestorben",
                        hilfsverb = "ist",
                        isSeparable = true,
                        separablePrefix = "weg",
                        meaningsTr = "vaktinden önce ölüp gitmek",
                        meaningsEn = "to die off, to die prematurely",
                        exampleSentence = "Es würde ihnen ein Verband gemacht, damit sie nicht vor der Zeit wegstürben."
                    ),
                    CuratedCardData(
                        word = "Ausbildung",
                        partOfSpeech = "NOUN",
                        article = "die",
                        plural = "die Ausbildungen",
                        meaningsTr = "eğitim, yetişme, formasyon",
                        meaningsEn = "education, training",
                        exampleSentence = "Die Hauptsache wäre natürlich die moralische Ausbildung der Fischlein."
                    ),
                    CuratedCardData(
                        word = "gesichert",
                        partOfSpeech = "ADJECTIVE",
                        meaningsTr = "güvence altına alınmış, emin",
                        meaningsEn = "secured, guaranteed, safe",
                        exampleSentence = "Man würde ihnen beibringen, dass ihre Zukunft nur gesichert sei, wenn sie Gehorsam lernten."
                    ),
                    CuratedCardData(
                        word = "erobern",
                        partOfSpeech = "VERB",
                        praesens = "erobert",
                        praeteritum = "eroberte",
                        partizipZwei = "erobert",
                        hilfsverb = "hat",
                        meaningsTr = "fethetmek, zapt etmek, ele geçirmek",
                        meaningsEn = "to conquer, to seize",
                        exampleSentence = "Sie würden Kriege führen, um fremde Fischkästen und fremde Fischlein zu erobern."
                    ),
                    CuratedCardData(
                        word = "verkünden",
                        partOfSpeech = "VERB",
                        praesens = "verkündet",
                        praeteritum = "verkündete",
                        partizipZwei = "verkündet",
                        hilfsverb = "hat",
                        meaningsTr = "ilan etmek, duyurmak",
                        meaningsEn = "to proclaim, to announce",
                        exampleSentence = "Die Haifische würden verkünden, dass zwischen den Fischen ein riesiger Unterschied bestehe."
                    ),
                    CuratedCardData(
                        word = "bekanntlich",
                        partOfSpeech = "ADJECTIVE",
                        meaningsTr = "bilindiği üzere, malum olduğu gibi",
                        meaningsEn = "as is well known",
                        exampleSentence = "Die Fische sind bekanntlich stumm, aber sie schweigen in ganz verschiedenen Sprachen."
                    ),
                    CuratedCardData(
                        word = "stumm",
                        partOfSpeech = "ADJECTIVE",
                        meaningsTr = "dilsiz, sessiz, suskun",
                        meaningsEn = "mute, silent",
                        exampleSentence = "Sie schweigen in verschiedenen Sprachen und verstehen sich nicht."
                    ),
                    CuratedCardData(
                        word = "heldenmütig",
                        partOfSpeech = "ADJECTIVE",
                        meaningsTr = "kahramanca, yiğitçe",
                        meaningsEn = "heroic, valiant",
                        exampleSentence = "Die Theater zeigen, wie heldenmütige Fischlein begeistert in die Haifischrachen schwimmen."
                    ),
                    CuratedCardData(
                        word = "Lustgarten",
                        partOfSpeech = "NOUN",
                        article = "der",
                        plural = "die Lustgärten",
                        meaningsTr = "eğlence parkı, zevk ve sefa bahçesi",
                        meaningsEn = "pleasure garden",
                        exampleSentence = "Die Rachen der Haifische wurden als reine Lustgärten dargestellt."
                    ),
                    CuratedCardData(
                        word = "Kapelle",
                        partOfSpeech = "NOUN",
                        article = "die",
                        plural = "die Kapellen",
                        meaningsTr = "bando, müzik orkestrası",
                        meaningsEn = "brass band, orchestra",
                        exampleSentence = "Die Fischlein strömten mit der Kapelle voran träumerisch in den Schlund."
                    ),
                    CuratedCardData(
                        word = "innehaben",
                        partOfSpeech = "VERB",
                        praesens = "hat inne",
                        praeteritum = "hatte inne",
                        partizipZwei = "innegehabt",
                        hilfsverb = "hat",
                        isSeparable = true,
                        separablePrefix = "inne",
                        meaningsTr = "elinde bulundurmak, makam/mevki sahibi olmak",
                        meaningsEn = "to hold (an office/position)",
                        exampleSentence = "Die Posten innehabenden Fische sorgen für die Ordnung unter den Fischlein."
                    )
                )
            )
        )

        return list
    }

    fun getCuratedBundlesInfo(): List<CuratedBundleInfo> {
        return getAllBundles().map { bundle ->
            val desc = when {
                bundle.name.contains("A1:") -> "En temel fiil ve isimler, A1 başlangıç"
                bundle.name.contains("A2:") -> "Günlük konuşma, modal fiiller ve zamanlar"
                bundle.name.contains("B1:") -> "B1 kelime dağarcığı, düzensiz fiiller"
                bundle.name.contains("B2:") -> "B2 sınav hazırlığı, akademik kelimeler"
                bundle.name.contains("C1") && bundle.name.contains("Almanca") -> "C1 ileri seviye, profesyonel Almanca"
                bundle.name.contains("Redewendungen") -> "Almanca günlük deyimler ve pratik kalıplar"
                bundle.name.contains("Nomen-Verb") -> "B2-C1 sınavlarında zorunlu isim-fiil kalıpları"
                bundle.name.contains("Atasözleri") -> "Türkçe karşılıklarıyla atasözü ve deyimler"
                bundle.name.contains("Brecht") -> "Bertolt Brecht metin analizi & B2 kelimeleri"
                bundle.name.contains("Essential English") -> "Temel İngilizce kelime dağarcığı"
                bundle.name.contains("Intermediate") -> "Orta düzey & iş dünyası İngilizcesi"
                bundle.name.contains("Advanced") -> "İleri seviye & akademik İngilizce"
                bundle.name.contains("Idioms") -> "İngilizce deyimler ve phrasal verbs"
                else -> "${bundle.cards.size} seçkin kelime kartı"
            }
            CuratedBundleInfo(
                name = bundle.name,
                cardCount = bundle.cards.size,
                language = if (bundle.name.contains("İngilizce", ignoreCase = true)) "en" else "de",
                description = desc
            )
        }
    }

    suspend fun seedSelectedCuratedDecks(
        selectedDeckNames: Set<String>,
        deckRepository: DeckRepository
    ): Pair<Int, Int> = withContext(Dispatchers.IO) {
        val bundles = getAllBundles().filter { it.name in selectedDeckNames }
        val now = System.currentTimeMillis()
        var insertedCardsCount = 0
        var insertedDecksCount = 0

        for (bundle in bundles) {
            val deckId = deckRepository.createDeck(bundle.name)
            val deckLang = if (bundle.name.contains("İngilizce", ignoreCase = true)) "en" else "de"
            val existingCards = deckRepository.getCardsByDeck(deckId).first()

            for (c in bundle.cards) {
                if (existingCards.none { it.germanWord.equals(c.word, ignoreCase = true) }) {
                    val card = GermanCard(
                        deckId = deckId,
                        germanWord = c.word,
                        partOfSpeech = c.partOfSpeech,
                        article = c.article,
                        pluralForm = c.plural,
                        praesens = c.praesens,
                        praeteritum = c.praeteritum,
                        partizipZwei = c.partizipZwei,
                        hilfsverb = c.hilfsverb,
                        imperativ = c.imperativ,
                        isSeparable = c.isSeparable,
                        separablePrefix = c.separablePrefix,
                        meaningsTr = c.meaningsTr,
                        meaningsEn = c.meaningsEn,
                        exampleSentence = c.exampleSentence,
                        nextReviewDate = now,
                        intervalDays = 0,
                        isPriority = c.isPriority,
                        targetLanguage = deckLang
                    )
                    deckRepository.saveCard(card)
                    insertedCardsCount++
                }
            }
            insertedDecksCount++
        }
        Pair(insertedDecksCount, insertedCardsCount)
    }

    suspend fun seedAllCuratedDecks(
        deckRepository: DeckRepository
    ): Int = withContext(Dispatchers.IO) {
        val allNames = getAllBundles().map { it.name }.toSet()
        seedSelectedCuratedDecks(allNames, deckRepository).second
    }
}
