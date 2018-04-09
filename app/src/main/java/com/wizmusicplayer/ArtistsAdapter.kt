package com.wizmusicplayer

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.SortedList
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.util.SortedListAdapterCallback
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_artist.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class ArtistsAdapter : RecyclerView.Adapter<ArtistsAdapter.ViewHolder>(), AnkoLogger{

    private val artistList: SortedList<Artist>

    init {
        artistList = SortedList(Artist::class.java, object : SortedListAdapterCallback<Artist>(this) {
            override fun compare(o1: Artist, o2: Artist): Int = o1.artistName.compareTo(o2.artistName)

            override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean = oldItem == newItem

            override fun areItemsTheSame(item1: Artist, item2: Artist): Boolean = item1.artistId == item2.artistId
        })
    }

    fun addArtists(incomingArtistList: List<Artist>) {
        info { artistList.toString() }
        artistList.addAll(incomingArtistList)
    }

    override fun getItemCount() = artistList.size()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_artist))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(artistList.get(position))
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(artist: Artist) {
            itemView.artistDetails.text = artist.artistAlbums
                    .plus(" Albums")
                    .plus(" \u2022 ")
                    .plus(artist.artistTracks)
                    .plus(" Tracks")
            itemView.artistName.text = artist.artistCover
            itemView.artistCoverImage.loadURL(artist.artistCover)
        }
    }
}