package com.tristana.sandroid.ui.downloader.holder

import android.content.Context
import android.view.View
import android.view.ViewParent
import androidx.appcompat.widget.AppCompatTextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.blankj.utilcode.util.FileUtils
import com.tonyodev.fetch2.Download
import com.tristana.sandroid.R

/**
 * @author koala
 * @date 2022/4/19 11:40
 * @version 1.0
 * @description
 */
@EpoxyModelClass(layout = R.layout.holder_download_task_view)
abstract class DownloadTaskHolder: EpoxyModelWithHolder<DownloadTaskHolder.Holder>() {

    @EpoxyAttribute
    lateinit var context: Context

    @EpoxyAttribute
    lateinit var taskInfo: Download

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.fileNameTextView.text = FileUtils.getFileName(taskInfo.file)
    }

    class Holder: EpoxyHolder() {

        lateinit var fileNameTextView: AppCompatTextView
        lateinit var fileTypeTextView: AppCompatTextView
        lateinit var taskStatusTextView: AppCompatTextView
        lateinit var fileSizeTextView: AppCompatTextView

        override fun bindView(itemView: View) {
            fileNameTextView = itemView.findViewById(R.id.file_name)
            fileTypeTextView = itemView.findViewById(R.id.file_type)
            taskStatusTextView = itemView.findViewById(R.id.task_status)
            fileSizeTextView = itemView.findViewById(R.id.file_size)
        }
    }
}