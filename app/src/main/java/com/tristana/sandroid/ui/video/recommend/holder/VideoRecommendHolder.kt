package com.tristana.sandroid.ui.video.recommend.holder

import android.content.Context
import butterknife.BindView
import com.airbnb.epoxy.*
import com.tristana.sandroid.R
import com.tristana.sandroid.epoxy.holder.BaseEpoxyHolder
import com.tristana.sandroid.epoxy.holder.CustomEpoxyModelWithHolder
import com.tristana.sandroid.respModel.video.recommend.AwemeDataModel
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

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var item: AwemeDataModel

    override fun getViewType(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun bind(holder: Holder) {
        super.bind(holder)
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + context.hashCode()
        return result
    }

    class Holder : BaseEpoxyHolder() {
        @BindView(R.id.video_recommend_player)
        var videoPlayer: VideoView? = null
    }
}