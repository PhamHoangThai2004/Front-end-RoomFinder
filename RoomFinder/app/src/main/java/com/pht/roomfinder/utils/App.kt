package com.pht.roomfinder.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.pht.roomfinder.R

class App : Application() {
    @SuppressLint("StaticFieldLeak")
    companion object {
        private var context: Context? = null

        fun getContext(): Context? {
            return context
        }

        fun getKey(id: Int): String {
            return getContext()!!.getString(id)
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        DataLocal.init(applicationContext)
    }

}