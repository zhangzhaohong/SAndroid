package com.tristana.sandroid.ui.downloader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import com.tristana.customViewWithToolsLibrary.tools.http.HttpUtils
import com.tristana.sandroid.MyApplication
import com.tristana.sandroid.R
import com.tristana.sandroid.downloader.utils.RequestObjectUtils
import com.tristana.sandroid.ui.downloader.controller.DownloadTaskListController
import com.tristana.sandroid.ui.downloader.manager.QuickScrollLinearLayoutManager
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


open class DownloadManagerFragment : Fragment() {
    private val groupId = "public".hashCode()
    private val mutex = Mutex()
    private var fetch: Fetch? = null
    private var fetchListener: FetchListener = getFetchListener()
    private var onScrollListener: RecyclerView.OnScrollListener = getOnScrollLister()

    private fun getOnScrollLister(): RecyclerView.OnScrollListener {
        return object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(
                recyclerView: RecyclerView,
                newState: Int
            ) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    downloadManagerViewModel?.loadMore()
                }
            }
        }
    }

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
                downloadManagerViewModel?.addOrUpdate(download, true)
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
        val testDownloader1 = root.findViewById<AppCompatButton>(R.id.test_downloader_1)
        val testDownloader2 = root.findViewById<AppCompatButton>(R.id.test_downloader_2)
        val testDownloader3 = root.findViewById<AppCompatButton>(R.id.test_downloader_3)
        val downloaderTaskView = root.findViewById<EpoxyRecyclerView>(R.id.downloader_task_view)
        fetch = MyApplication.fetch
        layoutManager = QuickScrollLinearLayoutManager(
            requireContext(),
            RecyclerView.VERTICAL,
            false
        )
        downloaderTaskView.layoutManager = layoutManager
        layoutManager.stackFromEnd = false
        downloadTaskListController = DownloadTaskListController(requireContext(), fetch)
        downloaderTaskView.setController(downloadTaskListController)
        downloaderTaskView.addOnScrollListener(onScrollListener)
        initObserver()
        // init
        fetch?.addListener(fetchListener)
        MainScope().launch {
            downloadManagerViewModel?.getData(fetch, groupId)
        }
        testDownloader1.setOnClickListener {
            startDownloadFile("http://speedtest.ftp.otenet.gr/files/test100Mb.db", Priority.HIGH)
        }
        testDownloader2.setOnClickListener {
            startDownloadFile("http://speedtest.ftp.otenet.gr/files/test100Mb.db")
        }
        testDownloader3.setOnClickListener {
            startDownloadFile("http://192.168.2.70:8080/tools/DouYin/player/video?vid=v0200fg10000c8t94c3c77u933tk63m0&ratio=540p&isDownload=1")
        }
        return root
    }

    private fun startDownloadFile(downloadUrl: String, priority: Priority = Priority.NORMAL) {
        var filePath = this.context?.getExternalFilesDir("download")?.absolutePath
        FileUtils.createOrExistsDir(filePath)
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
                    groupId,
                    priority
                ).let {
                    fetch?.enqueue(it)
                }
            }
        }
    }

    private fun initObserver() {
        downloadManagerViewModel!!.fileInfoList.observe(viewLifecycleOwner) {
            downloadTaskListController.fileInfoList = it
        }
        downloadManagerViewModel!!.hasMore.observe(viewLifecycleOwner) {
            downloadTaskListController.hasMore = it
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