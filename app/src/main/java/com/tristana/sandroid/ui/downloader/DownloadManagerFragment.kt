package com.tristana.sandroid.ui.downloader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.hl.downloader.DownloadListener
import com.hl.downloader.DownloadManager
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.Fetch.Impl.getInstance
import com.tonyodev.fetch2.FetchConfiguration
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2core.DownloadBlock
import com.tonyodev.fetch2core.Downloader.FileDownloaderType
import com.tonyodev.fetch2okhttp.OkHttpDownloader
import com.tristana.customViewWithToolsLibrary.tools.http.HttpUtils
import com.tristana.sandroid.R
import com.tristana.sandroid.downloader.utils.RequestObjectUtils
import com.tristana.sandroid.ui.downloader.adapter.FileItemAdapter
import com.tristana.sandroid.ui.downloader.listener.EndlessRecyclerOnScrollListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


open class DownloadManagerFragment : Fragment() {
    private val namespace = "DownloadManagerFragment"
    private val groupId = "public".hashCode()
    private var downloadManagerViewModel: DownloadManagerViewModel? = null
    private lateinit var fileItemAdapter: FileItemAdapter
    private var fetch: Fetch? = null
    private lateinit var fetchListener: FetchListener

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
        val testDownloader3 = root.findViewById<AppCompatButton>(R.id.test_downloader_3)
        val downloaderTaskView = root.findViewById<RecyclerView>(R.id.downloader_task_view)
        fileItemAdapter =
            FileItemAdapter(
                requireContext()
            )
        val layoutManager = GridLayoutManager(context, 1)
        downloaderTaskView.adapter = fileItemAdapter
        downloaderTaskView.layoutManager = layoutManager
        // init
        val fetchConfiguration: FetchConfiguration = FetchConfiguration.Builder(requireContext())
            .setDownloadConcurrentLimit(3)
            .setHttpDownloader(OkHttpDownloader(FileDownloaderType.PARALLEL))
            .setNamespace(namespace)
            .build()
        fetch = getInstance(fetchConfiguration)
        fetchListener = object : FetchListener {
            override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
                // fileItemAdapter.onAddOrUpdate(download)
            }

            override fun onCompleted(download: Download) {
                fileItemAdapter.onAddOrUpdate(download)
            }

            override fun onProgress(
                download: Download,
                etaInMilliSeconds: Long,
                downloadedBytesPerSecond: Long
            ) {
                // fileItemAdapter.onAddOrUpdate(download)
            }

            override fun onPaused(download: Download) {
                fileItemAdapter.onAddOrUpdate(download)
            }

            override fun onResumed(download: Download) {
                fileItemAdapter.onAddOrUpdate(download)
            }

            override fun onStarted(
                download: Download,
                downloadBlocks: List<DownloadBlock>,
                totalBlocks: Int
            ) {
                fileItemAdapter.onAddOrUpdate(download)
            }

            override fun onWaitingNetwork(download: Download) {
                // fileItemAdapter.onAddOrUpdate(download)
            }

            override fun onAdded(download: Download) {
                // fileItemAdapter.onAddOrUpdate(download)
            }

            override fun onCancelled(download: Download) {
                fileItemAdapter.onAddOrUpdate(download)
            }

            override fun onRemoved(download: Download) {
                // fileItemAdapter.onAddOrUpdate(download)
            }

            override fun onDeleted(download: Download) {
                // fileItemAdapter.onAddOrUpdate(download)
            }

            override fun onDownloadBlockUpdated(
                download: Download,
                downloadBlock: DownloadBlock,
                totalBlocks: Int
            ) {
                // fileItemAdapter.onAddOrUpdate(download)
            }

