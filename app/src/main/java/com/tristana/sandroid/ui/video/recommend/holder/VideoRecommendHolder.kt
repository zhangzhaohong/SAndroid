package com.tristana.sandroid.ui.video.recommend.holder

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.doOnDetach
import butterknife.BindView
import com.airbnb.epoxy.*
import com.airbnb.epoxy.VisibilityState.FULL_IMPRESSION_VISIBLE
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.tristana.library.tools.sharedPreferences.SpUtils
import com.tristana.sandroid.R
import com.tristana.sandroid.dataModel.data.DataModel
import com.tristana.sandroid.epoxy.holder.BaseEpoxyHolder
import com.tristana.sandroid.epoxy.holder.CustomEpoxyModelWithHolder
import com.tristana.sandroid.respModel.video.recommend.AwemeDataModel
import com.tristana.sandroid.ui.video.recommend.cache.PreloadManager
import com.tristana.sandroid.video.view.DebugInfoView
import xyz.doikki.videocontroller.StandardVideoController
import xyz.doikki.videoplayer.player.BaseVideoView
import xyz.doikki.videoplayer.player.BaseVideoView.OnStateChangeListener
import xyz.doikki.videoplayer.player.BaseVideoView.SCREEN_SCALE_CENTER_CROP
import xyz.doikki.videoplayer.player.BaseVideoView.STATE_PREPARED
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

    private var onStateChangeListener: OnStateChangeListener? = null

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
        super.onViewDetachedFromWindow(holder)
        if (holder.videoPlayer?.isPlaying == true) {
            holder.videoPlayer?.pause()
        }
        onStateChangeListener?.let {
            holder.videoPlayer?.removeOnStateChangeListener(it)
        }
        debugInfoView?.let {
            mManager?.removeViewImmediate(it)
        }
    }

    override fun onVisibilityStateChanged(visibilityState: Int, holder: Holder) {
        super.onVisibilityStateChanged(visibilityState, holder)
        if (visibilityState == FULL_IMPRESSION_VISIBLE) {
            if (debugInfoStatus) {
                initDebugView()
            }
            if (holder.videoPlayer?.isPlaying == false) {
                onStateChangeListener = getOnStateChangeListener(holder)
                onStateChangeListener?.let {
                    holder.videoPlayer?.addOnStateChangeListener(it)
                }
                if (holder.videoPlayer?.currentPlayState == BaseVideoView.STATE_PAUSED) {
                    holder.videoPlayer?.resume()
                } else {
                    holder.videoPlayer?.start()
                }
            }
        }
    }

    private fun getOnStateChangeListener(holder: Holder): OnStateChangeListener {
        return object : OnStateChangeListener {
            override fun onPlayerStateChanged(playerState: Int) {
                debugInfoView?.onPlayerStateChanged(playerState)
            }

            override fun onPlayStateChanged(playState: Int) {
                debugInfoView?.onPlayStateChanged(
                    playState,
                    item.video?.playAddr?.width,
                    item.video?.playAddr?.height
                )
                if (playState > STATE_PREPARED) {
                    holder.thumbView?.visibility = View.GONE
                    holder.videoPlayer?.visibility = View.VISIBLE
                } else {
                    holder.thumbView?.visibility = View.VISIBLE
                    holder.videoPlayer?.visibility = View.GONE
                }
            }
        }
    }

    override fun bind(holder: Holder) {
        super.bind(holder)
        debugInfoStatus =
            SpUtils.get(context, DataModel.ENABLE_VIDEO_TECH_DEBUG_INFO_SP, false) as Boolean
        if (item.video?.cover?.urlList?.isNotEmpty() == true) {
            holder.thumbView?.let {
                val options = RequestOptions()
                    .centerCrop()
                    // .placeholder(R.drawable.ic_picture_loading) //预加载图片
                    // .error(R.drawable.ic_picture_load_failed) //加载失败图片
                    .priority(Priority.HIGH) //优先级
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC) //缓存
                Glide.with(it.context)
                    .load(item.video!!.cover!!.urlList[0]) // .transform(new GlideRoundTransform(48))
                    .apply(options)
                    .into(it)
            }
        }
        holder.videoPlayer?.setLooping(true)
        val controller = StandardVideoController(context)
        controller.addDefaultControlComponent(
            item.desc?.let { desc ->
                desc.ifEmpty {
                    item.author?.nickname?.let { it + "的作品" } ?: "无标题"
                }
            } ?: kotlin.run { item.author?.nickname?.let { it + "的作品" } ?: "无标题" }, false
        )
        holder.videoPlayer?.setVideoController(controller) //设置控制器
        holder.videoPlayer?.setScreenScaleType(SCREEN_SCALE_CENTER_CROP)
        val cachePath = PreloadManager.getInstance(context).getPlayUrl(item.videoPath)
        holder.videoPlayer?.setUrl(cachePath)
        holder.videoPlayer?.setOnClickListener {
            if (holder.videoPlayer?.currentPlayState == BaseVideoView.STATE_PAUSED) {
                holder.videoPlayer?.resume()
            } else {
                holder.videoPlayer?.start()
            }
        }
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

        @JvmField
        @BindView(R.id.video_recommend_cover_view)
        var thumbView: AppCompatImageView? = null
    }
}