package com.tristana.sandroid.ui.downloader.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.arialyy.aria.core.download.DownloadEntity
import com.arialyy.aria.core.download.DownloadReceiver
import com.blankj.utilcode.util.ColorUtils
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
    fileInfoList: List<DownloadEntity?>?,
) : RecyclerView.Adapter<FileItemHolder>() {
    private val fileInfoList: ArrayList<DownloadEntity?> = ArrayList()
    private var fileNameTextView: AppCompatTextView? = null
    private var fileTypeTextView: AppCompatTextView? = null
    private var taskIdTextView: AppCompatTextView? = null
    private var taskStatusTextView: AppCompatTextView? = null
    private var fileSizeTextView: AppCompatTextView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileItemHolder {
        val itemView = View.inflate(parent.context, R.layout.item_downloader_view, null)
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
            return it.toInt()
        }
        return 0
    }

    override fun getItemId(position: Int): Long {
        fileInfoList[position]?.id?.let {
            return it
        }
        return 0
    }

    fun insertView(entity: DownloadEntity?) {
        if (this.fileInfoList.isEmpty()) {
            this.fileInfoList.add(entity)
        } else {
            this.fileInfoList.add(0, entity)
        }
        this.notifyItemInserted(0)
        // this.notifyItemRangeInserted(0, 1)
        // this.notifyItemRangeChanged(0, this.fileInfoList.size)
    }

    fun onTaskStateUpdate(taskEntity: DownloadEntity?) {
        this.fileInfoList.forEachIndexed { index, item ->
            if (item?.id == taskEntity?.id && item?.id != null) {
                this.fileInfoList[index] = taskEntity
                this.notifyItemChanged(index)
                // this.notifyItemRangeChanged(index, this.fileInfoList.size - index)
                return@forEachIndexed
            }
        }
    }

    inner class FileItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(downloadEntity: DownloadEntity?) {
            fileNameTextView!!.text = downloadEntity!!.fileName
            val fileType = getFileTypeByDownloadPath(downloadEntity)
            if (fileType != null && fileType.trim() != "") {
                fileTypeTextView!!.text = getFileTypeByDownloadPath(downloadEntity)
                fileTypeTextView!!.visibility = View.VISIBLE
            } else {
                fileTypeTextView!!.visibility = View.GONE
            }
            val id = "# ${downloadEntity.id}"
            taskIdTextView!!.text = id
            val status = DownloadStateEnums.getMsgByNum(downloadEntity.state)
            if (status != null && status.trim { it <= ' ' } != "") {
                taskStatusTextView!!.text = status
                refreshTaskTag(taskStatusTextView, downloadEntity)
            }
            val fileSize = downloadEntity.convertFileSize
            val gradientDrawable = fileSizeTextView!!.background as GradientDrawable
            gradientDrawable.setColor(ColorUtils.getColor(R.color.tag_483D8B))
            if (fileSize != null && fileSize != "") {
                fileSizeTextView!!.text = fileSize
                fileSizeTextView!!.visibility = View.VISIBLE
            } else {
                fileSizeTextView!!.visibility = View.GONE
            }
        }

        private fun refreshTaskTag(
            taskStatus: AppCompatTextView?,
            downloadEntity: DownloadEntity?
        ) {
            val gradientDrawable = taskStatus!!.background as GradientDrawable
            when (downloadEntity!!.state) {
                0 -> gradientDrawable.setColor(Color.RED)
                1 -> gradientDrawable.setColor(ColorUtils.getColor(R.color.green_006400))
                2 -> gradientDrawable.setColor(ColorUtils.getColor(R.color.yellow_EE7942))
                3 -> gradientDrawable.setColor(ColorUtils.getColor(R.color.gray_4A708B))
                4 -> gradientDrawable.setColor(ColorUtils.getColor(R.color.blue_008B8B))
                5 -> gradientDrawable.setColor(ColorUtils.getColor(R.color.red_8B636C))
                6 -> gradientDrawable.setColor(ColorUtils.getColor(R.color.red_8B475D))
                7 -> gradientDrawable.setColor(ColorUtils.getColor(R.color.gray_555555))
                else -> {}
            }
        }

        private fun getFileTypeByDownloadPath(downloadEntity: DownloadEntity?): String? {
            downloadEntity?.let {
                val data = downloadEntity.filePath.split(".").toTypedArray()
                return data[data.size - 1]
            } ?: kotlin.run {
                return null
            }
        }

        init {
            fileNameTextView = itemView.findViewById(R.id.file_name)
            fileTypeTextView = itemView.findViewById(R.id.file_type)
            taskIdTextView = itemView.findViewById(R.id.task_id)
            taskStatusTextView = itemView.findViewById(R.id.task_status)
            fileSizeTextView = itemView.findViewById(R.id.file_size)
        }
    }

    init {
        fileInfoList?.forEach {
            insertView(it)
        }
    }
}