package com.wizmusicplayer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger

class Dashboard : AppCompatActivity(), AnkoLogger {

    private lateinit var viewPagerAdapter: DashboardViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewPager()

    }

    private fun initViewPager() {
        viewPagerAdapter = DashboardViewPagerAdapter(supportFragmentManager, getFragmentTitles())
        viewPager.adapter = viewPagerAdapter
        viewPager.offscreenPageLimit = 5
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun getFragmentTitles(): MutableList<String> {
        val fragmentTitles: MutableList<String> = ArrayList()
        fragmentTitles.add(Config.MusicConfig.TRACKS)
        fragmentTitles.add(Config.MusicConfig.ARTISTS)
        fragmentTitles.add(Config.MusicConfig.ALBUMS)
        fragmentTitles.add(Config.MusicConfig.GENRE)
        return fragmentTitles
    }
}
