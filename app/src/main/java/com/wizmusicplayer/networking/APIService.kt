package com.wizmusicplayer.networking

import com.wizmusicplayer.ArtistInfo
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url


interface APIService {

    @GET
    fun getArtistImage(@Url url: String): Single<ArtistInfo>

}