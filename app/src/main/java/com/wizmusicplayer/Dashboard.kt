package com.wizmusicplayer

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import androidx.core.net.toUri
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter
import com.wizmusicplayer.ui.MusicViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.info
import javax.inject.Inject


class Dashboard : AppCompatActivity(), MusicCallbackInterface, AnkoLogger {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var musicViewModel: MusicViewModel
    private lateinit var viewPagerAdapter: DashboardViewPagerAdapter
    private lateinit var mMediaBrowserCompat: MediaBrowserCompat
    private var mCurrentState: Int = 0

    private val mMediaBrowserCompatConnectionCallback = object : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            super.onConnected()
            try {
                val token = mMediaBrowserCompat.sessionToken

                val mediaController = MediaControllerCompat(this@Dashboard,
                        token)

                MediaControllerCompat.setMediaController(this@Dashboard, mediaController)

                mediaController.registerCallback(mMediaControllerCompatCallback)

                info { "Connected" }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }

        }


        override fun onConnectionSuspended() {
            info { "onConnectionSuspended" }
        }

        override fun onConnectionFailed() {
            info { "onConnectionFailed" }
        }
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        }
    }

    private val mMediaControllerCompatCallback = object : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            if (state == null) {
                return
            }

            when (state.state) {
                PlaybackStateCompat.STATE_PLAYING -> {
                    mCurrentState = STATE_PLAYING
                }
                PlaybackStateCompat.STATE_PAUSED -> {
                    mCurrentState = STATE_PAUSED
                }
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            info { metadata }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setStatusBarColor()
        initViewModel()
        initMediaSession()
        initBottomNav()
    }

    private fun initViewModel() {
        WizApplication.instance.getApplicationComponent().inject(this)
        musicViewModel = ViewModelProviders.of(this, viewModelFactory).get(MusicViewModel::class.java)
    }

    private fun initMediaSession() {
        mMediaBrowserCompat = MediaBrowserCompat(this, ComponentName(this, MusicService::class.java), mMediaBrowserCompatConnectionCallback, null)
        mMediaBrowserCompat.connect()
    }

    private fun initBottomNav() {
        viewPagerAdapter = DashboardViewPagerAdapter(supportFragmentManager, getFragmentTitles())
        viewPager.adapter = viewPagerAdapter
        viewPager.offscreenPageLimit = 4

        val tabColors = applicationContext.resources.getIntArray(R.array.tabColors)
        val navigationAdapter = AHBottomNavigationAdapter(this, R.menu.bottom_nav_menu)
        navigationAdapter.setupWithBottomNavigation(bottomNavigation, tabColors)

        bottomNavigation.defaultBackgroundColor = this.resources.getColor(R.color.colorPrimary)
        bottomNavigation.accentColor = this.resources.getColor(android.R.color.white)
        bottomNavigation.isForceTint = true
        bottomNavigation.isTranslucentNavigationEnabled = true
        bottomNavigation.isColored = true

        bottomNavigation.setOnTabSelectedListener { position, _ ->
            viewPager.currentItem = position
            true
        }
    }

    private fun getFragmentTitles(): MutableList<String> {
        val fragmentTitles: MutableList<String> = ArrayList()
        fragmentTitles.add(Config.MusicConfig.TRACKS)
        fragmentTitles.add(Config.MusicConfig.ARTISTS)
        fragmentTitles.add(Config.MusicConfig.ALBUMS)
        fragmentTitles.add(Config.MusicConfig.GENRE)
        return fragmentTitles
    }

    override fun onDestroy() {
        super.onDestroy()
//        MediaControllerCompat.getMediaController(this).playbackState?.let {
//            if (MediaControllerCompat.getMediaController(this).playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
//                MediaControllerCompat.getMediaController(this).transportControls.pause()
//            }
//        }
//
//        mMediaBrowserCompat.disconnect()
    }

    public override fun onStart() {
        super.onStart()
    }

    public override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    override fun playTrack(position: Int, musicList: ArrayList<MusicTrack>) {

        val bundle = bundleOf(
                Config.MusicConfig.MUSIC_TRACKS to musicList,
                Config.MusicConfig.MUSIC_TRACK_POSITION to position)

        MediaControllerCompat.getMediaController(this).transportControls.playFromUri("Hack".toUri(), bundle)

    }
}
