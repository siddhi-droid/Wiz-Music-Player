package com.wizmusicplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
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

    private var mService: MusicService? = null
    private val CHANNEL_ID = "Wiz Music"

    private lateinit var mPlayAction: NotificationCompat.Action
    private lateinit var mPauseAction: NotificationCompat.Action
    private lateinit var mNextAction: NotificationCompat.Action
    private lateinit var mPrevAction: NotificationCompat.Action
    private lateinit var mStopAction: NotificationCompat.Action
    private lateinit var mNotificationManager: NotificationManager

    fun initNotificationManager(service: MusicService) {

        mService = service

        mNotificationManager = mService!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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
        mStopAction = NotificationCompat.Action(
                R.drawable.ic_clear_black_24dp,
                "Previous",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mService,
                        PlaybackStateCompat.ACTION_STOP))

        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
        mNotificationManager.cancelAll()
    }

    fun getNotificationManager(): NotificationManager {
        return mNotificationManager
    }

    fun getNotification(metadata: MediaMetadataCompat?, playbackState: PlaybackStateCompat?, token: MediaSessionCompat.Token?): Notification {
        val isPlaying = playbackState?.state == PlaybackStateCompat.STATE_PLAYING
        val description = metadata?.description
        val builder = buildNotification(playbackState, token, isPlaying, description)
        return builder.build()
    }

    private fun buildNotification(state: PlaybackStateCompat?,
                                  token: MediaSessionCompat.Token?,
                                  isPlaying: Boolean?,
                                  description: MediaDescriptionCompat?): NotificationCompat.Builder {

        // Create the (mandatory) notification channel when running on Android Oreo.
        if (isAndroidOreo()) {
            createChannel()
        }

        val builder = NotificationCompat.Builder(mService!!, CHANNEL_ID)
        builder.setStyle(
                android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(token)
                        .setShowActionsInCompactView(0, 1, 2)
                        // For backwards compatibility with Android L and earlier.
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(
                                MediaButtonReceiver.buildMediaButtonPendingIntent(
                                        mService,
                                        PlaybackStateCompat.ACTION_STOP)))
                .setSmallIcon(R.drawable.ic_music_note_amber_a200_24dp)
                // Pending intent that is fired when user clicks on notification.
                .setContentIntent(createContentIntent(mService!!))
                // Title - Usually Song name.
                .setContentTitle(description?.title)
                // Subtitle - Usually Artist name.
                .setContentText(description?.subtitle)
                .setLargeIcon(description?.iconBitmap)
                // Show controls on lock screen even when user hides sensitive content.
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)


        builder.addAction(mStopAction)
        builder.addAction(mPrevAction)
        builder.addAction(if (isPlaying!!) mPauseAction else mPlayAction)
        builder.addAction(mNextAction)

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


    private fun getDeleteIntent(): PendingIntent {
        val intent = Intent(mService, com.wizmusicplayer.NotificationManager::class.java)
        intent.action = "NMStop"
        return PendingIntent.getBroadcast(mService, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    private fun createContentIntent(context: Context): PendingIntent {

        val notificationIntent = Intent(context, Dashboard::class.java)

        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        return PendingIntent.getActivity(context, 500, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    }

}