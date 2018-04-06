package com.wizmusicplayer

data class MusicTrack(
        val id: Long,
        val title: String,
        val albumId: String,
        val artist: String,
        val duration: String,
        val trackArt: String = "")
