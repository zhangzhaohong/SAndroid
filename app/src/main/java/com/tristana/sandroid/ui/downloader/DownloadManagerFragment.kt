package com.tristana.sandroid.ui.downloader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arialyy.annotations.Download
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.task.DownloadTask
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.hl.downloader.DownloadListener
import com.hl.downloader.DownloadManager
import com.tristana.customViewWithToolsLibrary.tools.http.HttpUtils
import com.tristana.sandroid.R
import com.tristana.sandroid.ui.downloader.adapter.FileItemAdapter
import com.tristana.sandroid.ui.downloader.listener.EndlessRecyclerOnScrollListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.ceil


open class DownloadManagerFragment : Fragment() {
    private var downloadManagerViewModel: DownloadManagerViewModel? = null
    private lateinit var fileItemAdapter: FileItemAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (downloadManagerViewModel == null) downloadManagerViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(
                    DownloadManagerViewModel::class.java
                )
        val root = inflater.inflate(R.layout.fragment_download_manager, container, false)
        val textView = root.findViewById<TextView>(R.id.text_download_manager)
        val testDownloader1 = root.findViewById<AppCompatButton>(R.id.test_downloader_1)
        val testDownloader2 = root.findViewById<AppCompatButton>(R.id.test_downloader_2)
        val downloaderTaskView = root.findViewById<RecyclerView>(R.id.downloader_task_view)
        Aria.download(this).register()
        val pageSize = 10
        val pageNum = Aria.download(this).taskList?.size?.let { size ->
            ceil((size.toFloat() / pageSize.toFloat()).toDouble()).toInt().let {
                if (it < 1) {
                    1
                } else {
                    it
                }
            }
        } ?: kotlin.run {
            1
        };
        fileItemAdapter =
            FileItemAdapter(
                requireContext(),
                Aria.download(this).getTaskList(
                    pageNum,
                    pageSize
                )
            )
        val layoutManager = LinearLayoutManager(context)
        downloaderTaskView.adapter = fileItemAdapter
        downloaderTaskView.layoutManager = layoutManager
        downloaderTaskView.addOnScrollListener(object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore() {
                LogUtils.i("onLoadMore")
            }
        })
        downloadManagerViewModel!!.text.observe(viewLifecycleOwner) { s -> textView.text = s }
        testDownloader1.setOnClickListener {
            var filePath = this.context?.getExternalFilesDir("download")?.absolutePath
            FileUtils.createOrExistsDir(filePath)
            val downloadUrl =
                "http://192.168.2.70:8080/tools/DouYin/player/video?vid=v0200fg10000c8t94c3c77u933tk63m0&ratio=540p&isDownload=1"
            // "https://developer.lanzoug.com/file/?BmBTbVloBTQCCwM7AzZUOAQ7UGgACQI4VXxQZlIgBjcCLFM1CWRTNQVgCgNXZwZiADhVJQc/C1ZQFABuXWQEZgZtU01ZagUOAmYDZQNmVG0EblBiAG8CNVUNUHhSbwZ3AmlTIgkyU24FPwo5V1wGbgA+VW0HbAs5UGYANF05BDcGP1MiWWIFIgJpA2wDblRjBGdQZwBmAjBVdFAmUn4GOgIwUzQJZVM/BXwKbFc0BigAalVmB3cLOlBmAGVdNAQyBmVTN1k2BTcCMQNmAzJUMQRrUDQAPQI3VWZQZFI+BjQCN1NgCWdTNAVmCmpXNAY+AGJVZgdoCydQNgB2XWoEIwZzU3dZYQUjAj0DMQNqVGMEb1BlAGoCM1VqUHBSegZuAm9TYQkyUzoFYgpqVzYGNwBrVWEHaAsxUG4AMl0nBGMGalNzWTkFYAJiA2ADZ1RkBG5QbABmAjVVZlBwUnsGdwJ1UzkJZVMyBWAKbFc7Bj4AaFVmB28LPVBxAHNdaAR1BjtTMlkwBX8CZgNlA2VUewRsUGcAawIuVWNQZlI+BiECZlNoCWlTNw=="
            // "https://dev-081.baidupan.com/622e908803e44f19673750430e3a649f/1650102340/2018/07/06/31c57c32fe3ed5ab742035d335679860.apk?filename=V8.0.0.1023_debug_CheckIn_20180705_.apk"
            MainScope().launch {
                withContext(Dispatchers.IO) {
                    filePath = "$filePath/"
                    HttpUtils().getFileNameFromUrlByOkHttp3(downloadUrl, null, null)?.let {
                        filePath += it
                    } ?: kotlin.run {
                        filePath += System.currentTimeMillis()
                    }
                    LogUtils.i(filePath)
                }
                withContext(Dispatchers.Main) {
                    DownloadManager.startDownLoad(
                        requireActivity().application,
                        downloadUrl,
                        saveFilePath = filePath,
                        downloadListener = object : DownloadListener() {
                            override fun downloadIng(progress: String) {
                                textView.text = "下载中$progress%"
                            }

                            override fun downloadError(error: Throwable?) {
                                textView.text = "下载出错:${error?.message}"
                            }

                            override fun downloadComplete(downLoadFilePath: String) {
                                textView.text = "下载完成--->$downLoadFilePath"
                            }

                            override fun downloadPause() {
                                textView.text = "下载暂停"
                            }

                            override fun downloadCancel() {
                                textView.text = "下载取消"
                            }
                        }
                    )
                }
            }
        }
        testDownloader2.setOnClickListener {
            var filePath = this.context?.getExternalFilesDir("download")?.absolutePath
            FileUtils.createOrExistsDir(filePath)
            val downloadUrl =
                "http://192.168.2.70:8080/tools/DouYin/player/video?vid=v0200fg10000c8t94c3c77u933tk63m0&ratio=540p&isDownload=1"
            // "https://developer.lanzoug.com/file/?BmBTbVloBTQCCwM7AzZUOAQ7UGgACQI4VXxQZlIgBjcCLFM1CWRTNQVgCgNXZwZiADhVJQc/C1ZQFABuXWQEZgZtU01ZagUOAmYDZQNmVG0EblBiAG8CNVUNUHhSbwZ3AmlTIgkyU24FPwo5V1wGbgA+VW0HbAs5UGYANF05BDcGP1MiWWIFIgJpA2wDblRjBGdQZwBmAjBVdFAmUn4GOgIwUzQJZVM/BXwKbFc0BigAalVmB3cLOlBmAGVdNAQyBmVTN1k2BTcCMQNmAzJUMQRrUDQAPQI3VWZQZFI+BjQCN1NgCWdTNAVmCmpXNAY+AGJVZgdoCydQNgB2XWoEIwZzU3dZYQUjAj0DMQNqVGMEb1BlAGoCM1VqUHBSegZuAm9TYQkyUzoFYgpqVzYGNwBrVWEHaAsxUG4AMl0nBGMGalNzWTkFYAJiA2ADZ1RkBG5QbABmAjVVZlBwUnsGdwJ1UzkJZVMyBWAKbFc7Bj4AaFVmB28LPVBxAHNdaAR1BjtTMlkwBX8CZgNlA2VUewRsUGcAawIuVWNQZlI+BiECZlNoCWlTNw=="
            // "https://dev-081.baidupan.com/622e908803e44f19673750430e3a649f/1650102340/2018/07/06/31c57c32fe3ed5ab742035d335679860.apk?filename=V8.0.0.1023_debug_CheckIn_20180705_.apk"
            MainScope().launch {
                withContext(Dispatchers.IO) {
                    filePath = "$filePath/"
                    HttpUtils().getFileNameFromUrlByOkHttp3(downloadUrl, null, null)?.let {
                        filePath += it
                    } ?: kotlin.run {
                        filePath += System.currentTimeMillis()
                    }
                    LogUtils.i(filePath)
                }
                withContext(Dispatchers.Main) {
                    val taskId: Long = Aria.download(this)
                        .load(downloadUrl) //读取下载地址
                        .setFilePath(filePath) //设置文件保存的完整路径
                        .create() //启动下载
                    LogUtils.i("currentTaskId: $taskId")
                }
            }
        }
        return root
    }

    @Download.onWait
    open fun onWait(task: DownloadTask) {
        fileItemAdapter.onAddOrUpdate(task.downloadEntity)
    }

    @Download.onPre
    protected open fun onPre(task: DownloadTask?) {
        fileItemAdapter.onAddOrUpdate(task?.downloadEntity)
    }

    @Download.onTaskStart
    open fun taskStart(task: DownloadTask?) {
        fileItemAdapter.onAddOrUpdate(task?.downloadEntity)
    }

    @Download.onTaskRunning
    protected open fun running(task: DownloadTask?) {
        fileItemAdapter.onAddOrUpdate(task?.downloadEntity)
    }

    @Download.onTaskResume
    open fun taskResume(task: DownloadTask?) {
        fileItemAdapter.onAddOrUpdate(task?.downloadEntity)
    }

    @Download.onTaskStop
    open fun taskStop(task: DownloadTask?) {
        fileItemAdapter.onAddOrUpdate(task?.downloadEntity)
    }

    @Download.onTaskCancel
    open fun taskCancel(task: DownloadTask?) {
        fileItemAdapter.onAddOrUpdate(task?.downloadEntity)
    }

    @Download.onTaskFail
    open fun taskFail(task: DownloadTask?) {
        fileItemAdapter.onAddOrUpdate(task?.downloadEntity)
    }

    @Download.onTaskComplete
    open fun taskComplete(task: DownloadTask) {
        LogUtils.d("path ==> " + task.downloadEntity.filePath)
        LogUtils.d("id ==> " + task.downloadEntity.id)
        fileItemAdapter.onAddOrUpdate(task.downloadEntity)
    }

}