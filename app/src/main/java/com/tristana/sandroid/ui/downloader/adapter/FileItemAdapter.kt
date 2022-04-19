package com.tristana.sandroid.ui.downloader.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.format.Formatter
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Status
import com.tristana.sandroid.R
import com.tristana.sandroid.ui.downloader.DownloadStateEnums
import com.tristana.sandroid.ui.downloader.adapter.FileItemAdapter.FileItemHolder

/**
 * @author koala
 * @version 1.0
 * @date 2022/4/17 13:08
 * @description
 */
class FileItemAdapter(
    private val context: Context,
) : RecyclerView.Adapter<FileItemHolder>() {
    private var fileInfoList: ArrayList<Download?> = ArrayList()
    private var fileNameTextView: AppCompatTextView? = null
    private var fileTypeTextView: AppCompatTextView? = null
    private var taskStatusTextView: AppCompatTextView? = null
    private var fileSizeTextView: AppCompatTextView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileItemHolder {
        val itemView = View.inflate(parent.context, R.layout.holder_download_task_view, null)
        return FileItemHolder(itemView)
    }

    override fun onBindViewHolder(holder: FileItemHolder, position: Int) {
        holder.setData(fileInfoList[position])
    }

    override fun getItemCount(): Int {
        return fileInfoList.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        fileInfoList[position]?.id?.let {
            return it
        }
        return 0
    }

    override fun getItemId(position: Int): Long {
        fileInfoList[position]?.id?.let {
            return it.toLong()
        }
        return 0
    }

    private fun insertView(entity: Download?) {
        if (this.fileInfoList.isEmpty()) {
            this.fileInfoList.add(entity)
        } else {
            this.fileInfoList.add(0, entity)
        }
        this.notifyItemInserted(0)
        // this.notifyItemRangeInserted(0, 1)
        // this.notifyItemRangeChanged(0, this.fileInfoList.size)
    }

    fun onAddOrUpdate(entity: Download?) {
        this.fileInfoList.forEachIndexed { index, item ->
            if (item?.id == entity?.id && item?.id != null) {
                this.fileInfoList[index] = entity
                this.notifyItemChanged(index)
                // this.notifyItemRangeChanged(index, this.fileInfoList.size - index)
                return
            }
        }
        insertView(entity)
    }

    fun setData(taskList: List<Download>) {
        this.fileInfoList.clear()
        this.fileInfoList.addAll(taskList)
        this.notifyItemRangeInserted(0, this.fileInfoList.size)
    }

    inner class FileItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(downloadEntity: Download?) {
            fileNameTextView!!.text = FileUtils.getFileName(downloadEntity?.file)
            val fileType = FileUtils.getFileExtension(downloadEntity?.file)
            if (fileType != null && fileType.trim() != "") {
                fileTypeTextView!!.text = fileType
                fileTypeTextView!!.visibility = View.VISIBLE
            } else {
                fileTypeTextView!!.visibility = View.GONE
            }
            val status = DownloadStateEnums.getMsgByNum(downloadEntity?.status?.value)
            if (status != null && status.trim { it <= ' ' } != "") {
                taskStatusTextView!!.text = status
                refreshTaskTag(taskStatusTextView, downloadEntity)
            }
            val fileSize = Formatter.formatFileSize(context, downloadEntity?.total ?: 0L)
            val gradientDrawable = fileSizeTextView!!.background as GradientDrawable
            gradientDrawable.setColor(ColorUtils.getColor(R.color.tag_483D8B))
            LogUtils.i(fileSize)
            if (fileSize != null && fileSize != "" && fileSize != "-1 B") {
                fileSizeTextView!!.text = fileSize
                fileSizeTextView!!.visibility = View.VISIBLE
            } else {
                fileSizeTextView!!.visibility = View.GONE
            }
        }

        private fun refreshTaskTag(
            taskStatus: AppCompatTextView?,
            downloadEntity: Download?
        ) {
            val gradientDrawable = taskStatus!!.background as GradientDrawable
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

        init {
            fileNameTextView = itemView.findViewById(R.id.file_name)
            fileTypeTextView = itemView.findViewById(R.id.file_type)
            taskStatusTextView = itemView.findViewById(R.id.task_status)
            fileSizeTextView = itemView.findViewById(R.id.file_size)
        }
    }
}