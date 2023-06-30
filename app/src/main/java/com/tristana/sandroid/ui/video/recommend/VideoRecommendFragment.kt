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
import com.blankj.utilcode.util.ObjectUtils
import com.tristana.sandroid.R
import com.tristana.sandroid.epoxy.manager.QuickScrollLinearLayoutManager
import com.tristana.sandroid.ui.components.LoadingDialog
import com.tristana.sandroid.ui.video.recommend.cache.PreloadManager
import com.tristana.sandroid.ui.video.recommend.controller.VideoRecommendController
import com.tristana.sandroid.ui.video.recommend.listener.EndlessRecyclerOnScrollListener
import xyz.doikki.videoplayer.player.BaseVideoView.STATE_PAUSED
import xyz.doikki.videoplayer.player.VideoView
import java.text.FieldPosition

class VideoRecommendFragment : Fragment() {

    private lateinit var videoRecommendView: EpoxyRecyclerView
    private lateinit var videoRecommendController: VideoRecommendController
    private lateinit var layoutManager: QuickScrollLinearLayoutManager
    private lateinit var epoxyVisibilityTracker: EpoxyVisibilityTracker
    private lateinit var mPreloadManager: PreloadManager
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
                    if (lastPosition >= dataSize - 3 && lastPosition < dataSize - 2) {
                        onLoadMore(true)
                        videoRecommendViewModel?.let {
                            // 预加载后一批的接口 由于正常情况是从tmp中 拿数据 但是接口速度慢 提前请求
                            if (it.getTmpDataListSize() == 2) {
                                onRequestMore()
                            }
                        }
                    } else if (lastPosition >= dataSize - 2) {
                        onLoadMore(false)
                        videoRecommendViewModel?.let {
                            // 预加载后一批的接口 由于正常情况是从tmp中 拿数据 但是接口速度慢 提前请求
                            if (it.getTmpDataListSize() == 2) {
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
                                    recyclerView, null, firstPosition
                                )
                            } else {
                                layoutManager.smoothScrollToPosition(
                                    recyclerView, null, lastPosition
                                )
                                preloadManager(newState, lastPosition, getSlidingDirection())
                            }
                        } else {
                            // down
                            if (firstBottomOffset < innerHeight / revertSpace) {
                                layoutManager.smoothScrollToPosition(
                                    recyclerView, null, lastPosition
                                )
                            } else {
                                layoutManager.smoothScrollToPosition(
                                    recyclerView, null, firstPosition
                                )
                                preloadManager(newState, firstPosition, getSlidingDirection())
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

    override fun onResume() {
        super.onResume()
        if (videoRecommendViewModel?.isFirstLoad?.value == true && videoRecommendViewModel?.getTmpDataListSize() == 0) {
            loadingDialog.show()
        }
        videoRecommendView.post {
            if (ObjectUtils.isNotEmpty(lastPosition) && lastPosition >= 0) {
                val itemView = layoutManager.getChildAt(lastPosition) ?: return@post
                val videoView =
                    itemView.findViewById<VideoView>(R.id.video_recommend_player)
                        ?: return@post
                if (videoView.currentPlayState == STATE_PAUSED) {
                    videoView.resume()
                } else {
                    videoView.start()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        for (index in 0 until layoutManager.childCount) {
            val itemView = layoutManager.getChildAt(index) ?: continue
            val videoView =
                itemView.findViewById<VideoView>(R.id.video_recommend_player) ?: continue
            if (videoView.isPlaying) {
                lastPosition = index
                videoView.pause()
                return
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (videoRecommendViewModel == null) videoRecommendViewModel =
            AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(VideoRecommendViewModel::class.java)
        mPreloadManager = PreloadManager.getInstance(requireContext())
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
            requireContext(), RecyclerView.VERTICAL, false, 20f
        )
        videoRecommendView.layoutManager = layoutManager
        videoRecommendView.setHasFixedSize(true)
        layoutManager.stackFromEnd = false
        videoRecommendController = VideoRecommendController(requireContext())
        videoRecommendView.setControllerAndBuildModels(videoRecommendController)
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

    private fun preloadManager(state: Int, position: Int, slidingDirection: Boolean) {
        if (state == SCROLL_STATE_IDLE) {
            mPreloadManager.resumePreload(position, !slidingDirection);
        } else {
            mPreloadManager.pausePreload(position, !slidingDirection);
        }
    }
}