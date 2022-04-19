package com.tristana.sandroid.ui.downloader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.hl.downloader.DownloadListener
import com.hl.downloader.DownloadManager
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2.Fetch.Impl.getInstance
import com.tonyodev.fetch2core.DownloadBlock
import com.tonyodev.fetch2core.Downloader.FileDownloaderType
import com.tonyodev.fetch2okhttp.OkHttpDownloader
import com.tristana.customViewWithToolsLibrary.tools.http.HttpUtils
import com.tristana.sandroid.R
import com.tristana.sandroid.downloader.utils.RequestObjectUtils
import com.tristana.sandroid.ui.downloader.controller.DownloadTaskListController
import com.tristana.sandroid.ui.downloader.manager.QuickScrollLinearLayoutManager
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


open class DownloadManagerFragment : Fragment() {
    private val namespace = "DownloadManagerFragment"
    private val groupId = "public".hashCode()
    private val mutex = Mutex()
    private var fetch: Fetch? = null
    private var fetchListener: FetchListener = getFetchListener()

    private fun getFetchListener(): FetchListener {
        return object : FetchListener {
            override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
                downloadManagerViewModel?.addOrUpdate(download)
            }

            override fun onCompleted(download: Download) {
                downloadManagerViewModel?.addOrUpdate(download)
            }

            override fun onProgress(
                download: Download,
                etaInMilliSeconds: Long,
                downloadedBytesPerSecond: Long
            ) {
                downloadManagerViewModel?.addOrUpdate(download)
            }

            override fun onPaused(download: Download) {
                downloadManagerViewModel?.addOrUpdate(download)
            }

            override fun onResumed(download: Download) {
                downloadManagerViewModel?.addOrUpdate(download)
            }

            override fun onStarted(
                download: Download,
                downloadBlocks: List<DownloadBlock>,
                totalBlocks: Int
            ) {
                downloadManagerViewModel?.addOrUpdate(download)
            }

            override fun onWaitingNetwork(download: Download) {
                downloadManagerViewModel?.addOrUpdate(download)
            }

            override fun onAdded(download: Download) {
                downloadManagerViewModel?.addOrUpdate(download)
            }

            override fun onCancelled(download: Download) {
                downloadManagerViewModel?.addOrUpdate(download)
            }

            override fun onRemoved(download: Download) {
                downloadManagerViewModel?.addOrUpdate(download)
            }

            override fun onDeleted(download: Download) {
                downloadManagerViewModel?.addOrUpdate(download)
            }

            override fun onDownloadBlockUpdated(
                download: Download,
                downloadBlock: DownloadBlock,
                totalBlocks: Int
            ) {
                // downloadManagerViewModel?.addOrUpdate(download)
            }

            override fun onError(download: Download, error: Error, throwable: Throwable?) {
                downloadManagerViewModel?.addOrUpdate(download)
                LogUtils.e(throwable)
            }
        }
    }

    private lateinit var downloadTaskListController: DownloadTaskListController
    private lateinit var layoutManager: QuickScrollLinearLayoutManager
    private var downloadManagerViewModel: DownloadManagerViewModel? = null

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
        val downloaderTaskView = root.findViewById<EpoxyRecyclerView>(R.id.downloader_task_view)
        layoutManager = QuickScrollLinearLayoutManager(
            requireContext(),
            RecyclerView.VERTICAL,
            false
        )
        downloaderTaskView.layoutManager = layoutManager
        layoutManager.stackFromEnd = false
        downloadTaskListController = DownloadTaskListController(requireContext())
        downloaderTaskView.setController(downloadTaskListController)
        initObserver()
        // init
        val fetchConfiguration: FetchConfiguration = FetchConfiguration.Builder(requireContext())
            .setDownloadConcurrentLimit(3)
            .setHttpDownloader(OkHttpDownloader(FileDownloaderType.PARALLEL))
            .setNamespace(namespace)
            .enableAutoStart(true)
            .build()
        fetch = getInstance(fetchConfiguration)
        fetch?.addListener(fetchListener)
        MainScope().launch {
            downloadManagerViewModel?.getData(fetch, groupId)
        }
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

    private fun initObserver() {
        downloadManagerViewModel!!.fileInfoList.observe(viewLifecycleOwner) {
            downloadTaskListController.fileInfoList = it
//            MainScope().launch {
//                refreshRecyclerView(it)
//            }
        }
    }

    private suspend fun refreshRecyclerView(data: MutableList<Download>) {
        mutex.withLock {
            delay(1000)
            downloadTaskListController.fileInfoList = data
            LogUtils.i("on Refresh recyclerview")
        }
    }

    override fun onDestroyView() {
        fetch?.removeListener(fetchListener)
        super.onDestroyView()
    }

}