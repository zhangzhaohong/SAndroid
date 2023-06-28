package com.tristana.sandroid.ui.video.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.tristana.sandroid.R
import com.tristana.sandroid.epoxy.manager.QuickScrollLinearLayoutManager
import com.tristana.sandroid.ui.components.LoadingDialog
import com.tristana.sandroid.ui.video.recommend.controller.VideoRecommendController
import com.tristana.sandroid.ui.video.recommend.listener.EndlessRecyclerOnScrollListener


class VideoRecommendFragment : Fragment() {

    private lateinit var videoRecommendView: EpoxyRecyclerView
    private lateinit var videoRecommendController: VideoRecommendController
    private lateinit var layoutManager: QuickScrollLinearLayoutManager
    private lateinit var epoxyVisibilityTracker: EpoxyVisibilityTracker
    private var revertSpace = 6F
    private var onScrollListener: RecyclerView.OnScrollListener =
        object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore(isSingle: Boolean) {
                videoRecommendViewModel?.loadNext(
                    canLoadMore = true,
                    resolveVidPath = true,
                    continueLoadNext = !isSingle
                )
            }

            override fun onRequestMore() {
                videoRecommendViewModel?.loadMore(true)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val manager = recyclerView.layoutManager as LinearLayoutManager
                val innerHeight = videoRecommendView.measuredHeight
                val firstPosition = manager.findFirstVisibleItemPosition()
                val lastPosition = manager.findLastVisibleItemPosition()
                val firstVisibleView = manager.findViewByPosition(firstPosition)
                val dataSize = videoRecommendViewModel?.videoRecommendDataList?.value?.size ?: 0
                if (newState == SCROLL_STATE_DRAGGING) {
                    if (lastPosition >= dataSize - 4 && lastPosition < dataSize - 3) {
                        onLoadMore(true)
                        videoRecommendViewModel?.let {
                            // 预加载后一批的接口 由于正常情况是从tmp中 拿数据 但是接口速度慢 提前请求
                            if (it.getTmpDataListSize() == 3) {
                                onRequestMore()
                            }
                        }
                    } else if (lastPosition >= dataSize - 3) {
                        onLoadMore(false)
                        videoRecommendViewModel?.let {
                            // 预加载后一批的接口 由于正常情况是从tmp中 拿数据 但是接口速度慢 提前请求
                            if (it.getTmpDataListSize() == 3) {
                                onRequestMore()
                            }
                        }
                    }
                }
                if (newState == SCROLL_STATE_IDLE || newState == SCROLL_STATE_SETTLING) {
                    firstVisibleView?.bottom?.let { firstBottomOffset ->
                        if (getSlidingDirection()) {
                            // up
                            if (firstBottomOffset >= innerHeight - (innerHeight / revertSpace)) {
                                layoutManager.smoothScrollToPosition(
                                    recyclerView,
                                    null,
                                    firstPosition
                                )
                            } else {
                                layoutManager.smoothScrollToPosition(
                                    recyclerView,
                                    null,
                                    lastPosition
                                )
                            }
                        } else {
                            // down
                            if (firstBottomOffset < innerHeight / revertSpace) {
                                layoutManager.smoothScrollToPosition(
                                    recyclerView,
                                    null,
                                    lastPosition
                                )
                            } else {
                                layoutManager.smoothScrollToPosition(
                                    recyclerView,
                                    null,
                                    firstPosition
                                )
                            }
                        }
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
        epoxyVisibilityTracker = EpoxyVisibilityTracker()
        videoRecommendView = root.findViewById(R.id.video_recommend_view)
        epoxyVisibilityTracker.attach(videoRecommendView)
        videoRecommendView.isNestedScrollingEnabled = true
        layoutManager = QuickScrollLinearLayoutManager(
            requireContext(),
            RecyclerView.VERTICAL,
            false,
            20f
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