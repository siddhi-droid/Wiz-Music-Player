package com.wizmusicplayer.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.wizmusicplayer.MusicGenerator
import com.wizmusicplayer.MusicTrack
import com.wizmusicplayer.database.WizDatabase
import com.wizmusicplayer.networking.APIService
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import javax.inject.Inject


class MusicRepository @Inject constructor(private val apiService: APIService, private val wizDatabase: WizDatabase) {


    fun getAllTracks(): LiveData<List<MusicTrack>> {

        val tracksListLiveData = MutableLiveData<List<MusicTrack>>()

        doAsync {
            val trackList = MusicGenerator.getAllTracks()
            onComplete {
                tracksListLiveData.value = trackList
            }
        }

        return tracksListLiveData
    }


}