package com.wizmusicplayer.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.wizmusicplayer.Artist
import com.wizmusicplayer.Genre
import com.wizmusicplayer.MusicAlbum
import com.wizmusicplayer.MusicTrack
import javax.inject.Inject

class MusicViewModel @Inject constructor(private val musicRepository: MusicRepository) : ViewModel() {

    fun getAllTracks(): LiveData<List<MusicTrack>> {
        return musicRepository.getAllTracks()
    }

    fun getAllArtists(): LiveData<List<Artist>> {
        return musicRepository.getAllArtists()
    }

    fun getAllAlbums(): LiveData<List<MusicAlbum>> {
        return musicRepository.getAllAlbums()
    }

    fun getAllGenre(): LiveData<List<Genre>> {
        return musicRepository.getAllGenre()
    }
}