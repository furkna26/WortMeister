package com.wortmeister.app.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wortmeister.app.data.local.preferences.GamificationStats
import com.wortmeister.app.data.local.preferences.StreakManager
import com.wortmeister.app.data.repository.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class StatsTimeRange(val label: String, val days: Int) {
    ONE_DAY("1 Gün", 1),
    THREE_DAYS("3 Gün", 3),
    ONE_WEEK("1 Hafta", 7),
    TWO_WEEKS("2 Hafta", 14),
    ONE_MONTH("1 Ay", 30)
}

enum class TrendDirection {
    INCREASE,  // Artış
    DECREASE,  // Düşüş
    NEUTRAL    // Değişim Yok
}

data class TrendMetric(
    val currentValue: Int,
    val previousValue: Int,
    val percentageChange: Int?, // Yüzdelik değişim, e.g. +25 veya -10
    val direction: TrendDirection
)

data class StatisticsUiState(
    val selectedRange: StatsTimeRange = StatsTimeRange.ONE_WEEK,
    val learnedTrend: TrendMetric = TrendMetric(0, 0, null, TrendDirection.NEUTRAL),
    val addedCardsTrend: TrendMetric = TrendMetric(0, 0, null, TrendDirection.NEUTRAL),
    val reviewsTrend: TrendMetric = TrendMetric(0, 0, null, TrendDirection.NEUTRAL),
    val accuracyPercentage: Int = 0,
    val previousAccuracyPercentage: Int = 0,
    val accuracyTrend: TrendMetric = TrendMetric(0, 0, null, TrendDirection.NEUTRAL),
    val dailyLearnedAverage: Double = 0.0,
    val streakStats: GamificationStats? = null,
    val totalDeckCount: Int = 0,
    val totalCardCount: Int = 0,
    val priorityCardCount: Int = 0,
    // Dil Dağılımı
    val germanCardsCount: Int = 0,
    val englishCardsCount: Int = 0,
    val germanLearnedCount: Int = 0,
    val englishLearnedCount: Int = 0,
    // Telaffuz
    val pronunciationCount: Int = 0,
    val averagePronunciationScore: Int = 0,
    val isLoading: Boolean = false
)

class StatisticsViewModel(
    private val deckRepository: DeckRepository,
    private val streakManager: StreakManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState(streakStats = streakManager.getStats()))
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            deckRepository.backfillLogsIfEmpty()
            loadStatistics(_uiState.value.selectedRange)
        }
    }

    fun selectTimeRange(range: StatsTimeRange) {
        _uiState.update { it.copy(selectedRange = range) }
        loadStatistics(range)
    }

    fun refresh() {
        loadStatistics(_uiState.value.selectedRange)
    }

    private fun loadStatistics(range: StatsTimeRange) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, streakStats = streakManager.getStats()) }

            val now = System.currentTimeMillis()
            val periodMillis = range.days * 24L * 60 * 60 * 1000

            val currentStart = now - periodMillis
            val currentEnd = now

            val previousStart = now - (2 * periodMillis)
            val previousEnd = currentStart

            // 1. Yeni Öğrenilen Kelimeler
            val currentLearned = deckRepository.getLearnedCountBetween(currentStart, currentEnd).first()
            val previousLearned = deckRepository.getLearnedCountBetween(previousStart, previousEnd).first()
            val learnedTrend = calculateTrend(currentLearned, previousLearned)

            // 2. Eklenen Yeni Kartlar
            val currentAdded = deckRepository.getCardsAddedBetween(currentStart, currentEnd).first()
            val previousAdded = deckRepository.getCardsAddedBetween(previousStart, previousEnd).first()
            val addedTrend = calculateTrend(currentAdded, previousAdded)

            // 3. Toplam Tekrar Sayısı
            val currentReviews = deckRepository.getTotalReviewsBetween(currentStart, currentEnd).first()
            val previousReviews = deckRepository.getTotalReviewsBetween(previousStart, previousEnd).first()
            val reviewsTrend = calculateTrend(currentReviews, previousReviews)

            // 4. Doğruluk / Başarı Oranı
            val currentAccuracy = if (currentReviews > 0) ((currentLearned.toDouble() / currentReviews) * 100).roundToInt() else 0
            val previousAccuracy = if (previousReviews > 0) ((previousLearned.toDouble() / previousReviews) * 100).roundToInt() else 0
            val accuracyTrend = calculateTrend(currentAccuracy, previousAccuracy)

            // 5. Günlük Öğrenme Hızı
            val dailyVelocity = currentLearned.toDouble() / range.days.coerceAtLeast(1)

            // 6. Dil Dağılımı
            val germanTotal = deckRepository.getCardCountForLanguage("de").first()
            val englishTotal = deckRepository.getCardCountForLanguage("en").first()
            val germanLearned = deckRepository.getLearnedForLanguageBetween(currentStart, currentEnd, "de").first()
            val englishLearned = deckRepository.getLearnedForLanguageBetween(currentStart, currentEnd, "en").first()

            // 7. Telaffuz İstatistikleri
            val pronunciationCount = deckRepository.getPronunciationCountBetween(currentStart, currentEnd).first()
            val avgPronScoreFloat = deckRepository.getAveragePronunciationScoreBetween(currentStart, currentEnd).first()
            val avgPronScore = avgPronScoreFloat?.roundToInt() ?: 0

            // 8. Global Kütüphane Bilgileri
            val totalDecks = deckRepository.allDecks.first().size
            val totalCards = deckRepository.totalCardCount.first()
            val priorityCards = deckRepository.priorityCards.first().size

            _uiState.update {
                it.copy(
                    learnedTrend = learnedTrend,
                    addedCardsTrend = addedTrend,
                    reviewsTrend = reviewsTrend,
                    accuracyPercentage = currentAccuracy,
                    previousAccuracyPercentage = previousAccuracy,
                    accuracyTrend = accuracyTrend,
                    dailyLearnedAverage = dailyVelocity,
                    germanCardsCount = germanTotal,
                    englishCardsCount = englishTotal,
                    germanLearnedCount = germanLearned,
                    englishLearnedCount = englishLearned,
                    pronunciationCount = pronunciationCount,
                    averagePronunciationScore = avgPronScore,
                    totalDeckCount = totalDecks,
                    totalCardCount = totalCards,
                    priorityCardCount = priorityCards,
                    isLoading = false
                )
            }
        }
    }

    private fun calculateTrend(current: Int, previous: Int): TrendMetric {
        val diff = current - previous
        val direction = when {
            diff > 0 -> TrendDirection.INCREASE
            diff < 0 -> TrendDirection.DECREASE
            else -> TrendDirection.NEUTRAL
        }

        val pct = when {
            previous > 0 -> ((diff.toDouble() / previous) * 100).roundToInt()
            current > 0 -> 100 // 0'dan pozitif bir sayıya çıktıysa +%100
            else -> 0
        }

        return TrendMetric(
            currentValue = current,
            previousValue = previous,
            percentageChange = pct,
            direction = direction
        )
    }
}
