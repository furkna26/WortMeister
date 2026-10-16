package com.wortmeister.app

import com.wortmeister.app.data.local.entity.GermanCard
import com.wortmeister.app.ui.screens.game.GameQuestionGenerator
import com.wortmeister.app.ui.screens.game.GameType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameQuestionGeneratorTest {

    private val sampleCards = listOf(
        GermanCard(
            id = 1,
            deckId = 1,
            germanWord = "Haus",
            partOfSpeech = "NOUN",
            article = "das",
            pluralForm = "die Häuser",
            meaningsTr = "ev, konut"
        ),
        GermanCard(
            id = 2,
            deckId = 1,
            germanWord = "Buch",
            partOfSpeech = "NOUN",
            article = "das",
            pluralForm = "die Bücher",
            meaningsTr = "kitap"
        ),
        GermanCard(
            id = 3,
            deckId = 1,
            germanWord = "Katze",
            partOfSpeech = "NOUN",
            article = "die",
            pluralForm = "die Katzen",
            meaningsTr = "kedi"
        ),
        GermanCard(
            id = 4,
            deckId = 1,
            germanWord = "Hund",
            partOfSpeech = "NOUN",
            article = "der",
            pluralForm = "die Hunde",
            meaningsTr = "köpek"
        ),
        GermanCard(
            id = 5,
            deckId = 1,
            germanWord = "trinken",
            partOfSpeech = "VERB",
            praeteritum = "trank",
            partizipZwei = "getrunken",
            hilfsverb = "hat",
            meaningsTr = "içmek"
        ),
        GermanCard(
            id = 6,
            deckId = 1,
            germanWord = "gehen",
            partOfSpeech = "VERB",
            praeteritum = "ging",
            partizipZwei = "gegangen",
            hilfsverb = "ist",
            meaningsTr = "gitmek, yürümek"
        ),
        GermanCard(
            id = 7,
            deckId = 1,
            germanWord = "sehen",
            partOfSpeech = "VERB",
            praeteritum = "sah",
            partizipZwei = "gesehen",
            hilfsverb = "hat",
            meaningsTr = "görmek"
        ),
        GermanCard(
            id = 8,
            deckId = 1,
            germanWord = "schreiben",
            partOfSpeech = "VERB",
            praeteritum = "schrieb",
            partizipZwei = "geschrieben",
            hilfsverb = "hat",
            meaningsTr = "yazmak"
        ),
        GermanCard(
            id = 9,
            deckId = 1,
            germanWord = "schnell",
            partOfSpeech = "ADJECTIVE",
            meaningsTr = "hızlı, çabuk"
        ),
        GermanCard(
            id = 10,
            deckId = 1,
            germanWord = "gut",
            partOfSpeech = "ADJECTIVE",
            meaningsTr = "iyi, güzel"
        ),
        GermanCard(
            id = 11,
            deckId = 1,
            germanWord = "groß",
            partOfSpeech = "ADJECTIVE",
            meaningsTr = "büyük"
        ),
        GermanCard(
            id = 12,
            deckId = 1,
            germanWord = "alt",
            partOfSpeech = "ADJECTIVE",
            meaningsTr = "yaşlı, eski"
        ),
        GermanCard(
            id = 13,
            deckId = 1,
            germanWord = "ab und zu",
            partOfSpeech = "PHRASE",
            meaningsTr = "ara sıra, bazen"
        ),
        GermanCard(
            id = 14,
            deckId = 1,
            germanWord = "vor allem",
            partOfSpeech = "PHRASE",
            meaningsTr = "özellikle, her şeyden önce"
        ),
        GermanCard(
            id = 15,
            deckId = 1,
            germanWord = "in Frage kommen",
            partOfSpeech = "PHRASE",
            meaningsTr = "söz konusu olmak"
        ),
        GermanCard(
            id = 16,
            deckId = 1,
            germanWord = "Abschied nehmen",
            partOfSpeech = "PHRASE",
            meaningsTr = "vedalaşmak"
        )
    )

    @Test
    fun testFilterEligibleCards() {
        val articleCards = GameQuestionGenerator.filterEligibleCards(sampleCards, GameType.ARTICLE)
        assertEquals(4, articleCards.size)

        val verbCards = GameQuestionGenerator.filterEligibleCards(sampleCards, GameType.VERB)
        assertEquals(4, verbCards.size)

        val nounCards = GameQuestionGenerator.filterEligibleCards(sampleCards, GameType.NOUN)
        assertEquals(4, nounCards.size)

        val adjCards = GameQuestionGenerator.filterEligibleCards(sampleCards, GameType.ADJECTIVE)
        assertEquals(4, adjCards.size)

        val phraseCards = GameQuestionGenerator.filterEligibleCards(sampleCards, GameType.PHRASE)
        assertEquals(4, phraseCards.size)

        val mixedCards = GameQuestionGenerator.filterEligibleCards(sampleCards, GameType.MIXED)
        assertEquals(16, mixedCards.size)
    }

    @Test
    fun testGenerateArticleQuestions() {
        val eligible = GameQuestionGenerator.filterEligibleCards(sampleCards, GameType.ARTICLE)
        val questions = GameQuestionGenerator.generateQuestions(eligible, sampleCards, GameType.ARTICLE)

        assertTrue(questions.isNotEmpty())
        for (q in questions) {
            assertEquals(3, q.options.size)
            assertTrue(q.options.contains("der"))
            assertTrue(q.options.contains("die"))
            assertTrue(q.options.contains("das"))
            assertTrue(q.correctIndex in 0..2)
            assertNotNull(q.questionSubject)
        }
    }

    @Test
    fun testGenerateVerbQuestions() {
        val eligible = GameQuestionGenerator.filterEligibleCards(sampleCards, GameType.VERB)
        val questions = GameQuestionGenerator.generateQuestions(eligible, sampleCards, GameType.VERB)

        assertTrue(questions.isNotEmpty())
        for (q in questions) {
            assertTrue(q.options.size in 2..4)
            assertTrue(q.correctIndex in 0 until q.options.size)
        }
    }

    @Test
    fun testGenerateAdjectiveQuestions() {
        val eligible = GameQuestionGenerator.filterEligibleCards(sampleCards, GameType.ADJECTIVE)
        val questions = GameQuestionGenerator.generateQuestions(eligible, sampleCards, GameType.ADJECTIVE)

        assertTrue(questions.isNotEmpty())
        for (q in questions) {
            assertTrue(q.options.size in 2..4)
            assertTrue(q.correctIndex in 0 until q.options.size)
        }
    }

    @Test
    fun testGenerateMixedQuestions() {
        val eligible = GameQuestionGenerator.filterEligibleCards(sampleCards, GameType.MIXED)
        val questions = GameQuestionGenerator.generateQuestions(eligible, sampleCards, GameType.MIXED)

        assertTrue(questions.isNotEmpty())
        for (q in questions) {
            assertTrue(q.options.isNotEmpty())
            assertTrue(q.correctIndex in 0 until q.options.size)
        }
    }
}
