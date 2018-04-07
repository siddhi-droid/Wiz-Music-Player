package com.wizmusicplayer.di

import com.wizmusicplayer.MusicFragment
import com.wizmusicplayer.ui.MusicViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(ApplicationContextModule::class), (SharedPreferencesModule::class), (NetworkModule::class), (DatabaseModule::class)])
interface ApplicationComponent {

    fun inject(musicViewModel: MusicViewModel)
    fun inject(musicFragment: MusicFragment)
}