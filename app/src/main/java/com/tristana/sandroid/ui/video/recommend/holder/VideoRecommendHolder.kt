package com.tristana.sandroid.ui.video.recommend.holder

import android.content.Context
import butterknife.BindView
import com.airbnb.epoxy.*
import com.airbnb.epoxy.VisibilityState.FULL_IMPRESSION_VISIBLE
import com.blankj.utilcode.util.LogUtils
import com.tristana.sandroid.R
import com.tristana.sandroid.epoxy.holder.BaseEpoxyHolder
import com.tristana.sandroid.epoxy.holder.CustomEpoxyModelWithHolder
import com.tristana.sandroid.respModel.video.recommend.AwemeDataModel
import com.tristana.sandroid.ui.video.recommend.cache.PreloadManager
import xyz.doikki.videocontroller.StandardVideoController
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory
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

    override fun getViewType(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun onVisibilityStateChanged(visibilityState: Int, holder: Holder) {
        super.onVisibilityStateChanged(visibilityState, holder)
        if (visibilityState == FULL_IMPRESSION_VISIBLE) {
            holder.videoPlayer?.start()
        } else {
            holder.videoPlayer?.release()
        }
    }

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.videoPlayer?.setLooping(true)
        holder.videoPlayer?.setPlayerFactory(IjkPlayerFactory.create())
        val controller = StandardVideoController(context)
        controller.addDefaultControlComponent("标题", false)
        holder.videoPlayer?.setVideoController(controller) //设置控制器
        holder.videoPlayer?.setScreenScaleType(SCREEN_SCALE_CENTER_CROP)
        val cachePath = PreloadManager.getInstance(context).getPlayUrl(item.videoPath)
        holder.videoPlayer?.setUrl(cachePath)
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