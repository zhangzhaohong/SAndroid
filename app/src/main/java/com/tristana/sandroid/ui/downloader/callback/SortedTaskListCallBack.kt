package com.tristana.sandroid.ui.downloader.callback

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedListAdapterCallback
import com.arialyy.aria.core.download.DownloadEntity

/**
 * @author koala
 * @date 2022/4/18 10:28
 * @version 1.0
 * @description
 */
class SortedTaskListCallBack(adapter: RecyclerView.Adapter<*>) :
    SortedListAdapterCallback<DownloadEntity>(adapter) {

    override fun compare(o1: DownloadEntity, o2: DownloadEntity): Int = let {
        // 从大到小
        o2.id.compareTo(o1.id)
        // 从小到大
        // o1.id.compareTo(o2.id)
    }

    override fun areContentsTheSame(oldItem: DownloadEntity, newItem: DownloadEntity): Boolean =
        oldItem.hashCode() == newItem.hashCode()

    override fun areItemsTheSame(item1: DownloadEntity, item2: DownloadEntity): Boolean =
        item1.id == item2.id
}