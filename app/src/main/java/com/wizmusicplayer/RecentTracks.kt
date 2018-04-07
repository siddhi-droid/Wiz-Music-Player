package com.wizmusicplayer

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class RecentTracks(
        @PrimaryKey
        val id: Long,
        val title: String,
        val albumId: String,
        val artist: String,
        val duration: String,
        val trackArt: String = "")
