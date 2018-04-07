package com.wizmusicplayer.di

import android.arch.lifecycle.ViewModelProvider
import android.arch.persistence.room.Room
import android.content.Context
import com.wizmusicplayer.database.WizDatabase
import com.wizmusicplayer.networking.APIService
import com.wizmusicplayer.ui.MusicRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [(ApplicationContextModule::class), (NetworkModule::class)])
class DatabaseModule {

    @Provides
    @Singleton
    fun musicRepository(apiService: APIService, wizDatabase: WizDatabase): MusicRepository {
        return MusicRepository(apiService, wizDatabase)
    }

    @Provides
    @Singleton
    fun growDatabase(context: Context): WizDatabase {
        return Room.databaseBuilder(context, WizDatabase::class.java, "Wiz_DB").build()
    }

    @Provides
    @Singleton
    fun provideViewModelFactory(musicRepository: MusicRepository): ViewModelProvider.Factory {
        return CustomViewModelFactory(musicRepository)
    }
}