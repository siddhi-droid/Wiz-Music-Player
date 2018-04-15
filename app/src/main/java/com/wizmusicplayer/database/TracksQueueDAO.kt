package com.wizmusicplayer.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query
import com.wizmusicplayer.MusicTrack

@Dao
interface TracksQueueDAO {

    @Insert
    fun saveTracksQueue(musicTrack: List<MusicTrack>)


    @Query("Select * from MusicTrack")
    fun getAllTracksQueue(): List<MusicTrack>

}
