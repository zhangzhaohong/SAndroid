package com.tristana.sandroid.ui.video.recommend.holder

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import butterknife.BindView
import com.airbnb.epoxy.*
import com.airbnb.epoxy.VisibilityState.FULL_IMPRESSION_VISIBLE
import com.tristana.library.tools.sharedPreferences.SpUtils
import com.tristana.sandroid.R
import com.tristana.sandroid.dataModel.data.DataModel
import com.tristana.sandroid.epoxy.holder.BaseEpoxyHolder
import com.tristana.sandroid.epoxy.holder.CustomEpoxyModelWithHolder
import com.tristana.sandroid.respModel.video.recommend.AwemeDataModel
import com.tristana.sandroid.ui.video.recommend.cache.PreloadManager
import com.tristana.sandroid.video.view.DebugInfoView
import xyz.doikki.videocontroller.StandardVideoController
import xyz.doikki.videoplayer.controller.ControlWrapper
import xyz.doikki.videoplayer.player.BaseVideoView
import xyz.doikki.videoplayer.player.BaseVideoView.SCREEN_SCALE_CENTER_CROP
import xyz.doikki.videoplayer.player.VideoView


/**
 * @author koala
 * @date 2022/4/19 11:40
 * @version 1.0
 * @description
 */
@EpoxyModelClass(layout = R.layout.holder_video_recommend_view)
abstract class VideoRecommendHolder : CustomEpoxyModelWithHolder<VideoRecommendHolder.Holder>() {

    @EpoxyAttribute
    lateinit var context: Context

    @EpoxyAttribute
    lateinit var item: AwemeDataModel

    private var debugInfoView: DebugInfoView? = null

    private var mManager: WindowManager? = null

    private var debugInfoStatus: Boolean = false

    private var onStateChangeListener = object : BaseVideoView.OnStateChangeListener {
        override fun onPlayerStateChanged(playerState: Int) {
            debugInfoView?.onPlayerStateChanged(playerState)
        }

        override fun onPlayStateChanged(playState: Int) {
            debugInfoView?.onPlayStateChanged(
                playState,
                item.video?.playAddr?.width,
                item.video?.playAddr?.height
            )
        }
    }

    override fun getViewType(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun id(): Long {
        return item.viewPosition.toLong()
    }

    override fun onViewDetachedFromWindow(holder: Holder) {
        debugInfoView?.let {
            mManager?.removeView(it)
        }
        super.onViewDetachedFromWindow(holder)
        if (holder.videoPlayer?.isPlaying == true) {
            holder.videoPlayer?.pause()
        }
        holder.videoPlayer?.removeOnStateChangeListener(onStateChangeListener)
    }

    override fun onVisibilityStateChanged(visibilityState: Int, holder: Holder) {
        super.onVisibilityStateChanged(visibilityState, holder)
        if (visibilityState == FULL_IMPRESSION_VISIBLE) {
            if (debugInfoStatus) {
                initDebugView()
            }
            if (holder.videoPlayer?.isPlaying == false) {
                holder.videoPlayer?.addOnStateChangeListener(onStateChangeListener)
                if (holder.videoPlayer?.currentPlayState == BaseVideoView.STATE_PAUSED) {
                    holder.videoPlayer?.resume()
                } else {
                    holder.videoPlayer?.start()
                }
            }
        }
    }

    override fun bind(holder: Holder) {
        super.bind(holder)
        debugInfoStatus =
            SpUtils.get(context, DataModel.ENABLE_VIDEO_TECH_DEBUG_INFO_SP, false) as Boolean
        holder.videoPlayer?.setLooping(true)
        val controller = StandardVideoController(context)
        controller.addDefaultControlComponent("标题", false)
        holder.videoPlayer?.setVideoController(controller) //设置控制器
        holder.videoPlayer?.setScreenScaleType(SCREEN_SCALE_CENTER_CROP)
        val cachePath = PreloadManager.getInstance(context).getPlayUrl(item.videoPath)
        holder.videoPlayer?.setUrl(cachePath)
    }

    private fun initDebugView() {
        debugInfoView = DebugInfoView(context)
        mManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams()
        //  设置悬浮type，不设置会报错
        // params.type = WindowManager.LayoutParams.TYPE_PHONE
        //  设置背景透明
        params.format = PixelFormat.TRANSPARENT;
        //  设置不获取焦点，不设置会导致所有焦点分发不到底层界面
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP;
        params.x = 0;
        params.y = 0;
        mManager!!.addView(debugInfoView, params)
    }

    override fun unbind(holder: Holder) {
        holder.videoPlayer?.release()
        super.unbind(holder)
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + context.hashCode()
        return result
    }

    class Holder : BaseEpoxyHolder() {
        @JvmField
        @BindView(R.id.video_recommend_player)
        var videoPlayer: VideoView? = null
    }
}