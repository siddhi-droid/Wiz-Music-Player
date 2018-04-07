package com.wizmusicplayer.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.wizmusicplayer.RecentTracks


@Database(entities = [RecentTracks::class], version = 1)
abstract class WizDatabase : RoomDatabase()