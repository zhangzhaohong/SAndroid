package com.tristana.sandroid.ui.video.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.tristana.sandroid.R
import com.tristana.sandroid.ui.downloader.controller.DownloadTaskListController
import com.tristana.sandroid.ui.downloader.controller.VideoRecommendController
import com.tristana.sandroid.ui.downloader.manager.QuickScrollLinearLayoutManager

class VideoRecommendFragment : Fragment() {

    private lateinit var videoRecommendController: VideoRecommendController
    private lateinit var layoutManager: QuickScrollLinearLayoutManager

    private var galleryViewModel: VideoRecommendViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (galleryViewModel == null) galleryViewModel =
            AndroidViewModelFactory.getInstance(requireActivity().application)
                .create<VideoRecommendViewModel>(
                    VideoRecommendViewModel::class.java
                )
        val root = inflater.inflate(R.layout.fragment_video_recommend, container, false)
        val videoRecommendView = root.findViewById<EpoxyRecyclerView>(R.id.video_recommend_view)
        layoutManager = QuickScrollLinearLayoutManager(
            requireContext(),
            RecyclerView.VERTICAL,
            false
        )
        videoRecommendView.layoutManager = layoutManager
        return root
    }
}