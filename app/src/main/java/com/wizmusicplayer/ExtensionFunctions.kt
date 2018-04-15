package com.wizmusicplayer

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView


fun ImageView.loadURL(url: String) {
    GlideApp.with(context)
            .load(url)
            .placeholder(R.drawable.audio_track)
            .error(R.drawable.audio_track)
            .into(this)
}

fun ImageView.loadPaletteURL(url: String, title: TextView, subTitle: TextView, background: RelativeLayout) {

}

fun RecyclerView.withLinearLayout(context: Context) {
    this.layoutManager = LinearLayoutManager(context)
    val decoration = PinnedHeaderDecoration()
    decoration.registerTypePinnedHeader(1) { _, _ -> true }
    this.addItemDecoration(decoration)
}

fun RecyclerView.withGridLayout2X2(context: Context) {
    this.layoutManager = GridLayoutManager(context, 2)
    this.addItemDecoration(SpacesItemDecoration(2))
}

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}


