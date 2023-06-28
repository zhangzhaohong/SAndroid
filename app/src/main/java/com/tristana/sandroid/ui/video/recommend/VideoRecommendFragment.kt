package com.tristana.sandroid.ui.video.recommend

import android.app.Activity
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
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ObjectUtils
import com.blankj.utilcode.util.Utils
import com.tristana.sandroid.R
import com.tristana.sandroid.epoxy.manager.QuickScrollLinearLayoutManager
import com.tristana.sandroid.ui.components.LoadingDialog
import com.tristana.sandroid.ui.video.recommend.controller.VideoRecommendController
import com.tristana.sandroid.ui.video.recommend.listener.EndlessRecyclerOnScrollListener
import xyz.doikki.videoplayer.player.VideoView


class VideoRecommendFragment : Fragment() {

    private lateinit var videoRecommendView: EpoxyRecyclerView
    private lateinit var videoRecommendController: VideoRecommendController
    private lateinit var layoutManager: QuickScrollLinearLayoutManager
    private lateinit var epoxyVisibilityTracker: EpoxyVisibilityTracker
    private var lastPosition = -1
    private var revertSpace = 6F
    private var onScrollListener: RecyclerView.OnScrollListener =
        object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore(isSingle: Boolean) {
                videoRecommendViewModel?.loadNext(
                    canLoadMore = true,
                    resolveVidPath = true,
                    continueLoadNext = !isSingle,
                    context = requireContext()
                )
            }

            override fun onRequestMore() {
                videoRecommendViewModel?.loadMore(true, requireContext())
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
    private var videoRecommendFragmentAppStatusChangeListener = object : Utils.OnAppStatusChangedListener {
        override fun onForeground(activity: Activity?) {
            lastPosition = layoutManager.findLastVisibleItemPosition()
            if (ObjectUtils.isNotEmpty(lastPosition) && lastPosition > 0) {
                val itemView = videoRecommendView.getChildAt(lastPosition)
                if (ObjectUtils.isEmpty(itemView)) return
                val videoView = itemView.findViewById<VideoView>(R.id.video_recommend_player)
                videoView.start()
            }
        }

        override fun onBackground(activity: Activity?) {
            for (index in 0 until videoRecommendView.childCount) {
                val itemView = videoRecommendView.getChildAt(index)
                if (ObjectUtils.isEmpty(itemView)) continue
                val videoView = itemView.findViewById<VideoView>(R.id.video_recommend_player)
                if (videoView.isPlaying) {
                    lastPosition = index
                    videoView.pause()
                }
                return
            }
        }
    }

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
        videoRecommendViewModel?.loadNext(
            canLoadMore = true,
            resolveVidPath = true,
            continueLoadNext = true,
            context = requireContext()
        )
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
        AppUtils.registerAppStatusChangedListener(videoRecommendFragmentAppStatusChangeListener)
        return root
    }

    override fun onDestroyView() {
        AppUtils.unregisterAppStatusChangedListener(videoRecommendFragmentAppStatusChangeListener)
        super.onDestroyView()
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