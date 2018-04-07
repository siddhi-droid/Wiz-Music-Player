package com.wizmusicplayer.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.wizmusicplayer.MusicTrack
import javax.inject.Inject

class MusicViewModel @Inject constructor(private val musicRepository: MusicRepository) : ViewModel() {

    fun getAllTracks(): LiveData<List<MusicTrack>> {
        return musicRepository.getAllTracks()
    }
}