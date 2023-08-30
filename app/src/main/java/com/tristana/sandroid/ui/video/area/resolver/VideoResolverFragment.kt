package com.tristana.sandroid.ui.video.area.resolver

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import butterknife.BindView
import butterknife.ButterKnife
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.Priority
import com.tristana.library.tools.http.HttpUtils
import com.tristana.sandroid.FragmentDirector
import com.tristana.sandroid.MyApplication
import com.tristana.sandroid.R
import com.tristana.sandroid.downloader.utils.RequestObjectUtils
import com.tristana.sandroid.ui.ad.AdWebViewFragment
import com.tristana.sandroid.ui.components.LoadingDialog
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.HttpResponse
import cz.msebera.android.httpclient.client.config.RequestConfig
import cz.msebera.android.httpclient.client.methods.HttpGet
import cz.msebera.android.httpclient.impl.client.HttpClients
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class VideoResolverFragment : Fragment() {

    private var viewModel: VideoResolverViewModel? = null

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.video_resolver_link_input)
    lateinit var inputLink: AppCompatEditText

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.video_resolver_clear)
    lateinit var buttonClear: AppCompatButton

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.video_resolver_from_clipboard)
    lateinit var buttonFromClipboard: AppCompatButton

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.video_resolver_operation)
    lateinit var buttonResolverOperation: AppCompatButton

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.video_resolver_empty)
    lateinit var imageResolverEmpty: AppCompatImageView

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.video_resolver_preview)
    lateinit var buttonResolverPreview: AppCompatButton

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.video_resolver_preview_backup)
    lateinit var buttonResolverPreviewBackup: AppCompatButton

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.video_resolver_download)
    lateinit var buttonResolverDownload: AppCompatButton

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.video_resolver_download_browser)
    lateinit var buttonResolverDownloadBrowser: AppCompatButton

    private var fetch: Fetch? = null

    private val groupId = "public".hashCode()

    private val loadingDialog by lazy {
        LoadingDialog(requireContext(), getString(R.string.is_resolving), false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (viewModel == null) viewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(
                    VideoResolverViewModel::class.java
                )
        val root = inflater.inflate(R.layout.fragment_video_resolver, container, false)
        ButterKnife.bind(this, root)
        fetch = MyApplication().getFetchInstance(MyApplication.instance!!)
        initObserver()
        buttonClear.setOnClickListener {
            viewModel?.link?.value = ""
        }
        buttonFromClipboard.setOnClickListener { input ->
            input?.let {
                viewModel?.link?.value = ClipboardUtils.getText().toString()
            }
        }
        buttonResolverOperation.setOnClickListener {
            viewModel?.link?.value?.let { input ->
                if (input.isEmpty()) {
                    ToastUtils.showLong("分享链接不可为空")
                } else {
                    loadingDialog.show()
                    CoroutineScope(Dispatchers.Main).launch {
                        imageResolverEmpty.visibility = View.VISIBLE
                        buttonResolverPreview.visibility = View.GONE
                        buttonResolverPreviewBackup.visibility = View.GONE
                        buttonResolverDownload.visibility = View.GONE
                        buttonResolverDownloadBrowser.visibility = View.GONE
                        viewModel?.doRequest()
                    }
                }
            }
        }
        return root
    }

    private fun initObserver() {
        viewModel?.link?.observe(viewLifecycleOwner) { input ->
            input?.let {
                inputLink.text = SpannableStringBuilder(it)
            }
        }
        viewModel?.resolverData?.observe(viewLifecycleOwner) { resolverData ->
            loadingDialog.dismiss()
            if (resolverData?.mockPreviewPicturePath == null && resolverData?.realMusicPath == null && resolverData?.mockPreviewMusicPath == null && resolverData?.mockDownloadMusicPath == null && resolverData?.realVideoPath == null && resolverData?.mockPreviewVideoPath == null && resolverData?.mockDownloadVideoPath == null && resolverData?.mockPreviewLivePath?.isEmpty() == true) {
                imageResolverEmpty.visibility = View.VISIBLE
                buttonResolverPreview.visibility = View.GONE
                buttonResolverPreviewBackup.visibility = View.GONE
                buttonResolverDownload.visibility = View.GONE
                buttonResolverDownloadBrowser.visibility = View.GONE
            }
//            resolverData?.realMusicPath?.let { path ->
//                imageResolverEmpty.visibility = View.GONE
//                buttonResolverDownload.visibility = View.VISIBLE
//                buttonResolverDownload.setOnClickListener {
//                    startDownloadFile(path)
//                }
//            }
            resolverData?.mockPreviewPicturePath?.let { path ->
                imageResolverEmpty.visibility = View.GONE
                buttonResolverPreview.visibility = View.VISIBLE
                buttonResolverPreview.setOnClickListener {
                    doDirect(path)
                }
            }
            resolverData?.mockPreviewMusicPath?.let { path ->
                imageResolverEmpty.visibility = View.GONE
                buttonResolverPreview.visibility = View.VISIBLE
                buttonResolverPreview.setOnClickListener {
                    doDirect(path)
                }
            }
//            resolverData?.realVideoPath?.let { path ->
//                imageResolverEmpty.visibility = View.GONE
//                buttonResolverDownload.visibility = View.VISIBLE
//                buttonResolverDownload.setOnClickListener {
//                    startDownloadFile(path)
//                }
//            }
            resolverData?.mockDownloadMusicPath?.let { path ->
                imageResolverEmpty.visibility = View.GONE
                buttonResolverDownloadBrowser.visibility = View.VISIBLE
                buttonResolverDownloadBrowser.setOnClickListener {
                    jumpToBrowser(path)
                }
            }
            resolverData?.mockPreviewVideoPath?.let { path ->
                imageResolverEmpty.visibility = View.GONE
                buttonResolverPreview.visibility = View.VISIBLE
                buttonResolverPreview.setOnClickListener {
                    doDirect(path)
                }
            }
            resolverData?.mockDownloadVideoPath?.let { path ->
                imageResolverEmpty.visibility = View.GONE
                buttonResolverDownloadBrowser.visibility = View.VISIBLE
                buttonResolverDownloadBrowser.setOnClickListener {
                    jumpToBrowser(path)
                }
            }
            resolverData?.mockPreviewLivePath?.let { path ->
                imageResolverEmpty.visibility = View.GONE
                if (path.size == 2) {
                    buttonResolverPreview.visibility = View.VISIBLE
                    buttonResolverPreviewBackup.visibility = View.VISIBLE
                    buttonResolverPreview.setOnClickListener {
                        doDirect(path[0])
                    }
                    buttonResolverPreviewBackup.setOnClickListener {
                        doDirect(path[1])
                    }
                } else if (path.size == 1) {
                    buttonResolverPreview.visibility = View.VISIBLE
                    buttonResolverPreview.setOnClickListener {
                        doDirect(path[0])
                    }
                }
            }
        }
    }

    private fun doDirect(directionPath: String?) {
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        if (directionPath?.isNotEmpty() == true) {
            val direct = AdWebViewFragment.ROUTE
            FragmentDirector.doDirect(navController, direct, directionPath)
        }
    }

    private fun jumpToBrowser(path: String) {
        val uri = Uri.parse(path)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
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
                    requireContext(), downloadUrl, filePath, groupId, priority
                ).let {
                    fetch?.enqueue(it)
                }
            }
        }
    }

    private fun getLocationUrl(url: String?): String? {
        val config: RequestConfig =
            RequestConfig.custom().setConnectTimeout(50000).setConnectionRequestTimeout(10000)
                .setSocketTimeout(50000).setRedirectsEnabled(false).build() //不允许重定向
        val httpClient = HttpClients.custom().setDefaultRequestConfig(config).build()
        var location: String? = null
        var responseCode = 0
        val response: HttpResponse
        try {
            response = httpClient.execute(HttpGet(url))
            responseCode = response.getStatusLine().statusCode
            if (responseCode == 302) {
                val locationHeader: Header = response.getLastHeader("Location")
                location = locationHeader.value
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return location
    }

}