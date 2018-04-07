package com.wizmusicplayer.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.wizmusicplayer.ui.MusicRepository
import com.wizmusicplayer.ui.MusicViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomViewModelFactory @Inject
constructor(private val musicRepository: MusicRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return if (modelClass.isAssignableFrom(MusicViewModel::class.java))
            MusicViewModel(musicRepository) as T
        else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}
