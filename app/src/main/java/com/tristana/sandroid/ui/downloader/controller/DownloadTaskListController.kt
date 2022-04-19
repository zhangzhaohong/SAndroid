package com.tristana.sandroid.ui.downloader.controller

import android.content.Context
import com.airbnb.epoxy.EpoxyController
import com.tonyodev.fetch2.Download
import com.tristana.sandroid.ui.downloader.common.CommonFooter
import com.tristana.sandroid.ui.downloader.common.CommonFooter_
import com.tristana.sandroid.ui.downloader.common.commonFooter
import com.tristana.sandroid.ui.downloader.holder.DownloadTaskHolder_
import com.tristana.sandroid.ui.downloader.holder.downloadTaskHolder

/**
 * @author koala
 * @version 1.0
 * @date 2022/4/19 11:08
 * @description
 */
class DownloadTaskListController(
    private val context: Context,
    private val bottomPadding: Float = CommonFooter.NORMAL_PADDING
) : EpoxyController() {

    var fileInfoList: MutableList<Download>? = null
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        fileInfoList?.forEach { item ->
            DownloadTaskHolder_()
                .context(context)
                .taskInfo(item)
                .id(item.id)
                .addTo(this)
        }
        CommonFooter_()
            .hasMore(false)
            .bottomPadding(bottomPadding)
            .id("footer")
            .addTo(this)
    }

}