package com.wortmeister.app.data.local.dictionary

import com.wortmeister.app.data.remote.model.ExamplePair
import com.wortmeister.app.data.remote.model.WordSearchResult

data class OfflineWordEntry(
    val lemma: String,
    val partOfSpeech: String, // NOUN, VERB, ADJECTIVE, PREPOSITION, PHRASE
    val article: String? = null,
    val plural: String? = null,
    val praesens: String? = null,
    val praeteritum: String? = null,
    val partizipZwei: String? = null,
    val hilfsverb: String? = null,
    val imperativ: String? = null,
    val isSeparable: Boolean = false,
    val separablePrefix: String? = null,
    val meaningsTr: List<String>,
    val meaningsEn: List<String>,
    val exampleSentences: List<ExamplePair>
)

object OfflineDictionaryEngine {

    private val ENTRIES: Map<String, OfflineWordEntry> by lazy {
        val list = mutableListOf<OfflineWordEntry>()

        // 1. TEMEL FİİLLER (Core German Verbs with 100% full tenses & Hilfsverb)
        list.add(
            OfflineWordEntry(
                lemma = "sein",
                partOfSpeech = "VERB",
                praesens = "ist",
                praeteritum = "war",
                partizipZwei = "gewesen",
                hilfsverb = "ist",
                imperativ = "Sei! / Seid! / Seien Sie!",
                meaningsTr = listOf("olmak", "bulunmak"),
                meaningsEn = listOf("to be"),
                exampleSentences = listOf(
                    ExamplePair("Ich bin Student in Berlin.", "Berlin'de öğrenciyim."),
                    ExamplePair("Das Wetter ist heute sehr schön.", "Bugün hava çok güzel.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "haben",
                partOfSpeech = "VERB",
                praesens = "hat",
                praeteritum = "hatte",
                partizipZwei = "gehabt",
                hilfsverb = "hat",
                imperativ = "Hab! / Habt! / Haben Sie!",
                meaningsTr = listOf("sahip olmak", "var olmak"),
                meaningsEn = listOf("to have"),
                exampleSentences = listOf(
                    ExamplePair("Ich habe eine Frage.", "Bir sorum var."),
                    ExamplePair("Wir haben viel Zeit.", "Çok zamanımız var.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "werden",
                partOfSpeech = "VERB",
                praesens = "wird",
                praeteritum = "wurde",
                partizipZwei = "geworden",
                hilfsverb = "ist",
                imperativ = "Werde! / Werdet! / Werden Sie!",
                meaningsTr = listOf("olmak", "haline gelmek"),
                meaningsEn = listOf("to become", "will"),
                exampleSentences = listOf(
                    ExamplePair("Er wird bald Arzt.", "O yakında doktor olacak."),
                    ExamplePair("Es wird langsam dunkel.", "Yavaş yavaş hava kararıyor.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "gehen",
                partOfSpeech = "VERB",
                praesens = "geht",
                praeteritum = "ging",
                partizipZwei = "gegangen",
                hilfsverb = "ist",
                imperativ = "Geh! / Geht! / Gehen Sie!",
                meaningsTr = listOf("gitmek", "yürümek", "işlemek"),
                meaningsEn = listOf("to go", "to walk"),
                exampleSentences = listOf(
                    ExamplePair("Ich gehe jeden Morgen zur Arbeit.", "Her sabah işe gidiyorum."),
                    ExamplePair("Wie geht es dir heute?", "Bugün nasılsın?")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "kommen",
                partOfSpeech = "VERB",
                praesens = "kommt",
                praeteritum = "kam",
                partizipZwei = "gekommen",
                hilfsverb = "ist",
                imperativ = "Komm! / Kommt! / Kommen Sie!",
                meaningsTr = listOf("gelmek"),
                meaningsEn = listOf("to come"),
                exampleSentences = listOf(
                    ExamplePair("Woher kommst du?", "Nerelisin?"),
                    ExamplePair("Der Zug kommt pünktlich an.", "Tren tam vaktinde geliyor.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "machen",
                partOfSpeech = "VERB",
                praesens = "macht",
                praeteritum = "machte",
                partizipZwei = "gemacht",
                hilfsverb = "hat",
                imperativ = "Mach! / Macht! / Machen Sie!",
                meaningsTr = listOf("yapmak", "etmek"),
                meaningsEn = listOf("to do", "to make"),
                exampleSentences = listOf(
                    ExamplePair("Was machst du heute Abend?", "Bu akşam ne yapıyorsun?"),
                    ExamplePair("Das macht keinen Sinn.", "Bunun hiçbir mantığı yok.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "arbeiten",
                partOfSpeech = "VERB",
                praesens = "arbeitet",
                praeteritum = "arbeitete",
                partizipZwei = "gearbeitet",
                hilfsverb = "hat",
                imperativ = "Arbeite! / Arbeitet! / Arbeiten Sie!",
                meaningsTr = listOf("çalışmak"),
                meaningsEn = listOf("to work"),
                exampleSentences = listOf(
                    ExamplePair("Ich arbeite als Ingenieur in Frankfurt.", "Frankfurt'ta mühendis olarak çalışıyorum."),
                    ExamplePair("Sie arbeitet fleißig.", "O çok çalışkan bir şekilde çalışıyor.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "lernen",
                partOfSpeech = "VERB",
                praesens = "lernt",
                praeteritum = "lernte",
                partizipZwei = "gelernt",
                hilfsverb = "hat",
                imperativ = "Lern! / Lernt! / Lernen Sie!",
                meaningsTr = listOf("öğrenmek", "ders çalışmak"),
                meaningsEn = listOf("to learn", "to study"),
                exampleSentences = listOf(
                    ExamplePair("Ich lerne jeden Tag Deutsch mit WortMeister.", "WortMeister ile her gün Almanca öğreniyorum."),
                    ExamplePair("Sie lernt fleißig für die Prüfung.", "Sınav için gayretle çalışıyor.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "sprechen",
                partOfSpeech = "VERB",
                praesens = "spricht",
                praeteritum = "sprach",
                partizipZwei = "gesprochen",
                hilfsverb = "hat",
                imperativ = "Sprich! / Sprecht! / Sprechen Sie!",
                meaningsTr = listOf("konuşmak"),
                meaningsEn = listOf("to speak"),
                exampleSentences = listOf(
                    ExamplePair("Sprechen Sie bitte etwas langsamer.", "Lütfen biraz daha yavaş konuşun."),
                    ExamplePair("Er spricht fließend Deutsch und Türkisch.", "O akıcı şekilde Almanca ve Türkçe konuşuyor.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "sehen",
                partOfSpeech = "VERB",
                praesens = "sieht",
                praeteritum = "sah",
                partizipZwei = "gesehen",
                hilfsverb = "hat",
                imperativ = "Sieh! / Seht! / Sehen Sie!",
                meaningsTr = listOf("görmek", "bakmak"),
                meaningsEn = listOf("to see"),
                exampleSentences = listOf(
                    ExamplePair("Ich sehe dich morgen in der Schule.", "Yarın seni okulda göreceğim."),
                    ExamplePair("Hast du diesen Film gesehen?", "Bu filmi gördün mü?")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "geben",
                partOfSpeech = "VERB",
                praesens = "gibt",
                praeteritum = "gab",
                partizipZwei = "gegeben",
                hilfsverb = "hat",
                imperativ = "Gib! / Gebt! / Geben Sie!",
                meaningsTr = listOf("vermek"),
                meaningsEn = listOf("to give"),
                exampleSentences = listOf(
                    ExamplePair("Es gibt viele Möglichkeiten.", "Pek çok imkan var."),
                    ExamplePair("Gib mir bitte das Buch.", "Lütfen kitabı bana ver.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "nehmen",
                partOfSpeech = "VERB",
                praesens = "nimmt",
                praeteritum = "nahm",
                partizipZwei = "genommen",
                hilfsverb = "hat",
                imperativ = "Nimm! / Nehmt! / Nehmen Sie!",
                meaningsTr = listOf("almak"),
                meaningsEn = listOf("to take"),
                exampleSentences = listOf(
                    ExamplePair("Ich nehme die U-Bahn zur Stadtmitte.", "Şehir merkezine metroyla gidiyorum."),
                    ExamplePair("Nimm bitte deinen Mantel mit.", "Lütfen paltonu yanına al.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "fahren",
                partOfSpeech = "VERB",
                praesens = "fährt",
                praeteritum = "fuhr",
                partizipZwei = "gefahren",
                hilfsverb = "ist",
                imperativ = "Fahr! / Fahrt! / Fahren Sie!",
                meaningsTr = listOf("araçla gitmek", "sürmek"),
                meaningsEn = listOf("to drive", "to ride"),
                exampleSentences = listOf(
                    ExamplePair("Wir fahren am Wochenende nach Köln.", "Hafta sonu Köln'e gidiyoruz."),
                    ExamplePair("Fährst du mit dem Auto oder dem Zug?", "Arabayla mı yoksa trenle mi gidiyorsun?")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "aufstehen",
                partOfSpeech = "VERB",
                praesens = "steht auf",
                praeteritum = "stand auf",
                partizipZwei = "aufgestanden",
                hilfsverb = "ist",
                imperativ = "Steh auf! / Steht auf! / Stehen Sie auf!",
                isSeparable = true,
                separablePrefix = "auf",
                meaningsTr = listOf("ayağa kalkmak", "yataktan kalkmak"),
                meaningsEn = listOf("to get up", "to stand up"),
                exampleSentences = listOf(
                    ExamplePair("Ich stehe jeden Morgen um sieben Uhr auf.", "Her sabah saat yedide kalkarım."),
                    ExamplePair("Wann bist du heute aufgestanden?", "Bugün saat kaçta kalktın?")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "einkaufen",
                partOfSpeech = "VERB",
                praesens = "kauft ein",
                praeteritum = "kaufte ein",
                partizipZwei = "eingekauft",
                hilfsverb = "hat",
                imperativ = "Kauf ein! / Kauft ein! / Kaufen Sie ein!",
                isSeparable = true,
                separablePrefix = "ein",
                meaningsTr = listOf("alışveriş yapmak"),
                meaningsEn = listOf("to shop"),
                exampleSentences = listOf(
                    ExamplePair("Wir kaufen am Samstag im Supermarkt ein.", "Cumartesi günü süpermarkette alışveriş yapıyoruz."),
                    ExamplePair("Ich habe frisches Gemüse eingekauft.", "Taze sebze satın aldım.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "einbrechen",
                partOfSpeech = "VERB",
                praesens = "bricht ein",
                praeteritum = "brach ein",
                partizipZwei = "eingebrochen",
                hilfsverb = "ist",
                imperativ = "Brich ein! / Brecht ein! / Brechen Sie ein!",
                isSeparable = true,
                separablePrefix = "ein",
                meaningsTr = listOf("hırsız girmek", "çökmek"),
                meaningsEn = listOf("to break in", "to collapse"),
                exampleSentences = listOf(
                    ExamplePair("Diebe sind in das Haus eingebrochen.", "Eve hırsızlar girdi."),
                    ExamplePair("Das Eis ist unter seinen Füßen eingebrochen.", "Buz ayaklarının altında kırıldı.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "anrufen",
                partOfSpeech = "VERB",
                praesens = "ruft an",
                praeteritum = "rief an",
                partizipZwei = "angerufen",
                hilfsverb = "hat",
                imperativ = "Ruf an! / Ruft an! / Rufen Sie an!",
                isSeparable = true,
                separablePrefix = "an",
                meaningsTr = listOf("telefon etmek", "aramak"),
                meaningsEn = listOf("to call on phone"),
                exampleSentences = listOf(
                    ExamplePair("Ich rufe dich heute Abend an.", "Seni bu akşam arayacağım."),
                    ExamplePair("Hast du den Arzt angerufen?", "Doktoru aradın mı?")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "verstehen",
                partOfSpeech = "VERB",
                praesens = "versteht",
                praeteritum = "verstand",
                partizipZwei = "verstanden",
                hilfsverb = "hat",
                imperativ = "Versteh! / Versteht! / Verstehen Sie!",
                meaningsTr = listOf("anlamak", "kavramak"),
                meaningsEn = listOf("to understand"),
                exampleSentences = listOf(
                    ExamplePair("Ich verstehe diese Grammatikregel jetzt gut.", "Bu gramer kuralını şimdi iyi anlıyorum."),
                    ExamplePair("Haben Sie alles verstanden?", "Her şeyi anladınız mı?")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "schreiben",
                partOfSpeech = "VERB",
                praesens = "schreibt",
                praeteritum = "schrieb",
                partizipZwei = "geschrieben",
                hilfsverb = "hat",
                imperativ = "Schreib! / Schreibt! / Schreiben Sie!",
                meaningsTr = listOf("yazmak"),
                meaningsEn = listOf("to write"),
                exampleSentences = listOf(
                    ExamplePair("Ich schreibe eine E-Mail an meinen Kollegen.", "İş arkadaşıma bir e-posta yazıyorum."),
                    ExamplePair("Er hat ein interessantes Buch geschrieben.", "O ilginç bir kitap yazdı.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "lesen",
                partOfSpeech = "VERB",
                praesens = "liest",
                praeteritum = "las",
                partizipZwei = "gelesen",
                hilfsverb = "hat",
                imperativ = "Lies! / Lest! / Lesen Sie!",
                meaningsTr = listOf("okumak"),
                meaningsEn = listOf("to read"),
                exampleSentences = listOf(
                    ExamplePair("Ich lese jeden Tag die Zeitung.", "Her gün gazete okurum."),
                    ExamplePair("Welches Buch liest du gerade?", "Şu an hangi kitabı okuyorsun?")
                )
            )
        )

        // 2. TEMEL İSİMLER (Core German Nouns with Artikel & Plural)
        list.add(
            OfflineWordEntry(
                lemma = "Buch",
                partOfSpeech = "NOUN",
                article = "das",
                plural = "die Bücher",
                meaningsTr = listOf("kitap"),
                meaningsEn = listOf("book"),
                exampleSentences = listOf(
                    ExamplePair("Das Buch liegt auf dem Tisch.", "Kitap masanın üzerinde duruyor."),
                    ExamplePair("Ich habe viele deutsche Bücher.", "Birçok Almanca kitabım var.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Haus",
                partOfSpeech = "NOUN",
                article = "das",
                plural = "die Häuser",
                meaningsTr = listOf("ev", "bina"),
                meaningsEn = listOf("house"),
                exampleSentences = listOf(
                    ExamplePair("Das Haus hat einen großen Garten.", "Evin büyük bir bahçesi var."),
                    ExamplePair("Wir bleiben heute zu Hause.", "Bugün evde kalıyoruz.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Auto",
                partOfSpeech = "NOUN",
                article = "das",
                plural = "die Autos",
                meaningsTr = listOf("araba", "otomobil"),
                meaningsEn = listOf("car"),
                exampleSentences = listOf(
                    ExamplePair("Das Auto steht vor der Garage.", "Araba garajın önünde duruyor."),
                    ExamplePair("Er kauft sich ein neues Auto.", "Kendisine yeni bir araba satın alıyor.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Hund",
                partOfSpeech = "NOUN",
                article = "der",
                plural = "die Hunde",
                meaningsTr = listOf("köpek"),
                meaningsEn = listOf("dog"),
                exampleSentences = listOf(
                    ExamplePair("Der Hund bellt im Garten.", "Köpek bahçede havlıyor."),
                    ExamplePair("Mein Hund ist sehr treu.", "Köpeğim çok sadıktır.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Katze",
                partOfSpeech = "NOUN",
                article = "die",
                plural = "die Katzen",
                meaningsTr = listOf("kedi"),
                meaningsEn = listOf("cat"),
                exampleSentences = listOf(
                    ExamplePair("Die Katze schläft auf dem Sofa.", "Kedi kanepede uyuyor."),
                    ExamplePair("Sie hat zwei weiße Katzen.", "Onun iki beyaz kedisi var.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Frau",
                partOfSpeech = "NOUN",
                article = "die",
                plural = "die Frauen",
                meaningsTr = listOf("kadın", "bayan", "eş"),
                meaningsEn = listOf("woman", "wife"),
                exampleSentences = listOf(
                    ExamplePair("Die Frau spricht freundlich mit den Kunden.", "Kadın müşterilerle nazikçe konuşuyor."),
                    ExamplePair("Guten Tag, Frau Müller!", "İyi günler Bayan Müller!")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Mann",
                partOfSpeech = "NOUN",
                article = "der",
                plural = "die Männer",
                meaningsTr = listOf("adam", "erkek", "koca"),
                meaningsEn = listOf("man", "husband"),
                exampleSentences = listOf(
                    ExamplePair("Der Mann arbeitet in der Bank.", "Adam bankada çalışıyor."),
                    ExamplePair("Mein Mann kocht sehr gerne.", "Eşim yemek yapmayı çok sever.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Kind",
                partOfSpeech = "NOUN",
                article = "das",
                plural = "die Kinder",
                meaningsTr = listOf("çocuk"),
                meaningsEn = listOf("child"),
                exampleSentences = listOf(
                    ExamplePair("Das Kind spielt im Park.", "Çocuk parkta oynuyor."),
                    ExamplePair("Die Kinder lernen fleißig in der Schule.", "Çocuklar okulda gayretle öğreniyor.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Stadt",
                partOfSpeech = "NOUN",
                article = "die",
                plural = "die Städte",
                meaningsTr = listOf("şehir", "kent"),
                meaningsEn = listOf("city"),
                exampleSentences = listOf(
                    ExamplePair("Berlin ist die Hauptstadt von Deutschland.", "Berlin, Almanya'nın başkentidir."),
                    ExamplePair("In dieser Stadt gibt es viele Museen.", "Bu şehirde birçok müze var.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Tisch",
                partOfSpeech = "NOUN",
                article = "der",
                plural = "die Tische",
                meaningsTr = listOf("masa"),
                meaningsEn = listOf("table"),
                exampleSentences = listOf(
                    ExamplePair("Der Tisch ist aus Holz gemacht.", "Masa ahşaptan yapılmıştır."),
                    ExamplePair("Bitte setzen Sie sich an den Tisch.", "Lütfen masaya oturun.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Stuhl",
                partOfSpeech = "NOUN",
                article = "der",
                plural = "die Stühle",
                meaningsTr = listOf("sandalye"),
                meaningsEn = listOf("chair"),
                exampleSentences = listOf(
                    ExamplePair("Auf dem Stuhl liegt eine Tasche.", "Sandalyenin üstünde bir çanta var."),
                    ExamplePair("Dieser Stuhl ist sehr bequem.", "Bu sandalye çok rahat.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Tür",
                partOfSpeech = "NOUN",
                article = "die",
                plural = "die Türen",
                meaningsTr = listOf("kapı"),
                meaningsEn = listOf("door"),
                exampleSentences = listOf(
                    ExamplePair("Bitte schließen Sie die Tür.", "Lütfen kapıyı kapatın."),
                    ExamplePair("Die Tür steht offen.", "Kapı açık duruyor.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Fenster",
                partOfSpeech = "NOUN",
                article = "das",
                plural = "die Fenster",
                meaningsTr = listOf("pencere"),
                meaningsEn = listOf("window"),
                exampleSentences = listOf(
                    ExamplePair("Das Fenster öffnet sich leicht.", "Pencere kolayca açılıyor."),
                    ExamplePair("Ich schaue aus dem Fenster.", "Pencereden dışarı bakıyorum.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Wasser",
                partOfSpeech = "NOUN",
                article = "das",
                plural = "die Wasser",
                meaningsTr = listOf("su"),
                meaningsEn = listOf("water"),
                exampleSentences = listOf(
                    ExamplePair("Ein Glas Wasser, bitte.", "Bir bardak su, lütfen."),
                    ExamplePair("Das Wasser ist kalt und erfrischend.", "Su soğuk ve ferahlatıcı.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Zeit",
                partOfSpeech = "NOUN",
                article = "die",
                plural = "die Zeiten",
                meaningsTr = listOf("zaman", "vakit"),
                meaningsEn = listOf("time"),
                exampleSentences = listOf(
                    ExamplePair("Ich habe im Moment keine Zeit.", "Şu anda hiç vaktim yok."),
                    ExamplePair("Die Zeit vergeht wie im Flug.", "Zaman uçup gidiyor.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Arbeit",
                partOfSpeech = "NOUN",
                article = "die",
                plural = "die Arbeiten",
                meaningsTr = listOf("iş", "çalışma"),
                meaningsEn = listOf("work", "job"),
                exampleSentences = listOf(
                    ExamplePair("Ich gehe um acht Uhr zur Arbeit.", "Saat sekizde işe gidiyorum."),
                    ExamplePair("Die Arbeit macht mir viel Spaß.", "İşimi çok severek yapıyorum.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Schule",
                partOfSpeech = "NOUN",
                article = "die",
                plural = "die Schulen",
                meaningsTr = listOf("okul"),
                meaningsEn = listOf("school"),
                exampleSentences = listOf(
                    ExamplePair("Die Kinder sind jetzt in der Schule.", "Çocuklar şu an okulda."),
                    ExamplePair("Unsere Schule ist modern ausgestattet.", "Okulumuz modern donatılmıştır.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Weg",
                partOfSpeech = "NOUN",
                article = "der",
                plural = "die Wege",
                meaningsTr = listOf("yol", "yöntem"),
                meaningsEn = listOf("way", "path"),
                exampleSentences = listOf(
                    ExamplePair("Können Sie mir den Weg zum Bahnhof zeigen?", "Bana istasyona giden yolu gösterebilir misiniz?"),
                    ExamplePair("Das ist der beste Weg zum Erfolg.", "Başarıya giden en iyi yol budur.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Freund",
                partOfSpeech = "NOUN",
                article = "der",
                plural = "die Freunde",
                meaningsTr = listOf("arkadaş", "dost", "erkek arkadaş"),
                meaningsEn = listOf("friend"),
                exampleSentences = listOf(
                    ExamplePair("Mein bester Freund wohnt in München.", "En iyi arkadaşım Münih'te yaşıyor."),
                    ExamplePair("Wir sind seit der Kindheit Freunde.", "Çocukluktan beri arkadaşız.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Tag",
                partOfSpeech = "NOUN",
                article = "der",
                plural = "die Tage",
                meaningsTr = listOf("gün"),
                meaningsEn = listOf("day"),
                exampleSentences = listOf(
                    ExamplePair("Ich wünsche dir einen schönen Tag!", "Sana güzel bir gün dilerim!"),
                    ExamplePair("Jeder Tag bringt neue Chancen.", "Her gün yeni fırsatlar getirir.")
                )
            )
        )

        // 3. TEMEL SIFATLAR & DİĞERLERİ
        list.add(
            OfflineWordEntry(
                lemma = "gut",
                partOfSpeech = "ADJECTIVE",
                meaningsTr = listOf("iyi", "güzel"),
                meaningsEn = listOf("good"),
                exampleSentences = listOf(
                    ExamplePair("Das Essen schmeckt sehr gut.", "Yemek çok lezzetli."),
                    ExamplePair("Guten Morgen!", "Günaydın!")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "groß",
                partOfSpeech = "ADJECTIVE",
                meaningsTr = listOf("büyük", "uzun boylu"),
                meaningsEn = listOf("big", "large", "tall"),
                exampleSentences = listOf(
                    ExamplePair("Deutschland ist ein großes Land in Europa.", "Almanya Avrupa'da büyük bir ülkedir."),
                    ExamplePair("Er ist zwei Meter groß.", "Onun boyu iki metre.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "klein",
                partOfSpeech = "ADJECTIVE",
                meaningsTr = listOf("küçük", "kısa boylu"),
                meaningsEn = listOf("small", "short"),
                exampleSentences = listOf(
                    ExamplePair("Das Zimmer ist klein aber gemütlich.", "Oda küçük ama çok samimi."),
                    ExamplePair("Ein kleines Geschenk für dich.", "Senin için küçük bir hediye.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "schön",
                partOfSpeech = "ADJECTIVE",
                meaningsTr = listOf("güzel"),
                meaningsEn = listOf("beautiful", "nice"),
                exampleSentences = listOf(
                    ExamplePair("Das Wetter ist heute wunderschön.", "Bugün hava harikulade güzel."),
                    ExamplePair("Eine schöne Reise wünsche ich dir!", "Sana iyi yolculuklar dilerim!")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "neu",
                partOfSpeech = "ADJECTIVE",
                meaningsTr = listOf("yeni"),
                meaningsEn = listOf("new"),
                exampleSentences = listOf(
                    ExamplePair("Das ist mein neues Handy.", "Bu benim yeni cep telefonum."),
                    ExamplePair("Frohes neues Jahr!", "Mutlu yıllar!")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "alt",
                partOfSpeech = "ADJECTIVE",
                meaningsTr = listOf("eski", "yaşlı"),
                meaningsEn = listOf("old"),
                exampleSentences = listOf(
                    ExamplePair("Wie alt bist du?", "Kaç yaşındasın?"),
                    ExamplePair("Das alte Schloss ist beeindruckend.", "Eski kale çok etkileyici.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Einzel",
                partOfSpeech = "NOUN",
                article = "das",
                plural = "die Einzel",
                meaningsTr = listOf("tekler maçı (tenis/spor)", "bireysel oyun", "tek", "münferit", "ayrı", "birim"),
                meaningsEn = listOf("singles match (tennis/sports)", "individual match", "single", "separate", "solo", "unit"),
                exampleSentences = listOf(
                    ExamplePair("Boris Becker gewann das Einzel.", "Boris Becker tekler maçını kazandı."),
                    ExamplePair("Damit sind im Einzel alle deutschen Spieler ausgeschieden.", "Böylece tekler kategorisinde tüm Alman oyuncular elendi.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "einzeln",
                partOfSpeech = "ADJECTIVE",
                meaningsTr = listOf("tek", "bireysel", "tek başına", "münferit", "ayrı", "tane tane"),
                meaningsEn = listOf("single", "individual", "separate", "isolated", "one by one"),
                exampleSentences = listOf(
                    ExamplePair("Jedes einzelne Wort zählt.", "Her bir tek kelime önemlidir."),
                    ExamplePair("Die Schüler betraten den Raum einzeln.", "Öğrenciler odaya teker teker girdi.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Gericht",
                partOfSpeech = "NOUN",
                article = "das",
                plural = "die Gerichte",
                meaningsTr = listOf("yemek", "mahkeme", "yargı"),
                meaningsEn = listOf("dish / meal", "court of law", "tribunal"),
                exampleSentences = listOf(
                    ExamplePair("Dieses Gericht schmeckt hervorragend.", "Bu yemek mükemmel lezzette."),
                    ExamplePair("Der Fall wird vor Gericht verhandelt.", "Dava mahkemede görülüyor.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Bank",
                partOfSpeech = "NOUN",
                article = "die",
                plural = "die Banken / die Bänke",
                meaningsTr = listOf("banka (finans)", "oturma sırası", "bank (park)"),
                meaningsEn = listOf("bank (finance)", "bench (seat)"),
                exampleSentences = listOf(
                    ExamplePair("Er bringt sein Geld zur Bank.", "Parasini bankaya yatırıyor."),
                    ExamplePair("Wir sitzen auf einer Bank im Park.", "Parkta bir bankta oturuyoruz.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Schloss",
                partOfSpeech = "NOUN",
                article = "das",
                plural = "die Schlösser",
                meaningsTr = listOf("kilit", "şato, saray"),
                meaningsEn = listOf("lock", "castle, palace"),
                exampleSentences = listOf(
                    ExamplePair("Der Schlüssel passt nicht ins Schloss.", "Anahtar kilide uymuyor."),
                    ExamplePair("Wir besuchen das alte Schloss.", "Eski şatoyu ziyaret ediyoruz.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Leiter",
                partOfSpeech = "NOUN",
                article = "der",
                plural = "die Leiter / die Leitern",
                meaningsTr = listOf("yönetici, müdür, şef (der Leiter)", "merdiven (die Leiter)"),
                meaningsEn = listOf("director, leader, manager", "ladder"),
                exampleSentences = listOf(
                    ExamplePair("Er ist der Leiter des Instituts.", "O, enstitünün müdürüdür."),
                    ExamplePair("Er klettert auf die Leiter.", "Merdivene tırmanıyor.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Zigarette",
                partOfSpeech = "NOUN",
                article = "die",
                plural = "die Zigaretten",
                meaningsTr = listOf("sigara"),
                meaningsEn = listOf("cigarette"),
                exampleSentences = listOf(
                    ExamplePair("Er raucht eine Zigarette auf dem Balkon.", "Balkonda bir sigara içiyor."),
                    ExamplePair("Hier ist das Rauchen von Zigaretten verboten.", "Burada sigara içmek yasaktır.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Kaffee",
                partOfSpeech = "NOUN",
                article = "der",
                plural = "die Kaffees",
                meaningsTr = listOf("kahve"),
                meaningsEn = listOf("coffee"),
                exampleSentences = listOf(
                    ExamplePair("Ich trinke morgens gerne einen Kaffee.", "Sabahları severek bir kahve içerim."),
                    ExamplePair("Möchtest du Kaffee mit Milch?", "Sütlü kahve ister misin?")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Tee",
                partOfSpeech = "NOUN",
                article = "der",
                plural = "die Tees",
                meaningsTr = listOf("çay"),
                meaningsEn = listOf("tea"),
                exampleSentences = listOf(
                    ExamplePair("Eine Tasse schwarzer Tee bitte.", "Bir fincan siyah çay lütfen."),
                    ExamplePair("Im Winter trinke ich oft heißen Tee.", "Kışın sık sık sıcak çay içerim.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Wasser",
                partOfSpeech = "NOUN",
                article = "das",
                plural = "die Wässer",
                meaningsTr = listOf("su"),
                meaningsEn = listOf("water"),
                exampleSentences = listOf(
                    ExamplePair("Bitte ein Glas kaltes Wasser.", "Lütfen bir bardak soğuk su."),
                    ExamplePair("Wasser ist lebensnotwendig.", "Su yaşam için vazgeçilmezdir.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Brot",
                partOfSpeech = "NOUN",
                article = "das",
                plural = "die Brote",
                meaningsTr = listOf("ekmek"),
                meaningsEn = listOf("bread"),
                exampleSentences = listOf(
                    ExamplePair("Frisches Brot schmeckt am besten.", "Taze ekmek en lezzetlisidir."),
                    ExamplePair("Ich kaufe Brot beim Bäcker.", "Fırından ekmek alıyorum.")
                )
            )
        )

        list.add(
            OfflineWordEntry(
                lemma = "Apfel",
                partOfSpeech = "NOUN",
                article = "der",
                plural = "die Äpfel",
                meaningsTr = listOf("elma"),
                meaningsEn = listOf("apple"),
                exampleSentences = listOf(
                    ExamplePair("Ein Apfel am Tag hält den Arzt fern.", "Günde bir elma doktoru uzak tutar."),
                    ExamplePair("Der rote Apfel ist süß und saftig.", "Kırmızı elma tatlı ve sulu.")
                )
            )
        )

        val map = mutableMapOf<String, OfflineWordEntry>()
        for (entry in list) {
            map[entry.lemma.lowercase()] = entry
            if (entry.plural != null) {
                val cleanPlural = entry.plural.lowercase().replace(Regex("^(die)\\s+"), "").trim()
                map[cleanPlural] = entry
            }
            // Also map by Turkish meanings for reverse search!
            for (tr in entry.meaningsTr) {
                map[tr.lowercase()] = entry
            }
            // Also map by English meanings for reverse search!
            for (en in entry.meaningsEn) {
                val cleanEn = en.lowercase().replace(Regex("^to\\s+"), "").trim()
                map[cleanEn] = entry
                map[en.lowercase().trim()] = entry
            }
        }
        map
    }

    fun find(query: String): WordSearchResult? {
        val clean = query.trim().lowercase().replace(Regex("^(der|die|das)\\s+"), "")
        val entry = ENTRIES[clean] ?: return null

        val displayWord = if (entry.partOfSpeech == "NOUN") {
            entry.lemma.replaceFirstChar { it.uppercase() }
        } else {
            entry.lemma
        }

        val sourceLang = when {
            entry.meaningsTr.any { it.equals(clean, ignoreCase = true) } -> "tr"
            entry.meaningsEn.any { it.equals(clean, ignoreCase = true) || it.removePrefix("to ").trim().equals(clean, ignoreCase = true) } -> "en"
            else -> "de"
        }

        val isReflexive = entry.partOfSpeech == "VERB" && (entry.lemma.startsWith("sich ") || com.wortmeister.app.data.remote.parser.GermanVerbHelper.isKnownReflexive(entry.lemma))
        val reflexiveType = if (isReflexive) "sich" else null

        return WordSearchResult(
            query = query,
            germanWord = displayWord,
            sourceLanguage = sourceLang,
            searchedTerm = query,
            partOfSpeech = entry.partOfSpeech,
            article = entry.article,
            pluralForm = entry.plural,
            praesens = entry.praesens,
            praeteritum = entry.praeteritum,
            partizipZwei = entry.partizipZwei,
            imperativ = entry.imperativ,
            hilfsverb = entry.hilfsverb,
            isSeparable = entry.isSeparable,
            separablePrefix = entry.separablePrefix,
            isReflexive = isReflexive,
            reflexiveType = reflexiveType,
            regularity = if (entry.praeteritum != null && entry.partOfSpeech == "VERB") "Doğrulanmış Formlar" else null,
            meaningsTr = entry.meaningsTr,
            meaningsEn = entry.meaningsEn,
            exampleSentences = entry.exampleSentences,
            isSuccess = true
        )
    }

    fun getSuggestions(query: String): List<String> {
        val clean = query.trim().lowercase()
        if (clean.length < 2) return emptyList()
        return ENTRIES.keys
            .filter { it.startsWith(clean) }
            .take(6)
            .toList()
    }
}
