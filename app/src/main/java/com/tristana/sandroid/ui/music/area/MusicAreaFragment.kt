package com.tristana.sandroid.ui.music.area

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tristana.sandroid.R

class MusicAreaFragment : Fragment() {
    private var viewModel: MusicAreaViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (viewModel == null) viewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(
                    MusicAreaViewModel::class.java
                )
        return inflater.inflate(R.layout.fragment_music_area, container, false)
    }
}