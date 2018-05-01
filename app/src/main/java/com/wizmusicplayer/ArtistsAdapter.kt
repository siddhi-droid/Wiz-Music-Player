package com.wizmusicplayer

import android.os.Bundle
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_artist.view.*

class ArtistsAdapter : ListAdapter<Artist, ArtistsAdapter.ViewHolder>(ArtistsDiff()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_artist))
    }

    fun getLetterPosition(letter: String): Int {
        for (track in 0 until itemCount) {
            if (getItem(track).artistName.toUpperCase().take(1) == letter)
                return track
        }
        return -1
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(artist: Artist) {


            itemView.artistDetails.text = artist.artistAlbums
                    .plus(" Albums")
                    .plus(" \u2022 ")
                    .plus(artist.artistTracks)
                    .plus(" Tracks")
            itemView.artistName.text = artist.artistName
            itemView.artistCoverImage.loadURL(artist.artistCover)
        }
    }
}