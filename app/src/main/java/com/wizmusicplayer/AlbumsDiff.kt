package com.wizmusicplayer

import android.support.v7.util.DiffUtil


class AlbumsDiff : DiffUtil.ItemCallback<MusicAlbum>() {

    override fun areItemsTheSame(oldItem: MusicAlbum?, newItem: MusicAlbum?): Boolean {
        return oldItem?.albumId == newItem?.albumId
    }

    override fun areContentsTheSame(oldItem: MusicAlbum?, newItem: MusicAlbum?): Boolean {
        return oldItem == newItem
    }
}