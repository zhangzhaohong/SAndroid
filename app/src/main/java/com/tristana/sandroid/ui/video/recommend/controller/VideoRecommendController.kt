package com.tristana.sandroid.ui.downloader.controller

import android.content.Context
import com.airbnb.epoxy.EpoxyController
import com.tonyodev.fetch2.Download
import com.tristana.sandroid.common.CommonFooter
import com.tristana.sandroid.common.CommonFooter_
import com.tristana.sandroid.ui.video.recommend.holder.VideoRecommendHolder_

/**
 * @author koala
 * @version 1.0
 * @date 2022/4/19 11:08
 * @description
 */
class VideoRecommendController(
    private val context: Context,
    private val bottomPadding: Float = CommonFooter.NORMAL_PADDING
) : EpoxyController() {

    var fileInfoList: MutableList<Download>? = null
        set(value) {
            field = value
            requestModelBuild()
        }

    var hasMore: Boolean = false
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        fileInfoList?.forEach { item ->
            VideoRecommendHolder_()
                .context(context)
                .id(item.id)
                .addTo(this)
        }
        CommonFooter_()
            .hasMore(hasMore)
            .bottomPadding(bottomPadding)
            .id(
                "footer-" + if (hasMore) {
                    "more"
                } else {
                    "none"
                }
            )
            .addTo(this)
    }

}