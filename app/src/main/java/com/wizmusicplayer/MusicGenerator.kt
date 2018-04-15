package com.wizmusicplayer

import android.annotation.SuppressLint
import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore

class MusicGenerator {

    companion object {

        private val albumArtUri = Uri.parse("content://media/external/audio/albumart")

        private const val BASE_SELECTION = MediaStore.Audio.AudioColumns.IS_MUSIC + "=1" + " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''"

        private val BASE_PROJECTION = arrayOf(BaseColumns._ID,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.ALBUM_ID,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.DATA)

        @SuppressLint("Recycle")
        fun getAllTracks(): List<MusicTrack> {

            val allTracksList: MutableList<MusicTrack> = ArrayList()

            val projection = arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA)

            val cursor = WizApplication.instance.contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    MediaStore.Audio.Media.IS_MUSIC + "=1",
                    null,
                    "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC"
            ) ?: return listOf()

            try {
                if (cursor.moveToFirst()) {
                    do {
                        val musicTrack = MusicTrack(
                                id = cursor.getLong(0),
                                title = cursor.getString(1),
                                artist = cursor.getString(2),
                                albumId = cursor.getString(3),
                                duration = "${cursor.getLong(4)}",
                                trackArt = "${ContentUris.withAppendedId(albumArtUri,
                                        cursor.getString(3).toLong())}",
                                fileUri = Uri.parse(cursor.getString(5)))

                        allTracksList.add(musicTrack)
                    } while (cursor.moveToNext())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor.close()
            }

            return allTracksList
        }

        @SuppressLint("Recycle")
        fun getAllArtists(): List<Artist> {

            val artistList: MutableList<Artist> = ArrayList()

            val projection = arrayOf(MediaStore.Audio.Artists._ID,
                    MediaStore.Audio.Artists.ARTIST,
                    MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
                    MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)

            val cursor = WizApplication.instance.contentResolver.query(
                    MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    MediaStore.Audio.Artists.ARTIST + " ASC") ?: return listOf()

            try {
                if (cursor.moveToFirst()) {
                    do {
                        val artist = Artist(
                                artistId = cursor.getLong(0),
                                artistName = cursor.getString(1),
                                artistTracks = cursor.getString(2),
                                artistAlbums = cursor.getString(3))

                        artistList.add(artist)
                    } while (cursor.moveToNext())
                }
            } catch (e: RuntimeException) {
                e.printStackTrace()
            } finally {
                cursor.close()
            }
            return artistList
        }


        @SuppressLint("Recycle")
        fun getAllAlbums(): List<MusicAlbum> {

            val albumsList: MutableList<MusicAlbum> = ArrayList()

            val projection = arrayOf(MediaStore.Audio.Albums._ID,
                    MediaStore.Audio.Albums.ALBUM,
                    MediaStore.Audio.Albums.ARTIST,
                    MediaStore.Audio.Albums.ALBUM_ART,
                    MediaStore.Audio.Albums.NUMBER_OF_SONGS)

            val cursor = WizApplication.instance.contentResolver.query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    projection, null,
                    null,
                    MediaStore.Audio.Media.ALBUM + " ASC") ?: return listOf()

            try {
                if (cursor.moveToFirst()) {
                    do {
                        val musicAlbum = MusicAlbum(
                                albumId = cursor.getString(0),
                                albumName = cursor.getString(1),
                                albumArtist = cursor.getString(2),
                                albumArt = cursor.getString(3) ?: "",
                                albumSongs = cursor.getString(4))
                        albumsList.add(musicAlbum)
                    } while (cursor.moveToNext())
                }
            } catch (e: RuntimeException) {
                e.printStackTrace()
            } finally {
                cursor.close()
            }
            return albumsList
        }

        @SuppressLint("Recycle")
        fun getSongsForAlbum(albumID: Long): List<MusicTrack> {

            val tracksList: MutableList<MusicTrack> = ArrayList()

            val projection = arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA)

            val selection = "is_music=1 AND title != '' AND album_id=$albumID"

            val cursor = WizApplication.instance.contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null,
                    MediaStore.Audio.Media.ARTIST + " ASC") ?: return listOf()

            try {
                if (cursor.moveToFirst())
                    do {
                        val musicTrack = MusicTrack(
                                id = cursor.getLong(0),
                                title = cursor.getString(1),
                                artist = cursor.getString(2),
                                albumId = cursor.getString(3),
                                duration = cursor.getString(4),
                                trackArt = "${ContentUris.withAppendedId(albumArtUri,
                                        cursor.getString(3).toLong())}",
                                fileUri = Uri.parse(cursor.getString(5)))

                        tracksList.add(musicTrack)
                    } while (cursor.moveToNext())
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor.close()
            }
            return tracksList
        }


        @SuppressLint("Recycle")
        fun getSongsForArtist(artistId: Long): List<MusicTrack> {

            val tracksList: MutableList<MusicTrack> = ArrayList()

            val projection = arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA)

            val selection = "is_music=1 AND title != '' AND artist_id=$artistId"

            val cursor = WizApplication.instance.contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null,
                    MediaStore.Audio.Media.ARTIST + " ASC") ?: return listOf()

            try {
                if (cursor.moveToFirst())
                    do {
                        val musicTrack = MusicTrack(
                                id = cursor.getLong(0),
                                title = cursor.getString(1),
                                artist = cursor.getString(2),
                                albumId = cursor.getString(3),
                                duration = cursor.getString(4),
                                trackArt = "${ContentUris.withAppendedId(albumArtUri,
                                        cursor.getString(3).toLong())}",
                                fileUri = Uri.parse(cursor.getString(5)))

                        tracksList.add(musicTrack)
                    } while (cursor.moveToNext())
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor.close()
            }
            return tracksList
        }

        @SuppressLint("Recycle")
        fun getGenre(): List<Genre> {

            val genreList: MutableList<Genre> = ArrayList()

            val projection = arrayOf(
                    MediaStore.Audio.Genres._ID,
                    MediaStore.Audio.Genres.NAME)

            val cursor = WizApplication.instance.contentResolver.query(
                    MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    MediaStore.Audio.Genres.DEFAULT_SORT_ORDER) ?: return listOf()

            try {
                if (cursor.moveToFirst())
                    do {
                        val genre = Genre(
                                id = cursor.getLong(0),
                                name = cursor.getString(1),
                                count = getSongsForGenre(cursor.getLong(0)).size.toLong())
                        if (genre.count > 0)
                            genreList.add(genre)
                    } while (cursor.moveToNext())
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor.close()
            }
            return genreList
        }

        @SuppressLint("Recycle")
        private fun getSongsForGenre(genreId: Long): List<MusicTrack> {

            val musicTrackList: MutableList<MusicTrack> = ArrayList()

            val cursor = WizApplication.instance.contentResolver.query(
                    MediaStore.Audio.Genres.Members.getContentUri("external", genreId),
                    BASE_PROJECTION,
                    BASE_SELECTION,
                    null,
                    null) ?: return listOf()

            try {
                if (cursor.moveToFirst()) {
                    do {
                        val musicTrack = MusicTrack(
                                id = cursor.getLong(0),
                                title = cursor.getString(1),
                                artist = cursor.getString(2),
                                albumId = cursor.getString(3),
                                duration = "${cursor.getLong(4)}",
                                trackArt = "${ContentUris.withAppendedId(albumArtUri,
                                        cursor.getString(3).toLong())}",
                                fileUri = Uri.parse(cursor.getString(5)))
                        musicTrackList.add(musicTrack)
                    } while (cursor.moveToNext())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor.close()
            }
            return musicTrackList
        }
    }
}