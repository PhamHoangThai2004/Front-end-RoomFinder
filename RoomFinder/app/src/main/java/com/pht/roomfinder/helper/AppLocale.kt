package com.pht.roomfinder.helper

import android.annotation.SuppressLint
import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

object AppLocale {
    const val APP_LANGUAGE = "appLanguages"

    private fun getLanguage(context: Context): String {
        val preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        return preferences.getString(APP_LANGUAGE, "vi") ?: "vi"
    }

    @Suppress("DEPRECATION")
    @SuppressLint("ObsoleteSdkInt")
    fun setAppLocale(context: Context): Context {
        val language = getLanguage(context)
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)

        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> { // API 33+
                context.getSystemService(LocaleManager::class.java)
                    .applicationLocales = LocaleList.forLanguageTags(language)
                context
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> { // API 24+
                context.createConfigurationContext(config)
            }

            else -> { // API 23 trở xuống
                context.resources.updateConfiguration(config, context.resources.displayMetrics)
                context
            }
        }
    }
}