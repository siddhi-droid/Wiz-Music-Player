package com.wizmusicplayer

 import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class DashboardViewPagerAdapter(fragmentManager: FragmentManager,
                                private val fragmentTitles: MutableList<String>) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return MusicFragment.newInstance(fragmentTitles[position])
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitles[position]
    }

    override fun getCount(): Int {
        return fragmentTitles.size
    }
}