package com.wizmusicplayer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync

class MainActivity : AppCompatActivity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        doAsync {
            val tracksList = MusicGenerator.getAllTracks()
            val artistList = MusicGenerator.getAllArtists()
            val albumsList = MusicGenerator.getAllAlbums()
            MusicGenerator.getGenre()
        }
    }
}
