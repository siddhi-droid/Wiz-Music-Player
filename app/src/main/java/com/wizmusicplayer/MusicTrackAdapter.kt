package com.wizmusicplayer

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_music_track.view.*

class MusicTrackAdapter : ListAdapter<MusicTrack, MusicTrackAdapter.ViewHolder>(MusicTracksDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_music_track))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(musicTrack: MusicTrack) {
            itemView.trackArtist.text = musicTrack.artist.plus(" \u2022 ").plus(MusicUtils.convertDuration(musicTrack.duration.toLong()))
            itemView.trackCoverImage.loadURL(musicTrack.trackArt)
            itemView.trackTitle.text = musicTrack.title
        }
    }
}