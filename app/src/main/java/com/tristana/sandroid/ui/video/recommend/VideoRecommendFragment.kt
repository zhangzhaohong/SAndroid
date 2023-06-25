package com.tristana.sandroid.ui.video.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.blankj.utilcode.util.LogUtils
import com.tristana.sandroid.R
import com.tristana.sandroid.ui.video.recommend.controller.VideoRecommendController
import com.tristana.sandroid.epoxy.manager.QuickScrollLinearLayoutManager
import com.tristana.sandroid.ui.video.recommend.listener.EndlessRecyclerOnScrollListener

class VideoRecommendFragment : Fragment() {

    private lateinit var videoRecommendController: VideoRecommendController
    private lateinit var layoutManager: QuickScrollLinearLayoutManager
    private var onScrollListener: RecyclerView.OnScrollListener =
        object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore() {
                videoRecommendViewModel?.loadNext(true)
            }
        }
    private var videoRecommendViewModel: VideoRecommendViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (videoRecommendViewModel == null) videoRecommendViewModel =
            AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(VideoRecommendViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_video_recommend, container, false)
        val videoRecommendView = root.findViewById<EpoxyRecyclerView>(R.id.video_recommend_view)
        layoutManager = QuickScrollLinearLayoutManager(
            requireContext(),
            RecyclerView.VERTICAL,
            false
        )
        videoRecommendView.layoutManager = layoutManager
        videoRecommendView.setHasFixedSize(true)
        layoutManager.stackFromEnd = false
        videoRecommendController = VideoRecommendController(requireContext())
        videoRecommendView.setController(videoRecommendController)
        videoRecommendView.addOnScrollListener(onScrollListener)
        initObserver()
        return root
    }

    private fun initObserver() {
        videoRecommendViewModel!!.videoRecommendDataList.observe(viewLifecycleOwner) {
            videoRecommendController.videoRecommendDataList = it
        }
        videoRecommendViewModel!!.hasMore.observe(viewLifecycleOwner) {
            videoRecommendController.hasMore = it
        }
    }
}