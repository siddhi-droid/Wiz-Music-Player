package com.wizmusicplayer.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.wizmusicplayer.Artist
import com.wizmusicplayer.MusicGenerator
import com.wizmusicplayer.MusicTrack
import com.wizmusicplayer.database.WizDatabase
import com.wizmusicplayer.networking.APIService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.*
import javax.inject.Inject


class MusicRepository @Inject constructor(private val apiService: APIService, private val wizDatabase: WizDatabase) : AnkoLogger {

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

    fun getAllArtists(): LiveData<List<Artist>> {
        val artistListLiveData = MutableLiveData<List<Artist>>()
        doAsync {
            val artistList = MusicGenerator.getAllArtists()
            onComplete {
                artistListLiveData.value = artistList
                getArtistInfo(artistList, artistListLiveData)
            }
        }
        return artistListLiveData
    }



    private fun getArtistInfo(artistList: List<Artist>, artistListLiveData: MutableLiveData<List<Artist>>) {

        artistList.forEachIndexed { index, artist ->

            val url = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&api_key=1ad5e54c3e68bdd4d1c9f0d2f521f216&format=json"
                    .plus("&artist=")
                    .plus(artist.artistName)

            apiService.getArtistImage(url)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.computation())
                    .subscribe({ result ->
                        artistList[index].artistCover = result.artist.image[2].text
                        artistListLiveData.value = artistList
                    }, { error ->
                        error.printStackTrace()
                    })
        }
    }
}