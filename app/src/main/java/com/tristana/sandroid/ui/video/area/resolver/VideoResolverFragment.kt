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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import butterknife.BindView
import butterknife.ButterKnife
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.tristana.sandroid.R

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
        viewModel?.link?.observe(viewLifecycleOwner) { input ->
            input?.let {
                inputLink.text = SpannableStringBuilder(it)
            }
        }
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

                }
            }
        }
        return root
    }

}