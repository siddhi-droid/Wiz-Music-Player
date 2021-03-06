package com.wizmusicplayer

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wizmusicplayer.R.id.recyclerView
import com.wizmusicplayer.ui.MusicViewModel
import kotlinx.android.synthetic.main.fragment_music.*
import kotlinx.android.synthetic.main.fragment_music.view.*
import org.jetbrains.anko.AnkoLogger
import javax.inject.Inject


class MusicFragment : Fragment(), MusicTrackAdapter.TracksInterface, AnkoLogger {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var musicTrackAdapter: MusicTrackAdapter
    private lateinit var artistAdapter: ArtistsAdapter
    private lateinit var albumsAdapter: AlbumsAdapter
    private lateinit var genreAdapter: GenreAdapter
    internal lateinit var view: View
    private lateinit var musicViewModel: MusicViewModel
    private lateinit var musicCallbackInterface: MusicCallbackInterface

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        try {
            musicCallbackInterface = (activity as MusicCallbackInterface?)!!
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

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

    private fun initViewModel() {
        WizApplication.instance.getApplicationComponent().inject(this)
        musicViewModel = ViewModelProviders.of(this, viewModelFactory).get(MusicViewModel::class.java)
    }

    private fun checkArguments() {
        when (arguments?.get(Config.FRAGMENT_TYPE)) {
            Config.MusicConfig.TRACKS -> setUpTracks()
            Config.MusicConfig.ARTISTS -> setUpArtists()
            Config.MusicConfig.ALBUMS -> setUpAlbums()
            Config.MusicConfig.GENRE -> setUpGenres()
        }
    }

    private fun setUpGenres() {
        genreAdapter = GenreAdapter()
        activity?.let {
            view.recyclerView.withLinearLayout(it)
            view.recyclerView.adapter = genreAdapter
            musicViewModel.getAllGenre().observe(this, Observer {
                it?.let {
                    genreAdapter.submitList(it)
                }
            })
        }

        view.sideView.setOnTouchLetterChangeListener({ letter ->
            val position = genreAdapter.getLetterPosition(letter)
            if (position != -1) {
                recyclerView.scrollToPosition(position)
            }
        })
    }

    private fun setUpAlbums() {
        albumsAdapter = AlbumsAdapter()
        activity?.let {
            view.recyclerView.withLinearLayout(it)
            view.recyclerView.adapter = albumsAdapter
            musicViewModel.getAllAlbums().observe(this, Observer {
                it?.let {
                    albumsAdapter.submitList(it)
                }
            })
        }

        view.sideView.setOnTouchLetterChangeListener({ letter ->
            val position = albumsAdapter.getLetterPosition(letter)
            if (position != -1) {
                recyclerView.scrollToPosition(position)
            }
        })
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

            view.sideView.setOnTouchLetterChangeListener({ letter ->
                val position = artistAdapter.getLetterPosition(letter)
                if (position != -1) {
                    recyclerView.scrollToPosition(position)
                }
            })
        }
    }

    private fun setUpTracks() {
        musicTrackAdapter = MusicTrackAdapter(this)
        activity?.let {
            view.recyclerView.withLinearLayout(it)
            view.recyclerView.adapter = musicTrackAdapter
            musicViewModel.getAllTracks().observe(this, Observer {
                it?.let {
                    musicTrackAdapter.submitList(it)
                }
            })


            view.sideView.setOnTouchLetterChangeListener({ letter ->
                val position = musicTrackAdapter.getLetterPosition(letter)
                if (position != -1) {
                    recyclerView.scrollToPosition(position)
                }
            })
        }
    }

    override fun onClick(position: Int, musicList: ArrayList<MusicTrack>) {
        musicCallbackInterface.playTrack(position, musicList)
    }
}