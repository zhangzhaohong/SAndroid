package com.tristana.sandroid.ui.video.recommend.controller

import android.content.Context
import com.airbnb.epoxy.EpoxyController
import com.tristana.sandroid.epoxy.common.CommonFooter
import com.tristana.sandroid.epoxy.common.CommonFooter_
import com.tristana.sandroid.models.video.recommend.AwemeDataModel
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

    var videoRecommendDataList: MutableList<AwemeDataModel>? = null
        set(value) {
            field = value
            requestModelBuild()
        }

    var hasMore: Boolean = false
        set(value) {
            field = value
            requestModelBuild()
        }

    var isFirstLoad: Boolean = true

    override fun buildModels() {
        videoRecommendDataList?.let {dataList ->
            dataList.forEachIndexed { index, item ->
                VideoRecommendHolder_()
                    .context(context)
                    .id(item.awemeId + "-" + index)
                    .item(item)
                    .addTo(this)
            }
            if (!isFirstLoad && dataList.isNotEmpty()) {
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
    }

}