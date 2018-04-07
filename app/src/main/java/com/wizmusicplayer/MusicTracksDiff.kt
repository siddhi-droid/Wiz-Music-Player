package com.wizmusicplayer

import android.support.v7.util.DiffUtil


class MusicTracksDiff : DiffUtil.ItemCallback<MusicTrack>() {

    override fun areItemsTheSame(oldItem: MusicTrack?, newItem: MusicTrack?): Boolean {
        return oldItem?.id === newItem?.id
    }

    override fun areContentsTheSame(oldItem: MusicTrack?, newItem: MusicTrack?): Boolean {
        return false
    }
}