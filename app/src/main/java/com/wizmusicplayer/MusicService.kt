package com.wizmusicplayer

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.PowerManager
import android.support.annotation.Nullable
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.*


class MusicService : MediaBrowserServiceCompat(), AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnCompletionListener, AnkoLogger {

    private lateinit var mMediaSession: MediaSessionCompat
    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var musicTrack: MusicTrack
    private lateinit var mNotificationManager: com.wizmusicplayer.NotificationManager
    private lateinit var playbackStateBuilder: PlaybackStateCompat.Builder
    private lateinit var metadataBuilder: MediaMetadataCompat.Builder
    private var trackPosition: Int = 0
    private var trackBitmap: Bitmap? = null

    @Nullable
    override fun onGetRoot(clientPackageName: String, clientUid: Int, @Nullable rootHints: Bundle?): MediaBrowserServiceCompat.BrowserRoot? {
        return if (TextUtils.equals(clientPackageName, packageName)) {
            MediaBrowserServiceCompat.BrowserRoot(getString(R.string.app_name), null)
        } else null
    }

    override fun onLoadChildren(parentId: String, result: MediaBrowserServiceCompat.Result<List<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(listOf())
    }


    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> audiMangerGain()
            AudioManager.AUDIOFOCUS_LOSS -> audioManagerLoss()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> audioManagerTransient()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> audioManagerTransientCanDuck()
        }
    }

    private fun audiMangerGain() {
        if (!mMediaPlayer.isPlaying) {
            mMediaPlayer.start()
        }
        mMediaPlayer.setVolume(1.0f, 1.0f);
    }

    private fun audioManagerTransientCanDuck() {
        mMediaPlayer.setVolume(0.3f, 0.3f);
    }

    private fun audioManagerTransient() {
        mMediaPlayer.pause()
    }

    private fun audioManagerLoss() {
        if (mMediaPlayer.isPlaying) {
            mMediaPlayer.stop()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        info { "onStartCommand" }
        MediaButtonReceiver.handleIntent(mMediaSession, intent)
        setMusic(intent?.getParcelableArrayListExtra(Config.MusicConfig.MUSIC_TRACKS), intent?.getIntExtra(Config.MusicConfig.MUSIC_TRACK_POSITION, 0))
        return START_NOT_STICKY
    }

    private fun setMusic(songsList: ArrayList<MusicTrack>?, position: Int?) {
        position?.let {
            trackPosition = it
        }

        songsList?.let {
            musicTrack = songsList[trackPosition]
        }

        playSong(musicTrack)

    }

    override fun onCreate() {
        info { "onCreate" }
        super.onCreate()
        initMediaPlayer()
        initMediaSession()
        initNotificationManger()
        initNoisyReceiver()
    }


    private fun initNotificationManger() {
        mNotificationManager = NotificationManager()
        mNotificationManager.initNotificationManager(this)
    }

    private fun initNoisyReceiver() {
        val filter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(mNoisyReceiver, filter)
    }

    private fun initMediaPlayer() {
        mMediaPlayer = MediaPlayer()
        mMediaPlayer.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mMediaPlayer.setVolume(1.0f, 1.0f)
    }

    private val mNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (mMediaPlayer.isPlaying) {
                mMediaPlayer.pause()
            }
        }
    }

    private fun initMediaSession() {
        val mediaButtonReceiver = ComponentName(applicationContext, MediaButtonReceiver::class.java)
        mMediaSession = MediaSessionCompat(applicationContext, "Wiz_Music_Service", mediaButtonReceiver, null)

        mMediaSession.setCallback(MediaSessionCallback())
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        mediaButtonIntent.setClass(this, MediaButtonReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0)
        mMediaSession.setMediaButtonReceiver(pendingIntent)

        sessionToken = mMediaSession.sessionToken
    }


    private fun successfullyRetrievedAudioFocus(): Boolean {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val result = audioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)

        return result == AudioManager.AUDIOFOCUS_GAIN
    }

    inner class MediaSessionCallback : MediaSessionCompat.Callback() {
        override fun onPlay() {
            super.onPlay()
            if (!successfullyRetrievedAudioFocus()) {
                return
            }

            mMediaPlayer.start()
            mMediaSession.isActive = true
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
        }

        override fun onPause() {
            super.onPause()
            if (mMediaPlayer.isPlaying) {
                mMediaPlayer.pause()
                setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
            }
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
        }
    }


    private fun playSong(musicTrack: MusicTrack?) {

        mMediaPlayer.reset()

        mMediaPlayer.setDataSource(this@MusicService, musicTrack?.fileUri)

        mMediaPlayer.prepareAsync()

        mMediaPlayer.setOnPreparedListener {

            if (successfullyRetrievedAudioFocus()) {
                mMediaSession.isActive = true
                mMediaPlayer.start()
                setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
                initMediaSessionMetadata()
                updateNotification()
            }
        }
    }

    private fun setMediaPlaybackState(state: Int) {
        playbackStateBuilder = PlaybackStateCompat.Builder()
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PAUSE)
        } else {
            playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PLAY)
        }
        playbackStateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0f)
        mMediaSession.setPlaybackState(playbackStateBuilder.build())
    }


    private fun initMediaSessionMetadata() {
        metadataBuilder = MediaMetadataCompat.Builder()
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, trackBitmap)
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, trackBitmap)
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, musicTrack.title)
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, musicTrack.artist)
        mMediaSession.setMetadata(metadataBuilder.build())
    }


    override fun onCompletion(mp: MediaPlayer?) {
        mMediaPlayer.release()
    }

    private fun updateNotification() {
        val notification = mNotificationManager.getNotification(
                metadataBuilder.build(),
                playbackStateBuilder.build(),
                mMediaSession.sessionToken)
        mNotificationManager.getNotificationManager()
                .notify(1337, notification)
    }

    override fun onDestroy() {
        info { "onDestroy" }
        super.onDestroy()
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.abandonAudioFocus(this)
        unregisterReceiver(mNoisyReceiver)
        mMediaSession.release()
        NotificationManagerCompat.from(this).cancel(1)
    }

}
