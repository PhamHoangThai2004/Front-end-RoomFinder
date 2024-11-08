package com.pht.roomfinder.utils

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        DataLocal.init(applicationContext)
    }

}