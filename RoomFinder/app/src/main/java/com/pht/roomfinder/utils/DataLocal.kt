package com.pht.roomfinder.utils;

import android.content.Context;
import android.content.SharedPreferences;

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

    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String) = sharedPreferences.getString(key, null)

    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String) = sharedPreferences.getBoolean(key, false)

}
