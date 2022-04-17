package com.tristana.sandroid.ui.downloader.adapter

import android.content.Context
import android.graphics.Color
import com.arialyy.aria.core.download.DownloadEntity
import com.arialyy.aria.core.download.DownloadReceiver
import androidx.recyclerview.widget.RecyclerView
import com.tristana.sandroid.ui.downloader.adapter.FileItemAdapter.FileItemHolder
import androidx.appcompat.widget.AppCompatTextView
import android.view.ViewGroup
import com.tristana.sandroid.R
import com.tristana.sandroid.ui.downloader.DownloadStateEnums
import android.graphics.drawable.GradientDrawable
import android.view.View
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author koala
 * @version 1.0
 * @date 2022/4/17 13:08
 * @description
 */
class FileItemAdapter(
    private val context: Context,
    fileInfoList: List<DownloadEntity?>?,
    aria: DownloadReceiver?
) : RecyclerView.Adapter<FileItemHolder>() {
    private val fileInfoList: ArrayList<DownloadEntity?>?
    private val aria: DownloadReceiver?
    private var fileNameTextView: AppCompatTextView? = null
    private var fileTypeTextView: AppCompatTextView? = null
    private var taskStatusTextView: AppCompatTextView? = null
    private var fileSizeTextView: AppCompatTextView? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileItemHolder {
        val itemView = View.inflate(parent.context, R.layout.item_downloader_view, null)
        return FileItemHolder(itemView)
    }

    override fun onBindViewHolder(holder: FileItemHolder, position: Int) {
        holder.setData(fileInfoList!![position])
    }

    override fun getItemCount(): Int {
        return fileInfoList?.size ?: 0
    }

    fun insertView(entity: DownloadEntity?) {
        fileInfoList?.add(0, entity)
        notifyItemRangeInserted(0, 1)
    }

    inner class FileItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(downloadEntity: DownloadEntity?) {
            fileNameTextView!!.text = downloadEntity!!.fileName
            fileTypeTextView!!.text = getFileTypeByDownloadPath(downloadEntity)
            val status = DownloadStateEnums.getMsgByNum(downloadEntity.state)
            if (status != null && status.trim { it <= ' ' } != "") {
                taskStatusTextView!!.text = status
                refreshTaskTag(taskStatusTextView, downloadEntity)
            }
            val fileSize = downloadEntity.convertFileSize
            val gradientDrawable = fileSizeTextView!!.background as GradientDrawable
            gradientDrawable.setColor(context.resources.getColor(R.color.tag_483D8B))
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
                1 -> gradientDrawable.setColor(context.resources.getColor(R.color.green_006400))
                2 -> gradientDrawable.setColor(context.resources.getColor(R.color.yellow_EE7942))
                3 -> gradientDrawable.setColor(context.resources.getColor(R.color.gray_4A708B))
                4 -> gradientDrawable.setColor(context.resources.getColor(R.color.blue_008B8B))
                5 -> gradientDrawable.setColor(context.resources.getColor(R.color.red_8B636C))
                6 -> gradientDrawable.setColor(context.resources.getColor(R.color.red_8B475D))
                7 -> gradientDrawable.setColor(context.resources.getColor(R.color.gray_555555))
                else -> {}
            }
        }

        private fun getFileTypeByDownloadPath(downloadEntity: DownloadEntity?): String {
            val data = downloadEntity!!.filePath.split("\\.").toTypedArray()
            return data[data.size - 1]
        }

        init {
            fileNameTextView = itemView.findViewById(R.id.file_name)
            fileTypeTextView = itemView.findViewById(R.id.file_type)
            taskStatusTextView = itemView.findViewById(R.id.task_status)
            fileSizeTextView = itemView.findViewById(R.id.file_size)
        }
    }

    init {
        fileInfoList?.reversed()
        this.fileInfoList = fileInfoList as ArrayList<DownloadEntity?>?
        this.aria = aria
    }
}