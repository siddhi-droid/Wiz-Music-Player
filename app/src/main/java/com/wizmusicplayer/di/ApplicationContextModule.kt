package com.wizmusicplayer.di

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationContextModule(private val context: Context) {

    @Provides
    @Singleton
    fun getContext(): Context = context

}