package com.tristana.sandroid.ui.downloader.holder

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.format.Formatter
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.airbnb.epoxy.*
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.FileUtils
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Status
import com.tristana.sandroid.R
import com.tristana.sandroid.ui.downloader.DownloadStateEnums


/**
 * @author koala
 * @date 2022/4/19 11:40
 * @version 1.0
 * @description
 */
@EpoxyModelClass(layout = R.layout.holder_download_task_view)
abstract class DownloadTaskHolder : EpoxyModelWithHolder<DownloadTaskHolder.Holder>() {

    @EpoxyAttribute
    lateinit var context: Context

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var taskInfo: Download? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        taskInfo?.let { info ->
            holder.fileNameTextView.text = FileUtils.getFileName(info.file)
            val fileType = FileUtils.getFileExtension(info.file)
            if (fileType != null && fileType.trim() != "") {
                holder.fileTypeTextView.text = fileType
                holder.fileTypeTextView.visibility = View.VISIBLE
            } else {
                holder.fileTypeTextView.visibility = View.GONE
            }
            val status = DownloadStateEnums.getMsgByNum(info.status.value)
            if (status != null && status.trim { it <= ' ' } != "") {
                holder.taskStatusTextView.text = status
                refreshTaskTag(holder.taskStatusTextView, taskInfo)
            }
            val fileSize = Formatter.formatFileSize(context, info.total)
            val gradientDrawable = holder.fileSizeTextView.background as GradientDrawable
            gradientDrawable.setColor(ColorUtils.getColor(R.color.tag_483D8B))
            if (fileSize != null && fileSize != "" && fileSize != "-1 B") {
                holder.fileSizeTextView.text = fileSize
                holder.fileSizeTextView.visibility = View.VISIBLE
            } else {
                holder.fileSizeTextView.visibility = View.GONE
            }
        }
    }

    private fun refreshTaskTag(
        taskStatus: AppCompatTextView,
        downloadEntity: Download?
    ) {
        val gradientDrawable = taskStatus.background as GradientDrawable
        when (downloadEntity!!.status) {
            Status.QUEUED -> gradientDrawable.setColor(ColorUtils.getColor(R.color.red_8B636C))
            Status.DOWNLOADING -> gradientDrawable.setColor(ColorUtils.getColor(R.color.blue_008B8B))
            Status.PAUSED -> gradientDrawable.setColor(ColorUtils.getColor(R.color.red_8B475D))
            Status.COMPLETED -> gradientDrawable.setColor(ColorUtils.getColor(R.color.green_006400))
            Status.CANCELLED -> gradientDrawable.setColor(ColorUtils.getColor(R.color.gray_555555))
            Status.FAILED -> gradientDrawable.setColor(Color.RED)
            Status.REMOVED -> gradientDrawable.setColor(ColorUtils.getColor(R.color.gray_555555))
            Status.DELETED -> gradientDrawable.setColor(ColorUtils.getColor(R.color.gray_555555))
            Status.ADDED -> gradientDrawable.setColor(ColorUtils.getColor(R.color.gray_4A708B))
            else -> {
                gradientDrawable.setColor(ColorUtils.getColor(R.color.tag_483D8B))
            }
        }
    }

    class Holder : EpoxyHolder() {

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