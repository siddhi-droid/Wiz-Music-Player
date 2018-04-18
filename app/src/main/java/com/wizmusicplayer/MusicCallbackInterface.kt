package com.wizmusicplayer


interface MusicCallbackInterface {
    fun playTrack(position: Int, musicList: ArrayList<MusicTrack>)
}