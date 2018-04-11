package com.wizmusicplayer

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_album.view.*

class AlbumsAdapter : ListAdapter<MusicAlbum, AlbumsAdapter.ViewHolder>(AlbumsDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_album))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getLetterPosition(letter: String): Int {
        for (track in 0 until itemCount) {
            if (getItem(track).albumName.toUpperCase().take(1) == letter)
                return track
        }
        return -1
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(musicAlbum: MusicAlbum) {
            itemView.albumDetails.text = musicAlbum.albumArtist
                    .plus(" \u2022 ")
                    .plus(musicAlbum.albumSongs)
                    .plus(" Tracks")
            itemView.albumName.text = musicAlbum.albumName
            itemView.albumCoverImage.loadURL(musicAlbum.albumArt)
        }
    }
}