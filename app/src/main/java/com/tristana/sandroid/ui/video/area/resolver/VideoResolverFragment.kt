package com.tristana.sandroid.ui.video.area.resolver

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
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
import com.blankj.utilcode.util.ToastUtils
import com.tristana.sandroid.FragmentDirector
import com.tristana.sandroid.R
import com.tristana.sandroid.ui.ad.AdWebViewFragment
import com.tristana.sandroid.ui.components.LoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            if (resolverData?.mockPreviewPicturePath == null && resolverData?.mockPreviewMusicPath == null && resolverData?.mockDownloadMusicPath == null && resolverData?.mockPreviewVideoPath == null && resolverData?.mockDownloadVideoPath == null && resolverData?.mockPreviewLivePath?.isEmpty() == true) {
                imageResolverEmpty.visibility = View.VISIBLE
                buttonResolverPreview.visibility = View.GONE
                buttonResolverPreviewBackup.visibility = View.GONE
                buttonResolverDownload.visibility = View.GONE
            }
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
            resolverData?.mockDownloadMusicPath?.let { path ->
                imageResolverEmpty.visibility = View.GONE
                buttonResolverDownload.visibility = View.VISIBLE
                buttonResolverDownload.setOnClickListener {
                    // TODO DOWNLOAD
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
                buttonResolverDownload.visibility = View.VISIBLE
                buttonResolverDownload.setOnClickListener {
                    // TODO DOWNLOAD
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
        val navController =
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        if (directionPath?.isNotEmpty() == true) {
            val direct = AdWebViewFragment.ROUTE
            FragmentDirector.doDirect(navController, direct, directionPath)
        }
    }

}