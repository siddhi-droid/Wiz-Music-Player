package com.wizmusicplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import org.jetbrains.anko.AnkoLogger


class NotificationManager : AnkoLogger {

    private lateinit var mService: MusicService
    private val CHANNEL_ID = "Wiz Music"
    private val REQUEST_CODE: Int = 999

    private lateinit var mPlayAction: NotificationCompat.Action
    private lateinit var mPauseAction: NotificationCompat.Action
    private lateinit var mNextAction: NotificationCompat.Action
    private lateinit var mPrevAction: NotificationCompat.Action
    private lateinit var mNotificationManager: NotificationManager

    fun initNotificationManager(service: MusicService) {

        mService = service

        mNotificationManager = mService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mPlayAction = NotificationCompat.Action(
                R.drawable.ic_play_arrow_black_24dp,
                "Play",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mService,
                        PlaybackStateCompat.ACTION_PLAY))
        mPauseAction = NotificationCompat.Action(
                R.drawable.ic_pause_black_24dp,
                "Pause",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mService,
                        PlaybackStateCompat.ACTION_PAUSE))
        mNextAction = NotificationCompat.Action(
                R.drawable.ic_skip_next_black_24dp,
                "Next",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mService,
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT))
        mPrevAction = NotificationCompat.Action(
                R.drawable.ic_skip_previous_black_24dp,
                "Previous",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mService,
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))

        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
        mNotificationManager.cancelAll()
    }


    fun getNotificationManager(): NotificationManager {
        return mNotificationManager
    }

    fun getNotification(metadata: MediaMetadataCompat,
                        playbackState: PlaybackStateCompat,
                        token: MediaSessionCompat.Token): Notification {
        val isPlaying = playbackState.state == PlaybackStateCompat.STATE_PLAYING
        val description = metadata.description
        val builder = buildNotification(playbackState, token, isPlaying, description)
        return builder.build()
    }

    private fun buildNotification(state: PlaybackStateCompat,
                                  token: MediaSessionCompat.Token,
                                  isPlaying: Boolean,
                                  description: MediaDescriptionCompat): NotificationCompat.Builder {

        if (isAndroidOreo()) {
            createChannel()
        }

        val builder = NotificationCompat.Builder(mService, CHANNEL_ID)
                .setColor(ContextCompat.getColor(mService, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_music_note_amber_a200_24dp)
                // Pending intent that is fired when user clicks on notification.
                // Title - Usually Song name.
                .setContentTitle(description.title)
                .setContentIntent(createContentIntent(context = mService))
                // Subtitle - Usually Artist name.
                .setContentText(description.subtitle)
                // When notification is deleted (when playback is paused and notification can be
                // deleted) fire MediaButtonPendingIntent with ACTION_STOP.
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mService, PlaybackStateCompat.ACTION_STOP))
                // Show controls on lock screen even when user hides sensitive content.
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)


        builder.addAction(mPrevAction)
        builder.addAction(if (isPlaying) mPauseAction else mPlayAction)
        builder.addAction(mNextAction)


        builder.setStyle(
                android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(token)
                        .setShowActionsInCompactView(2)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(
                                MediaButtonReceiver.buildMediaButtonPendingIntent(
                                        mService,
                                        PlaybackStateCompat.ACTION_STOP)))

        return builder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        if (mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            val name = "Wiz Music"
            val description = "This channel provides music playback to Wiz Music"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.YELLOW
            mNotificationManager.createNotificationChannel(mChannel)
        }
    }

    private fun isAndroidOreo(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }


    private fun createContentIntent(context: Context): PendingIntent {

        val requestID = System.currentTimeMillis().toInt()

        val notificationIntent = Intent(context, Dashboard::class.java)

        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        return PendingIntent.getActivity(context, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    }


}