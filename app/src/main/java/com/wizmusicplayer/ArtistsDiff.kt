package com.wizmusicplayer

import android.support.v7.util.DiffUtil


class ArtistsDiff : DiffUtil.ItemCallback<Artist>() {

    override fun areItemsTheSame(oldItem: Artist?, newItem: Artist?): Boolean {
        return oldItem?.artistId == newItem?.artistId
    }

    override fun areContentsTheSame(oldItem: Artist?, newItem: Artist?): Boolean {
        return oldItem == newItem
    }

}