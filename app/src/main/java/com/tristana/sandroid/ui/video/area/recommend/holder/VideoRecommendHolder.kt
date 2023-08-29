package com.tristana.sandroid.ui.video.area.recommend.holder

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import butterknife.BindView
import com.airbnb.epoxy.*
import com.android.iplayer.base.AbstractMediaPlayer
import com.android.iplayer.listener.OnPlayerEventListener
import com.android.iplayer.media.IMediaPlayer
import com.android.iplayer.media.core.ExoPlayerFactory
import com.android.iplayer.media.core.IjkPlayerFactory
import com.android.iplayer.model.PlayerState
import com.android.iplayer.widget.VideoPlayer
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
import com.tristana.sandroid.ui.video.area.recommend.cache.PreloadManager
import com.tristana.sandroid.ui.video.area.recommend.factory.WidgetFactory


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

    private var isFirstRender = true

    override fun getViewType(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun id(): Long {
        return item.viewPosition.toLong()
    }

    override fun onVisibilityChanged(
        percentVisibleHeight: Float,
        percentVisibleWidth: Float,
        visibleHeight: Int,
        visibleWidth: Int,
        holder: Holder
    ) {
        super.onVisibilityChanged(
            percentVisibleHeight,
            percentVisibleWidth,
            visibleHeight,
            visibleWidth,
            holder
        )
        if (percentVisibleHeight > 0 && percentVisibleHeight < 100) {
            if (isFirstRender) {
                if (holder.videoPlayer?.isPlaying == false) {
                    holder.thumbView?.visibility = View.VISIBLE
                    holder.videoPlayer?.visibility = View.GONE
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun bind(holder: Holder) {
        super.bind(holder)
        item.author?.nickname?.let {
            holder.authorView?.text = "@$it"
        }
        item.desc?.let {
            holder.descView?.text = it
        }
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
        holder.videoPlayer?.onReset()
        holder.videoPlayer?.setZoomModel(IMediaPlayer.MODE_ZOOM_CROPPING)
        val controller = holder.videoPlayer?.initController()
        WidgetFactory.bindDefaultControls(controller)
        holder.videoPlayer?.controller = controller
        controller?.setTitle(item.desc?.let { desc ->
            desc.ifEmpty {
                item.author?.nickname?.let { it + "的作品" } ?: "无标题"
            }
        } ?: kotlin.run {
            item.author?.nickname?.let { it + "的作品" } ?: "无标题"
        }) //视频标题(仅横屏状态可见)
        holder.videoPlayer?.setLoop(true)
        holder.videoPlayer?.setOnPlayerActionListener(object : OnPlayerEventListener() {
            override fun createMediaPlayer(): AbstractMediaPlayer {
                return when (SpUtils.get(context, DataModel.VIDEO_TECH_SP, 0) as Int) {
                    0, 1 -> {
                        IjkPlayerFactory.create().createPlayer(context);
                    }

                    2 -> {
                        ExoPlayerFactory.create().createPlayer(context);
                    }

                    else -> {
                        super.createMediaPlayer()
                    }
                }
            }

            override fun onPlayerState(state: PlayerState?, message: String?) {
                super.onPlayerState(state, message)
                when (state) {
                    PlayerState.STATE_PREPARE, PlayerState.STATE_BUFFER -> {
                        if (isFirstRender) {
                            holder.thumbView?.visibility = View.VISIBLE
                            holder.videoPlayer?.visibility = View.GONE
                        }
                    }

                    PlayerState.STATE_START, PlayerState.STATE_PLAY, PlayerState.STATE_ON_PLAY -> {
                        isFirstRender = false
                        holder.thumbView?.visibility = View.GONE
                        holder.videoPlayer?.visibility = View.VISIBLE
                    }

                    PlayerState.STATE_MOBILE -> {}
                    PlayerState.STATE_ON_PAUSE, PlayerState.STATE_PAUSE -> {}
                    PlayerState.STATE_RESET, PlayerState.STATE_STOP, PlayerState.STATE_DESTROY -> {}
                    PlayerState.STATE_COMPLETION -> {}
                    PlayerState.STATE_ERROR -> {}
                    else -> {}
                }
            }
        })
        //设置播放源
        val cachePath = PreloadManager.getInstance(context).getPlayUrl(item.videoPath)
        holder.videoPlayer?.setDataSource(cachePath)
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

        @JvmField
        @BindView(R.id.video_recommend_author)
        var authorView: AppCompatTextView? = null

        @JvmField
        @BindView(R.id.video_recommend_desc)
        var descView: AppCompatTextView? = null
    }
}