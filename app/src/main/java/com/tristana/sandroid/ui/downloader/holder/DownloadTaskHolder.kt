package com.tristana.sandroid.ui.downloader.holder

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
    lateinit var taskInfo: Download

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var fetch: Fetch

    override fun getViewType(): Int {
        return taskInfo.id
    }

    override fun equals(other: Any?): Boolean {
        if (taskInfo.etaInMilliSeconds <= 0 && !checkEquals(taskInfo)) {
            return true
        }
        return false
    }

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.fileNameTextView.text = FileUtils.getFileName(taskInfo.file)
        val fileType = FileUtils.getFileExtension(taskInfo.file)
        if (fileType != null && fileType.trim() != "") {
            holder.fileTypeTextView.text = fileType
            holder.fileTypeTextView.visibility = View.VISIBLE
        } else {
            holder.fileTypeTextView.visibility = View.GONE
        }
        val status = DownloadStateEnums.getMsgByNum(taskInfo.status.value)
        if (status != null && status.trim { it <= ' ' } != "") {
            holder.taskStatusTextView.text = status
            refreshTaskTag(holder.taskStatusTextView, taskInfo)
        }
        val fileSize = Formatter.formatFileSize(context, taskInfo.total)
        val gradientDrawable = holder.fileSizeTextView.background as GradientDrawable
        gradientDrawable.setColor(ColorUtils.getColor(R.color.tag_483D8B))
        if (fileSize != null && fileSize != "" && fileSize != "-1 B") {
            holder.fileSizeTextView.text = fileSize
            holder.fileSizeTextView.visibility = View.VISIBLE
        } else {
            holder.fileSizeTextView.visibility = View.GONE
        }
        if (checkDownloadProgress(taskInfo)) {
            holder.downloadProgressBar.visibility = View.VISIBLE
            holder.downloadProgressBar.progress = taskInfo.progress
        } else {
            holder.downloadProgressBar.visibility = View.GONE
        }
        if (showResumeButton(taskInfo)) {
            holder.operationButtonImageView.visibility = View.VISIBLE
            holder.operationButtonImageView.background = ResourceUtils.getDrawable(R.drawable.ic_start)
            holder.operationButtonImageView.setOnClickListener {
                fetch.resume(taskInfo.id)
            }
        } else if (showStopButton(taskInfo)) {
            holder.operationButtonImageView.visibility = View.VISIBLE
            holder.operationButtonImageView.background = ResourceUtils.getDrawable(R.drawable.ic_stop)
            holder.operationButtonImageView.setOnClickListener {
                fetch.pause(taskInfo.id)
            }
        } else if (showRetryButton(taskInfo)) {
            holder.operationButtonImageView.visibility = View.VISIBLE
            holder.operationButtonImageView.background = ResourceUtils.getDrawable(R.drawable.ic_retry)
            holder.operationButtonImageView.setOnClickListener {
                fetch.retry(taskInfo.id)
                holder.operationButtonImageView.visibility = View.GONE
            }
        } else {
            holder.operationButtonImageView.visibility = View.GONE
            holder.operationButtonImageView.setOnClickListener(null)
        }
        if (showCancel(taskInfo)) {
            holder.cancelButtonImageView.visibility = View.VISIBLE
        } else {
            holder.cancelButtonImageView.visibility = View.GONE
        }
        holder.cancelButtonImageView.setOnClickListener {
            fetch.cancel(taskInfo.id)
        }
    }

    private fun showRetryButton(downloadEntity: Download): Boolean {
        return when(downloadEntity.status) {
            Status.QUEUED -> false
            Status.DOWNLOADING -> false
            Status.PAUSED -> false
            Status.COMPLETED -> false
            Status.CANCELLED -> true
            Status.FAILED -> true
            Status.REMOVED -> false
            Status.DELETED -> false
            Status.ADDED -> false
            else -> {
                false
            }
        }
    }

    private fun showCancel(downloadEntity: Download): Boolean {
        return when(downloadEntity.status) {
            Status.QUEUED -> false
            Status.DOWNLOADING -> true
            Status.PAUSED -> true
            Status.COMPLETED -> false
            Status.CANCELLED -> false
            Status.FAILED -> false
            Status.REMOVED -> false
            Status.DELETED -> false
            Status.ADDED -> true
            else -> {
                false
            }
        }
    }

    private fun checkDownloadProgress(downloadEntity: Download): Boolean {
        return when(downloadEntity.status) {
            Status.QUEUED -> false
            Status.DOWNLOADING -> true
            Status.PAUSED -> true
            Status.COMPLETED -> false
            Status.CANCELLED -> false
            Status.FAILED -> false
            Status.REMOVED -> false
            Status.DELETED -> false
            Status.ADDED -> false
            else -> {
                false
            }
        }
    }

    private fun checkEquals(downloadEntity: Download): Boolean {
        return when(downloadEntity.status) {
            Status.QUEUED -> false
            Status.DOWNLOADING -> true
            Status.PAUSED -> true
            Status.COMPLETED -> false
            Status.CANCELLED -> false
            Status.FAILED -> false
            Status.REMOVED -> false
            Status.DELETED -> false
            Status.ADDED -> false
            else -> {
                false
            }
        }
    }

    private fun showResumeButton(downloadEntity: Download): Boolean {
        return when(downloadEntity.status) {
            Status.QUEUED -> false
            Status.DOWNLOADING -> false
            Status.PAUSED -> true
            Status.COMPLETED -> false
            Status.CANCELLED -> false
            Status.FAILED -> false
            Status.REMOVED -> false
            Status.DELETED -> false
            Status.ADDED -> false
            else -> {
                false
            }
        }
    }

    private fun showStopButton(downloadEntity: Download): Boolean {
        return when(downloadEntity.status) {
            Status.QUEUED -> false
            Status.DOWNLOADING -> true
            Status.PAUSED -> false
            Status.COMPLETED -> false
            Status.CANCELLED -> false
            Status.FAILED -> false
            Status.REMOVED -> false
            Status.DELETED -> false
            Status.ADDED -> false
            else -> {
                false
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

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + context.hashCode()
        return result
    }

    class Holder : EpoxyHolder() {

        lateinit var fileNameTextView: AppCompatTextView
        lateinit var fileTypeTextView: AppCompatTextView
        lateinit var taskStatusTextView: AppCompatTextView
        lateinit var fileSizeTextView: AppCompatTextView
        lateinit var downloadProgressBar: NumberProgressBar
        lateinit var operationButtonImageView: AppCompatImageView
        lateinit var cancelButtonImageView: AppCompatImageView

        override fun bindView(itemView: View) {
            fileNameTextView = itemView.findViewById(R.id.file_name)
            fileTypeTextView = itemView.findViewById(R.id.file_type)
            taskStatusTextView = itemView.findViewById(R.id.task_status)
            fileSizeTextView = itemView.findViewById(R.id.file_size)
            downloadProgressBar = itemView.findViewById(R.id.download_progress_bar)
            operationButtonImageView = itemView.findViewById(R.id.operation_button)
            cancelButtonImageView = itemView.findViewById(R.id.cancel_button)
        }
    }
}