package com.wizmusicplayer

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_music_track.view.*

class MusicTrackAdapter(var tracksInterface: TracksInterface) : ListAdapter<MusicTrack, MusicTrackAdapter.ViewHolder>(MusicTracksDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_music_track))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getLetterPosition(letter: String): Int {
        for (track in 0 until itemCount) {
            if (getItem(track).title.toUpperCase().take(1) == letter)
                return track
        }
        return -1
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(musicTrack: MusicTrack) {
            itemView.trackArtist.text = musicTrack.artist.plus(" \u2022 ").plus(MusicUtils.convertDuration(musicTrack.duration.toLong()))
            itemView.trackTitle.text = musicTrack.title
            itemView.trackCoverImage.loadURL(musicTrack.trackArt)
            if (adapterPosition == -1)
                return
            itemView.rootLayout.setOnClickListener { tracksInterface.onClick(adapterPosition, getAllTracks()) }
        }
    }


    fun getAllTracks(): ArrayList<MusicTrack> {
        val tracksList: ArrayList<MusicTrack> = ArrayList()
        for (track in 0 until itemCount) {
            tracksList.add(getItem(track))
        }

        return tracksList
    }

    interface TracksInterface {
        fun onClick(position: Int, musicList: ArrayList<MusicTrack>)
    }
}