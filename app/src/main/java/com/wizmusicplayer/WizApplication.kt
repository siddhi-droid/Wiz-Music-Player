package com.wizmusicplayer

import android.app.Application
import com.wizmusicplayer.di.ApplicationComponent
import com.wizmusicplayer.di.ApplicationContextModule
import com.wizmusicplayer.di.DaggerApplicationComponent

class WizApplication : Application() {

    private lateinit var applicationComponent: ApplicationComponent

    companion object {
        lateinit var instance: WizApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationContextModule(ApplicationContextModule(this))
                .build()
    }


    fun getApplicationComponent(): ApplicationComponent {
        return applicationComponent
    }
}