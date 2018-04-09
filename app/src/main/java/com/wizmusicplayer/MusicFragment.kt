package com.wizmusicplayer

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wizmusicplayer.ui.MusicViewModel
import kotlinx.android.synthetic.main.fragment_music.view.*
import org.jetbrains.anko.AnkoLogger
import javax.inject.Inject


class MusicFragment : Fragment(), AnkoLogger {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var musicTrackAdapter: MusicTrackAdapter
    lateinit var artistAdapter: ArtistsAdapter
    internal lateinit var view: View
    lateinit var musicViewModel: MusicViewModel

    companion object {
        fun newInstance(fragmentType: String): MusicFragment {
            val fragment = MusicFragment()
            val bundle = Bundle()
            bundle.putString(Config.FRAGMENT_TYPE, fragmentType)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_music, container, false)
        initViewModel()
        checkArguments()
        return view
    }

    fun initViewModel() {
        WizApplication.instance.getApplicationComponent().inject(this)
        musicViewModel = ViewModelProviders.of(this, viewModelFactory).get(MusicViewModel::class.java)
    }

    fun checkArguments() {
        when (arguments?.get(Config.FRAGMENT_TYPE)) {
            Config.MusicConfig.TRACKS -> setUpTracks()
            Config.MusicConfig.ARTISTS -> setUpArtists()
            Config.MusicConfig.ALBUMS -> setUpAlbums()
            Config.MusicConfig.GENRE -> setUpGenres()
        }
    }

    private fun setUpGenres() {
    }

    private fun setUpAlbums() {
    }

    private fun setUpArtists() {
        artistAdapter = ArtistsAdapter()
        activity?.let {
            view.recyclerView.withGridLayout2X2(it)
            view.recyclerView.adapter = artistAdapter
            musicViewModel.getAllArtists().observe(this, Observer {
                it?.let {
                    artistAdapter.submitList(it)
                }
            })
        }
    }

    fun setUpTracks() {
        musicTrackAdapter = MusicTrackAdapter()
        activity?.let {
            view.recyclerView.withLinearLayout(it)
            view.recyclerView.adapter = musicTrackAdapter
            musicViewModel.getAllTracks().observe(this, Observer {
                it?.let {
                    musicTrackAdapter.submitList(it)
                }
            })
        }
    }
}