package com.wizmusicplayer

import java.util.*

class MusicUtils {
    companion object {
        fun convertDuration(durationInMs: Long): String {
            val durationInSeconds = durationInMs / 1000
            val seconds = durationInSeconds % 60
            val minutes = durationInSeconds % 3600 / 60
            val hours = durationInSeconds / 3600
            return if (hours > 0) {
                String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
            } else String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }
    }
}