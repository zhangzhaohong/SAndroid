package com.tristana.sandroid.ui.video.recommend.holder

import android.content.Context
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatImageView
import butterknife.BindView
import com.airbnb.epoxy.*
import com.airbnb.epoxy.VisibilityState.FULL_IMPRESSION_VISIBLE
import com.android.iplayer.listener.OnPlayerEventListener
import com.android.iplayer.model.PlayerState
import com.android.iplayer.widget.VideoPlayer
import com.android.iplayer.widget.WidgetFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.tristana.sandroid.R
import com.tristana.sandroid.epoxy.holder.BaseEpoxyHolder
import com.tristana.sandroid.epoxy.holder.CustomEpoxyModelWithHolder
import com.tristana.sandroid.respModel.video.recommend.AwemeDataModel
import com.tristana.sandroid.ui.video.recommend.cache.PreloadManager


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

    private var mManager: WindowManager? = null

    private var onPlayerEventListener: OnPlayerEventListener? = null

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
        onPlayerEventListener?.let {
            onPlayerEventListener = null
        }
    }

    override fun onVisibilityStateChanged(visibilityState: Int, holder: Holder) {
        super.onVisibilityStateChanged(visibilityState, holder)
        if (visibilityState == FULL_IMPRESSION_VISIBLE) {
            if (holder.videoPlayer?.isPlaying == false) {
                onPlayerEventListener = getOnStateChangeListener(holder)
                onPlayerEventListener?.let {
                    holder.videoPlayer?.setOnPlayerActionListener(it)
                }
            }
        }
    }

    private fun getOnStateChangeListener(holder: Holder): OnPlayerEventListener {
        val isFirstLoad = true
        return object : OnPlayerEventListener() {
            override fun onPlayerState(state: PlayerState?, message: String?) {
                super.onPlayerState(state, message)
                if (state == PlayerState.STATE_RESET || state == PlayerState.STATE_PREPARE || state == PlayerState.STATE_BUFFER) {
                    holder.thumbView?.visibility = View.VISIBLE
                    holder.videoPlayer?.visibility = View.GONE
                } else {
                    holder.thumbView?.visibility = View.GONE
                    holder.videoPlayer?.visibility = View.VISIBLE
                }
                if (isFirstLoad && (state == PlayerState.STATE_ON_PAUSE || state == PlayerState.STATE_PAUSE)) {
                    holder.videoPlayer?.onResume()
                }
            }
        }
    }

    override fun bind(holder: Holder) {
        super.bind(holder)
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
        holder.videoPlayer?.layoutParams?.height =
            context.resources.displayMetrics.widthPixels * 16 / 9 //固定播放器高度，或高度设置为:match_parent
        val controller = holder.videoPlayer?.initController()
        WidgetFactory.bindDefaultControls(controller);
        holder.videoPlayer?.controller = controller;
        controller?.setTitle(item.desc?.let { desc ->
            desc.ifEmpty {
                item.author?.nickname?.let { it + "的作品" } ?: "无标题"
            }
        } ?: kotlin.run {
            item.author?.nickname?.let { it + "的作品" } ?: "无标题"
        }) //视频标题(仅横屏状态可见)
        holder.videoPlayer?.setLoop(true)
        //设置播放源
        val cachePath = PreloadManager.getInstance(context).getPlayUrl(item.videoPath)
        holder.videoPlayer?.setDataSource(cachePath)
        //异步开始准备播放
        holder.videoPlayer?.prepareAsync()
    }

    override fun unbind(holder: Holder) {
        holder.videoPlayer?.onRelease()
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
        var videoPlayer: VideoPlayer? = null

        @JvmField
        @BindView(R.id.video_recommend_cover_view)
        var thumbView: AppCompatImageView? = null
    }
}