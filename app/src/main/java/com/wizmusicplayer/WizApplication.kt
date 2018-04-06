package com.wizmusicplayer

import android.app.Application

class WizApplication : Application() {

    companion object {
        lateinit var instance: WizApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}