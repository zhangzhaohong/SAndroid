package com.tristana.sandroid.ui.music.area.operation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import butterknife.ButterKnife
import com.tristana.sandroid.R

class MusicSearchOperationFragment : Fragment() {

    private var viewModel: MusicSearchOperationViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (viewModel == null) viewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(
                    MusicSearchOperationViewModel::class.java
                )
        val root = inflater.inflate(R.layout.fragment_music_search_operation, container, false)
        ButterKnife.bind(this, root)
        return root
    }

}