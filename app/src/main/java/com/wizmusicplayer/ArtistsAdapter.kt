package com.wizmusicplayer

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_artist.view.*

class ArtistsAdapter : ListAdapter<Artist, ArtistsAdapter.ViewHolder>(ArtistsDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_artist))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(artist: Artist) {
            itemView.artistDetails.text = artist.artistAlbums
                    .plus(" Albums")
                    .plus(" \u2022 ")
                    .plus(artist.artistTracks)
                    .plus(" Tracks")
            itemView.artistName.text = artist.artistName
        }
    }
}