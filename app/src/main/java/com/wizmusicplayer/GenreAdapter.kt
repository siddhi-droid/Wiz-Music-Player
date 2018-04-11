package com.wizmusicplayer

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_genre.view.*

class GenreAdapter : ListAdapter<Genre, GenreAdapter.ViewHolder>(GenreDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_genre))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getLetterPosition(letter: String): Int {
        for (track in 0 until itemCount) {
            if (getItem(track).name.toUpperCase().take(1) == letter)
                return track
        }
        return -1
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(genre: Genre) {
            itemView.genreDetails.text = genre.count.toString().plus(" Tracks")
            itemView.genreName.text = genre.name
            itemView.genreCoverImage.loadURL(genre.name)
        }
    }
}