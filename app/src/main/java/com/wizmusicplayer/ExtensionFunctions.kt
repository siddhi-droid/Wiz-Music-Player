package com.wizmusicplayer

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import okhttp3.Call

fun ImageView.loadURL(url: String) {
    GlideApp
            .with(context)
            .load(url)
            .placeholder(R.drawable.audio_track)
            .error(R.drawable.audio_track)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
}

fun RecyclerView.withLinearLayout(context: Context) {
    this.layoutManager = LinearLayoutManager(context)
}

fun RecyclerView.withGridLayout2X2(context: Context) {
    this.layoutManager = GridLayoutManager(context, 2)
    this.addItemDecoration(SpacesItemDecoration(2))
}

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}


