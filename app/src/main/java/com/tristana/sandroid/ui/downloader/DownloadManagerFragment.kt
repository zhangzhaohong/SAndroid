package com.tristana.sandroid.ui.downloader

import com.tristana.sandroid.ui.downloader.DownloadManagerViewModel
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.tristana.sandroid.R
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.hl.downloader.DownloadListener
import com.hl.downloader.DownloadManager
import com.tristana.customViewWithToolsLibrary.tools.http.HttpUtils
import com.tristana.sandroid.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class DownloadManagerFragment : Fragment() {
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
        downloadManagerViewModel!!.text.observe(viewLifecycleOwner) { s -> textView.text = s }
        textView.setOnClickListener {
            var filePath = this.context?.getExternalFilesDir("download")?.absolutePath
            FileUtils.createOrExistsDir(filePath)
            val downloadUrl =
                "http://192.168.2.70:8080/tools/DouYin/player/video?vid=v0200fg10000c8t94c3c77u933tk63m0&ratio=540p&isDownload=1"
                // "https://developer.lanzoug.com/file/?UzUFOwEwVWRVXAY+VGFSPltkV28EDQI4By5XYQJwADEFK1o8D2JQNgdiCwJQYFM3ADgDcwI6BVgFQVA+Vm8HZVM4BRsBMlVeVTEGYFQxUmtbMVdlBGsCNQdfV38CPwBxBW5aKw80UG0HPQs4UFtTOwA+AzsCaQU3BTNQZFYyBzRTagV0ATpVclU+BmlUOVJlWzhXYARiAjAHJlchAi4APAU3Wj0PY1A8B34LbVAzU30AagMwAnIFNAUzUDVWPwcxUzAFYQFuVWdVZgZjVGVSN1s0VzMEOQI3BzRXYwJuADIFMFppD2FQNwdkC2tQM1NrAGIDMAJtBSkFY1AmVmEHIFMmBSEBOVVzVWoGNFQ9UmVbMFdiBG4CMwc4V3cCKgBoBWhaaA80UDkHYAtrUDFTYgBrAzYCbgUwBTZQZlYsB2BTPwUlAWFVMFU1BmVUMFJiWzFXYQRqAjMHNFd3AisAcQVyWjAPY1AxB2ILbVA8U2sAaAMwAmoFMwUkUCNWYwd2U24FYAFsVTVVLQZhVDRSa1svV2AEawI2By5XYwJtADMFI1ppDz5QOQdh"
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
        return root
    }
}