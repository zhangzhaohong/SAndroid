package com.tristana.sandroid.ui.music.area.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import butterknife.ButterKnife
import com.tristana.sandroid.R

class MusicRecommendFragment : Fragment() {

    private var viewModel: MusicRecommendViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (viewModel == null) viewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(
                    MusicRecommendViewModel::class.java
                )
        val root = inflater.inflate(R.layout.fragment_music_recommend, container, false)
        ButterKnife.bind(this, root)
        return root
    }

}