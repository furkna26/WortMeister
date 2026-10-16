package com.wortmeister.app.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import java.util.Calendar

data class GamificationStats(
    val streakDays: Int,
    val cardsToday: Int,
    val totalLearned: Int,
    val levelTitle: String,
    val motivationalMessage: String
)

class StreakManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("wortmeister_streak_prefs", Context.MODE_PRIVATE)

    fun getStats(): GamificationStats {
        val calendar = Calendar.getInstance()
        val todayDay = calendar.get(Calendar.DAY_OF_YEAR)
        val todayYear = calendar.get(Calendar.YEAR)

        val lastDay = prefs.getInt("last_day_practiced", -1)
        val lastYear = prefs.getInt("last_year_practiced", -1)
        var streak = prefs.getInt("streak_days", 0)
        var cardsToday = prefs.getInt("cards_today", 0)
        val totalLearned = prefs.getInt("total_learned", 0)

        // Check if streak is broken
        if (lastDay != -1 && lastYear != -1) {
            val isToday = (lastDay == todayDay && lastYear == todayYear)
            val isYesterday = if (lastYear == todayYear) {
                lastDay == todayDay - 1
            } else if (todayYear == lastYear + 1) {
                // Year boundary (e.g., Dec 31 to Jan 1)
                todayDay == 1 && (lastDay >= 365)
            } else {
                false
            }

            if (!isToday && !isYesterday) {
                // Streak broken
                streak = 0
                cardsToday = 0
            } else if (!isToday) {
                // New day, reset today's count
                cardsToday = 0
            }
        }

        val level = when {
            totalLearned >= 150 -> "WortMeister"
            totalLearned >= 50 -> "Usta (Meister)"
            totalLearned >= 20 -> "Öğrenci (Lernender)"
            else -> "Çırak (Anfänger)"
        }

        val message = when {
            streak >= 7 -> "İnanılmaz! 1 haftayı devirdin!"
            streak >= 3 -> "Harika bir tempo yakaladın, devam et!"
            cardsToday >= 20 -> "Bugün harikalar yarattın!"
            cardsToday > 0 -> "Her gün 1 adım daha ileri!"
            else -> "Günün tekrarını yapmaya hazır mısın?"
        }

        return GamificationStats(
            streakDays = streak,
            cardsToday = cardsToday,
            totalLearned = totalLearned,
            levelTitle = level,
            motivationalMessage = message
        )
    }

    fun recordReview(isLearned: Boolean) {
        val calendar = Calendar.getInstance()
        val todayDay = calendar.get(Calendar.DAY_OF_YEAR)
        val todayYear = calendar.get(Calendar.YEAR)

        val lastDay = prefs.getInt("last_day_practiced", -1)
        val lastYear = prefs.getInt("last_year_practiced", -1)
        var streak = prefs.getInt("streak_days", 0)
        var cardsToday = prefs.getInt("cards_today", 0)
        var totalLearned = prefs.getInt("total_learned", 0)

        if (lastDay == todayDay && lastYear == todayYear) {
            cardsToday++
        } else {
            val isYesterday = if (lastYear == todayYear) {
                lastDay == todayDay - 1
            } else if (todayYear == lastYear + 1) {
                todayDay == 1 && (lastDay >= 365)
            } else {
                false
            }

            streak = if (isYesterday) streak + 1 else 1
            cardsToday = 1
        }

        if (isLearned) {
            totalLearned++
        }

        prefs.edit()
            .putInt("last_day_practiced", todayDay)
            .putInt("last_year_practiced", todayYear)
            .putInt("streak_days", streak)
            .putInt("cards_today", cardsToday)
            .putInt("total_learned", totalLearned)
            .apply()
    }
}
