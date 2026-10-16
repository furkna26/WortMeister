package com.wortmeister.app.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RecentSearchesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("wortmeister_recent_searches", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val key = "recent_search_queries"
    private val maxItems = 8

    fun getRecentSearches(): List<String> {
        val json = prefs.getString(key, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson<List<String>>(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.length < 2) return

        val current = getRecentSearches().toMutableList()
        current.removeAll { it.equals(trimmed, ignoreCase = true) }
        current.add(0, trimmed)

        val trimmedList = current.take(maxItems)
        prefs.edit().putString(key, gson.toJson(trimmedList)).apply()
    }

    fun removeSearch(query: String) {
        val current = getRecentSearches().toMutableList()
        current.removeAll { it.equals(query.trim(), ignoreCase = true) }
        prefs.edit().putString(key, gson.toJson(current)).apply()
    }

    fun clearAll() {
        prefs.edit().remove(key).apply()
    }
}
