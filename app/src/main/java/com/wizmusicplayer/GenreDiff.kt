package com.wizmusicplayer

import android.support.v7.util.DiffUtil


class GenreDiff : DiffUtil.ItemCallback<Genre>() {

    override fun areItemsTheSame(oldItem: Genre?, newItem: Genre?): Boolean {
        return oldItem?.id == newItem?.id
    }

    override fun areContentsTheSame(oldItem: Genre?, newItem: Genre?): Boolean {
        return oldItem == newItem
    }
}