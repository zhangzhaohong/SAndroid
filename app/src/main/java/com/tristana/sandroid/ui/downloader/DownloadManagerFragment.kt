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
import com.tristana.sandroid.MyApplication
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
            filePath = filePath + "/" + "demo.apk"
            LogUtils.i(filePath)
            val downloadUrl =
                "https://dev-081.baidupan.com/7e2f3789f57bd23b66ff36633c940ea3/1650086009/2018/07/06/31c57c32fe3ed5ab742035d335679860.apk?filename=V8.0.0.1023_debug_CheckIn_20180705_.apk"
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
        return root
    }
}