package com.wizmusicplayer.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module(includes = [(ApplicationContextModule::class)])
class SharedPreferencesModule {

    @Provides
    @Singleton
    fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("Wiz_Preferences", Context.MODE_PRIVATE)
    }
}