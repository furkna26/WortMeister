package com.wortmeister.app.data.remote.parser

import kotlin.math.max
import kotlin.math.min

data class FuzzyMatchResult(
    val matchedWord: String,
    val distance: Int,
    val similarity: Double
)

object FuzzyMatcher {

    /**
     * Damerau-Levenshtein distance calculation (handles insertions, deletions, substitutions, and transpositions)
     */
    fun distance(s1: String, s2: String): Int {
        val a = s1.lowercase()
        val b = s2.lowercase()

        if (a == b) return 0
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length

        val dp = Array(a.length + 1) { IntArray(b.length + 1) }

        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j

        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1

                dp[i][j] = min(
                    min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                )

                // Transposition check (e.g. "ei" vs "ie")
                if (i > 1 && j > 1 && a[i - 1] == b[j - 2] && a[i - 2] == b[j - 1]) {
                    dp[i][j] = min(dp[i][j], dp[i - 2][j - 2] + 1)
                }
            }
        }

        return dp[a.length][b.length]
    }

    fun similarity(s1: String, s2: String): Double {
        val maxLen = max(s1.length, s2.length)
        if (maxLen == 0) return 1.0
        val dist = distance(s1, s2)
        return (maxLen - dist).toDouble() / maxLen.toDouble()
    }

    /**
     * Finds the closest match from a collection of candidates.
     * Returns null if no candidate is within acceptable tolerance.
     */
    fun findBestMatch(
        query: String,
        candidates: List<String>,
        maxAllowedDistance: Int = 2
    ): FuzzyMatchResult? {
        val trimmed = query.trim()
        if (trimmed.length < 3 || candidates.isEmpty()) return null

        var bestMatch: FuzzyMatchResult? = null

        for (cand in candidates) {
            val dist = distance(trimmed, cand)
            val sim = similarity(trimmed, cand)

            // Dynamic tolerance: for short words, distance must be 1; for longer words, distance <= 2
            val maxDist = if (trimmed.length <= 4) 1 else maxAllowedDistance

            if (dist <= maxDist && sim >= 0.65) {
                if (bestMatch == null || dist < bestMatch.distance || (dist == bestMatch.distance && sim > bestMatch.similarity)) {
                    bestMatch = FuzzyMatchResult(
                        matchedWord = cand,
                        distance = dist,
                        similarity = sim
                    )
                }
            }
        }

        return bestMatch
    }
}
