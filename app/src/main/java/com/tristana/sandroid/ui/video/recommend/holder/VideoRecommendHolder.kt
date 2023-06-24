package com.tristana.sandroid.ui.video.recommend.holder

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.format.Formatter
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.airbnb.epoxy.*
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ResourceUtils
import com.daimajia.numberprogressbar.NumberProgressBar
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.Status
import com.tristana.sandroid.R
import com.tristana.sandroid.models.video.recommend.AwemeDataModel
import com.tristana.sandroid.ui.downloader.DownloadStateEnums


/**
 * @author koala
 * @date 2022/4/19 11:40
 * @version 1.0
 * @description
 */
@EpoxyModelClass(layout = R.layout.holder_video_recommend_view)
abstract class VideoRecommendHolder : EpoxyModelWithHolder<VideoRecommendHolder.Holder>() {

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

    class Holder : EpoxyHolder() {

        override fun bindView(itemView: View) {

        }
    }
}