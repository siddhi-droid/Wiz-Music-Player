package com.wizmusicplayer

data class Artist(
        val artistAlbums: String,
        val artistId: Long,
        val artistName: String,
        val artistTracks: String,
        val artistCover: String = "")
