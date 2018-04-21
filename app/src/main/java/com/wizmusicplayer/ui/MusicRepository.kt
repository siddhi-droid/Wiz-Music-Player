package com.wizmusicplayer.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.wizmusicplayer.*
import com.wizmusicplayer.database.WizDatabase
import com.wizmusicplayer.networking.APIService
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.io.IOException
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
            getArtistInfo(artistList.map { it.copy() }, artistListLiveData)
        }
        return artistListLiveData
    }

    private fun getArtistInfo(artistList: List<Artist>, artistListLiveData: MutableLiveData<List<Artist>>) {

        artistList.forEachIndexed { index, artist ->
            val url = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&api_key=1ad5e54c3e68bdd4d1c9f0d2f521f216&format=json"
                    .plus("&artist=")
                    .plus(artist.artistName)

            launch {
                try {
                    val request = apiService.getArtistImage(url)
                    val response = request.await()
                    if (response.isSuccessful) {
                        val data = response.body()
                        artistList[index].artistCover = data?.artist?.image?.get(2)?.text ?: ""
                        artistListLiveData.postValue(artistList)
                    } else {
                        info { "${response.code()}" }
                    }
                } catch (exception: IOException) {
                    exception.printStackTrace()
                } catch (exception: Throwable) {
                    exception.printStackTrace()
                }
            }
        }
    }
}
