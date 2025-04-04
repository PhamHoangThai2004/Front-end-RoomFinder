package com.pht.roomfinder.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.pht.roomfinder.helper.AppLocale
import com.pht.roomfinder.helper.DataLocal

class App : Application() {
    @SuppressLint("StaticFieldLeak")
    companion object {
        private var context: Context? = null

        fun getContext(): Context? {
            return context
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        DataLocal.init(applicationContext)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(AppLocale.setAppLocale(base!!))
    }
}