            override fun onError(download: Download, error: Error, throwable: Throwable?) {
                fileItemAdapter.onAddOrUpdate(download)
            }
        }
        fetch?.addListener(fetchListener)
        fetch?.getDownloadsInGroup(groupId) { taskList ->
            run {
                fileItemAdapter.setData(taskList)
            }
        }
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
                "http://speedtest.ftp.otenet.gr/files/test100Mb.db"
            // "http://192.168.2.70:8080/tools/DouYin/player/video?vid=v0200fg10000c8t94c3c77u933tk63m0&ratio=540p&isDownload=1"
            // "https://developer.lanzoug.com/file/?BmBTbVloBTQCCwM7AzZUOAQ7UGgACQI4VXxQZlIgBjcCLFM1CWRTNQVgCgNXZwZiADhVJQc/C1ZQFABuXWQEZgZtU01ZagUOAmYDZQNmVG0EblBiAG8CNVUNUHhSbwZ3AmlTIgkyU24FPwo5V1wGbgA+VW0HbAs5UGYANF05BDcGP1MiWWIFIgJpA2wDblRjBGdQZwBmAjBVdFAmUn4GOgIwUzQJZVM/BXwKbFc0BigAalVmB3cLOlBmAGVdNAQyBmVTN1k2BTcCMQNmAzJUMQRrUDQAPQI3VWZQZFI+BjQCN1NgCWdTNAVmCmpXNAY+AGJVZgdoCydQNgB2XWoEIwZzU3dZYQUjAj0DMQNqVGMEb1BlAGoCM1VqUHBSegZuAm9TYQkyUzoFYgpqVzYGNwBrVWEHaAsxUG4AMl0nBGMGalNzWTkFYAJiA2ADZ1RkBG5QbABmAjVVZlBwUnsGdwJ1UzkJZVMyBWAKbFc7Bj4AaFVmB28LPVBxAHNdaAR1BjtTMlkwBX8CZgNlA2VUewRsUGcAawIuVWNQZlI+BiECZlNoCWlTNw=="
            // "https://dev-081.baidupan.com/622e908803e44f19673750430e3a649f/1650102340/2018/07/06/31c57c32fe3ed5ab742035d335679860.apk?filename=V8.0.0.1023_debug_CheckIn_20180705_.apk"
            MainScope().launch {
                withContext(Dispatchers.IO) {
                    filePath = "$filePath/"
                    HttpUtils().getFileNameFromUrlByOkHttp3(downloadUrl, null, null)?.let {
                        filePath += it
                    } ?: kotlin.run {
                        val path = downloadUrl.split("/")
                        val fileName = path[path.size - 1].split(".")
                        filePath += System.currentTimeMillis()
                            .toString() + "." + fileName[fileName.size - 1]
                    }
                    LogUtils.i(filePath)
                }
                withContext(Dispatchers.Main) {
                    RequestObjectUtils.getFetchRequests(
                        requireContext(),
                        downloadUrl,
                        filePath,
                        groupId
                    ).let {
                        fetch?.enqueue(it)
                    }
                }
            }
        }
        testDownloader3.setOnClickListener {
            var filePath = this.context?.getExternalFilesDir("download")?.absolutePath
            FileUtils.createOrExistsDir(filePath)
            val downloadUrl =
                "http://192.168.2.70:8080/tools/DouYin/player/video?vid=v0200fg10000c8t94c3c77u933tk63m0&ratio=540p&isDownload=1"
            //    "http://speedtest.ftp.otenet.gr/files/test100Mb.db"
            // "https://developer.lanzoug.com/file/?BmBTbVloBTQCCwM7AzZUOAQ7UGgACQI4VXxQZlIgBjcCLFM1CWRTNQVgCgNXZwZiADhVJQc/C1ZQFABuXWQEZgZtU01ZagUOAmYDZQNmVG0EblBiAG8CNVUNUHhSbwZ3AmlTIgkyU24FPwo5V1wGbgA+VW0HbAs5UGYANF05BDcGP1MiWWIFIgJpA2wDblRjBGdQZwBmAjBVdFAmUn4GOgIwUzQJZVM/BXwKbFc0BigAalVmB3cLOlBmAGVdNAQyBmVTN1k2BTcCMQNmAzJUMQRrUDQAPQI3VWZQZFI+BjQCN1NgCWdTNAVmCmpXNAY+AGJVZgdoCydQNgB2XWoEIwZzU3dZYQUjAj0DMQNqVGMEb1BlAGoCM1VqUHBSegZuAm9TYQkyUzoFYgpqVzYGNwBrVWEHaAsxUG4AMl0nBGMGalNzWTkFYAJiA2ADZ1RkBG5QbABmAjVVZlBwUnsGdwJ1UzkJZVMyBWAKbFc7Bj4AaFVmB28LPVBxAHNdaAR1BjtTMlkwBX8CZgNlA2VUewRsUGcAawIuVWNQZlI+BiECZlNoCWlTNw=="
            // "https://dev-081.baidupan.com/622e908803e44f19673750430e3a649f/1650102340/2018/07/06/31c57c32fe3ed5ab742035d335679860.apk?filename=V8.0.0.1023_debug_CheckIn_20180705_.apk"
            MainScope().launch {
                withContext(Dispatchers.IO) {
                    filePath = "$filePath/"
                    HttpUtils().getFileNameFromUrlByOkHttp3(downloadUrl, null, null)?.let {
                        filePath += it
                    } ?: kotlin.run {
                        val path = downloadUrl.split("/")
                        val fileName = path[path.size - 1].split(".")
                        filePath += System.currentTimeMillis()
                            .toString() + fileName[fileName.size - 1]
                    }
                    LogUtils.i(filePath)
                }
                withContext(Dispatchers.Main) {
                    RequestObjectUtils.getFetchRequests(
                        requireContext(),
                        downloadUrl,
                        filePath,
                        groupId
                    ).let {
                        fetch?.enqueue(it)
                    }
                }
            }
        }
        return root
    }

    override fun onDestroyView() {
        fetch?.removeListener(fetchListener)
        super.onDestroyView()
    }

}