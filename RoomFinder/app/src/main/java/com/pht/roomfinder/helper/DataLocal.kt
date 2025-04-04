package com.pht.roomfinder.helper

import android.content.Context
import android.content.SharedPreferences

class DataLocal private constructor() {
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val STORAGE_LOCATION = "data"
        private var instance: DataLocal? = null

        fun init(context: Context) {
            if (instance == null) instance = DataLocal()
            instance?.sharedPreferences =
                context.getSharedPreferences(STORAGE_LOCATION, Context.MODE_PRIVATE)
        }

        fun getInstance(): DataLocal {
            if (instance == null) {
                instance = DataLocal()
            }
            return instance!!
        }
    }


    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String) = sharedPreferences.getString(key, null)

    fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun getInt(key: String) = sharedPreferences.getInt(key, 0)

    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String) = sharedPreferences.getBoolean(key, false)

}

class SearchHistoryManager(context: Context) {
    private val prefs = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)

    fun saveSearchHistory(keySearch: String) {
        val history = loadSearchHistory().toMutableList()
        history.remove(keySearch)
        history.add(0, keySearch)
        if (history.size > 8) history.removeLast()

        prefs.edit().putStringSet("keySearch", history.toSet()).apply()
    }

    fun loadSearchHistory(): List<String> {
        return prefs.getStringSet("keySearch", emptySet())?.toList() ?: emptyList()
    }

    fun clearKeySearch() {
        prefs.edit().clear().apply()
    }
}
