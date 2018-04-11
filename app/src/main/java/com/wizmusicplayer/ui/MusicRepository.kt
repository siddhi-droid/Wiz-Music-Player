package com.wizmusicplayer.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.wizmusicplayer.*
import com.wizmusicplayer.database.WizDatabase
import com.wizmusicplayer.networking.APIService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiConsumer
import io.reactivex.internal.util.HalfSerializer.onComplete
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.*
import javax.inject.Inject


class MusicRepository @Inject constructor(private val apiService: APIService, private val wizDatabase: WizDatabase) : AnkoLogger {

    fun getAllTracks(): LiveData<List<MusicTrack>> {
        val tracksListLiveData = MutableLiveData<List<MusicTrack>>()
        async(CommonPool) {
            tracksListLiveData.postValue(MusicGenerator.getAllTracks())
        }
        return tracksListLiveData
    }

    fun getAllGenre(): LiveData<List<Genre>> {
        val genreListLiveData = MutableLiveData<List<Genre>>()
        async(CommonPool) {
            genreListLiveData.postValue(MusicGenerator.getGenre())
        }
        return genreListLiveData
    }

    fun getAllAlbums(): LiveData<List<MusicAlbum>> {
        val musicAlbumListLiveData = MutableLiveData<List<MusicAlbum>>()
        async(CommonPool) {
            musicAlbumListLiveData.postValue(MusicGenerator.getAllAlbums())
        }
        return musicAlbumListLiveData
    }

    fun getAllArtists(): LiveData<List<Artist>> {
        val artistListLiveData = MutableLiveData<List<Artist>>()
        async(CommonPool) {
            val artistList = MusicGenerator.getAllArtists()
            artistListLiveData.postValue(artistList)
            // getArtistInfo(artistList.map { it.copy() }, artistListLiveData)

        }
        return artistListLiveData
    }

    private fun getArtistInfo(artistList: List<Artist>, artistListLiveData: MutableLiveData<List<Artist>>) {

        artistList.forEachIndexed { index, artist ->
            val url = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&api_key=1ad5e54c3e68bdd4d1c9f0d2f521f216&format=json"
                    .plus("&artist=")
                    .plus(artist.artistName)

            apiService.getArtistImage(url)
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        artistList[index].artistCover = result.artist?.image?.get(2)?.text ?: ""
                        artistListLiveData.postValue(artistList)
                    }, { error ->
                        error.printStackTrace()
                    })
        }
    }
}