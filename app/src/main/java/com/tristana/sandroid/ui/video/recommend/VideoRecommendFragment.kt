package com.tristana.sandroid.ui.video.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.blankj.utilcode.util.LogUtils
import com.tristana.sandroid.R
import com.tristana.sandroid.epoxy.manager.QuickScrollLinearLayoutManager
import com.tristana.sandroid.ui.components.LoadingDialog
import com.tristana.sandroid.ui.video.recommend.controller.VideoRecommendController
import com.tristana.sandroid.ui.video.recommend.listener.EndlessRecyclerOnScrollListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


class VideoRecommendFragment : Fragment() {

    private lateinit var videoRecommendView: EpoxyRecyclerView
    private lateinit var videoRecommendController: VideoRecommendController
    private lateinit var layoutManager: QuickScrollLinearLayoutManager
    private var firstVisibleItemPosition = -1
    private var lastVisibleItemPosition = -1
    private var scrollDirection: Boolean = false
    private var onScrollListener: RecyclerView.OnScrollListener =
        object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore() {
                videoRecommendViewModel?.loadNext(true)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val manager = recyclerView.layoutManager as LinearLayoutManager
                val firstPosition = manager.findFirstVisibleItemPosition()
                val lastPosition = manager.findLastVisibleItemPosition()
                scrollDirection = this.getSlidingDirection()
                if (scrollDirection) {
                    // up
                    val dataSize = videoRecommendViewModel?.videoRecommendDataList?.value?.size
                        ?: kotlin.run { 0 }
                    if (lastPosition < dataSize && lastPosition != lastVisibleItemPosition) {
                        layoutManager.smoothScrollToPosition(recyclerView, null, lastPosition)
                        lastVisibleItemPosition = lastPosition
                        firstVisibleItemPosition = -1
                    }
                } else {
                    // down
                    if (firstPosition != firstVisibleItemPosition) {
                        layoutManager.smoothScrollToPosition(recyclerView, null, firstPosition)
                        firstVisibleItemPosition = firstPosition
                        lastVisibleItemPosition = -1
                    }
                }
            }
        }
    private var videoRecommendViewModel: VideoRecommendViewModel? = null

    private val loadingDialog by lazy {
        LoadingDialog(requireContext(), getString(R.string.is_loading), false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        loadingDialog.show()
        if (videoRecommendViewModel == null) videoRecommendViewModel =
            AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(VideoRecommendViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_video_recommend, container, false)
        videoRecommendView = root.findViewById(R.id.video_recommend_view)
        layoutManager = QuickScrollLinearLayoutManager(
            requireContext(),
            RecyclerView.VERTICAL,
            false,
            50f
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
        videoRecommendViewModel!!.isFirstLoad.observe(viewLifecycleOwner) {
            videoRecommendController.isFirstLoad = it
            if (!it) {
                loadingDialog.dismiss()
            }
        }
    }
}