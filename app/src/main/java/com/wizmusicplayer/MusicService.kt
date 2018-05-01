package com.wizmusicplayer

import android.app.PendingIntent
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.MediaStore
import android.support.annotation.Nullable
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import androidx.core.net.toUri
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.lang.IllegalStateException
import android.content.Intent


class MusicService : MediaBrowserServiceCompat(), AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnCompletionListener, AnkoLogger {

    private var mediaSession: MediaSessionCompat? = null
    private var mediaPlayer: MediaPlayer? = null
    private var currentTrack: MusicTrack? = null
    private var notificationManager: com.wizmusicplayer.NotificationManager? = null
    private var playbackStateBuilder: PlaybackStateCompat.Builder? = null
    private var metadataBuilder: MediaMetadataCompat.Builder? = null
    private var trackPosition: Int = 0
    private var musicList: java.util.ArrayList<MusicTrack>? = ArrayList()
    private var trackBitmap: Bitmap? = null
    private var isServiceStarted: Boolean = false

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
        if (!mediaPlayer?.isPlaying!!) {
            mediaPlayer?.start()
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
            updateNotification()
        }
        mediaPlayer?.setVolume(1.0f, 1.0f)
    }

    private fun audioManagerTransientCanDuck() {
        mediaPlayer?.setVolume(0.3f, 0.3f)
    }

    private fun audioManagerTransient() {
        mediaPlayer?.pause()
        setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
        updatePauseNotification()
    }

    private fun audioManagerLoss() {
        if (mediaPlayer?.isPlaying!!) {
            mediaPlayer?.pause()
            setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
            updatePauseNotification()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        info { "onStartCommand" }
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return START_NOT_STICKY
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
        notificationManager = NotificationManager()
        notificationManager?.initNotificationManager(this)
    }

    private fun initNoisyReceiver() {
        val filter = IntentFilter(AudioManager.ACTION_HEADSET_PLUG)
        registerReceiver(mNoisyReceiver, filter)
    }

    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer?.setVolume(1.0f, 1.0f)
        mediaPlayer?.setOnCompletionListener(this)
    }

    private val mNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_HEADSET_PLUG) {
                val state = intent.getIntExtra("state", -1)
                when (state) {
                    0 -> headPhoneUnPlugged()
                }
            }
        }
    }

    private fun headPhoneUnPlugged() {
        if (mediaPlayer?.isPlaying!!) {
            mediaPlayer?.pause()
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
            updatePauseNotification()
        }
    }

    private fun initMediaSession() {
        val mediaButtonReceiver = ComponentName(applicationContext, MediaButtonReceiver::class.java)
        mediaSession = MediaSessionCompat(applicationContext, "Wiz_Music_Service", mediaButtonReceiver, null)

        mediaSession?.setCallback(MediaSessionCallback())

        mediaSession?.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                        MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS or
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        mediaButtonIntent.setClass(this, MediaButtonReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0)
        mediaSession?.setMediaButtonReceiver(pendingIntent)

        sessionToken = mediaSession?.sessionToken
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

            mediaPlayer?.start()
            mediaSession?.isActive = true
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
            updateNotification()
        }

        override fun onPause() {
            super.onPause()
            if (mediaPlayer?.isPlaying!!) {
                mediaPlayer?.pause()
                setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
                updatePauseNotification()
            }
        }

        override fun onStop() {
            super.onStop()
            onDestroy()
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
        }

        override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
            super.onPlayFromUri(uri, extras)

            readBundle(extras)

            currentTrack = musicList?.get(trackPosition)

            playSong(musicList?.get(trackPosition))
        }

        override fun onSkipToNext() {
            playNextTrack()
        }

        override fun onSkipToPrevious() {
            playPreviousTrack()
        }
    }

    private fun playPreviousTrack() {

        trackPosition--

        if (trackPosition >= 0) {

            currentTrack = musicList?.get(trackPosition)

            playSong(musicList?.get(trackPosition))

        }
    }

    private fun playNextTrack() {

        trackPosition = (++trackPosition % musicList?.size!!)

        currentTrack = musicList?.get(trackPosition)

        playSong(musicList?.get(trackPosition))

    }


    private fun readBundle(extras: Bundle?) {
        extras?.classLoader = this@MusicService.classLoader
        musicList = extras?.getParcelableArrayList(Config.MusicConfig.MUSIC_TRACKS)
        trackPosition = extras?.get(Config.MusicConfig.MUSIC_TRACK_POSITION) as Int
    }


    private fun playSong(musicTrack: MusicTrack?) {

        launch {
            try {
                trackBitmap = MediaStore.Images.Media.getBitmap(this@MusicService.contentResolver, musicTrack?.trackArt?.toUri())
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }

        if (mediaPlayer == null)
            initMediaPlayer()

        mediaPlayer?.reset()

        mediaPlayer?.setDataSource(this@MusicService, musicTrack?.fileUri)

        mediaPlayer?.prepareAsync()

        mediaPlayer?.setOnPreparedListener {

            if (successfullyRetrievedAudioFocus()) {
                mediaSession?.isActive = true
                mediaPlayer?.start()
                setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
                initMediaSessionMetadata()
                updateNotification()
            }
        }
    }

    private fun setMediaPlaybackState(state: Int) {
        playbackStateBuilder = PlaybackStateCompat.Builder()
        playbackStateBuilder?.setActions(
                PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_STOP)
        playbackStateBuilder?.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0f)
        mediaSession?.setPlaybackState(playbackStateBuilder?.build())
    }


    private fun initMediaSessionMetadata() {
        metadataBuilder = MediaMetadataCompat.Builder()
        metadataBuilder?.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, trackBitmap)
        metadataBuilder?.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, trackBitmap)
        metadataBuilder?.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
        metadataBuilder?.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, currentTrack?.title)
        metadataBuilder?.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, currentTrack?.artist)
        mediaSession?.setMetadata(metadataBuilder?.build())
    }

    override fun onCompletion(mp: MediaPlayer?) {
        playNextTrack()
    }

    private fun updateNotification() {
        metadataBuilder?.let {
            val notification = notificationManager?.getNotification(metadataBuilder?.build(), playbackStateBuilder?.build(), mediaSession?.sessionToken)
            if (!isServiceStarted) {
                ContextCompat.startForegroundService(this@MusicService,
                        Intent(this@MusicService, MusicService::class.java))
                isServiceStarted = true
            }
            startForeground(1337, notification)
        }
    }

    private fun updatePauseNotification() {
        metadataBuilder?.let {
            val notification = notificationManager?.getNotification(metadataBuilder?.build(), playbackStateBuilder?.build(), mediaSession?.sessionToken)
            notificationManager?.getNotificationManager()?.notify(1337, notification)
        }
    }

    override fun onDestroy() {
        info { "onDestroy" }
        stopForeground(true)
        stopSelf()
        isServiceStarted = false
        try {
            unregisterReceiver(mNoisyReceiver)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.abandonAudioFocus(this)
        mediaSession?.release()
        mediaPlayer?.release()
        mediaPlayer = null
        NotificationManagerCompat.from(this).cancel(1337)
    }

}
