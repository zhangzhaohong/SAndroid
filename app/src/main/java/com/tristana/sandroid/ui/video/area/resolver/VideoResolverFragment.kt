package com.tristana.sandroid.ui.video.area.resolver

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import butterknife.ButterKnife
import com.blankj.utilcode.util.LogUtils
import com.tristana.library.tools.clipBoard.ClipboardUtil
import com.tristana.sandroid.R

class VideoResolverFragment : Fragment() {

    private var viewModel: VideoResolverViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (viewModel == null) viewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(
                    VideoResolverViewModel::class.java
                )
        val root = inflater.inflate(R.layout.fragment_video_resolver, container, false)
        ButterKnife.bind(this, root)
        return root
    }

}