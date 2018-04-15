package com.wizmusicplayer

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class MusicTrack(
        val id: Long,
        val title: String,
        val albumId: String,
        val artist: String,
        val duration: String,
        val trackArt: String = "",
        val fileUri: Uri) : Parcelable
