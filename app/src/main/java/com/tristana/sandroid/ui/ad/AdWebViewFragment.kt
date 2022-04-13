package com.tristana.sandroid.ui.ad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tristana.customViewWithToolsLibrary.customInterface.IOnPageFinishedInterface
import com.tristana.customViewWithToolsLibrary.view.webView.X5WebView
import com.tristana.sandroid.MainActivity
import com.tristana.sandroid.R
import com.tristana.sandroid.customInterface.IOnBackPressedInterface

class AdWebViewFragment : Fragment(), IOnBackPressedInterface, IOnPageFinishedInterface {
    private lateinit var x5WebView: X5WebView
    private var adViewModel: AdViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (adViewModel == null) adViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(
                    AdViewModel::class.java
                )
        val root = inflater.inflate(R.layout.fragment_ad_webview, container, false)
        x5WebView = root.findViewById(R.id.ad_web_viewer)
        x5WebView.init(requireActivity())
        x5WebView.loadUrl("https://www.baidu.com")
        x5WebView.onLoadFinishListener = this
        hideActionBar()
        return root
    }

    override fun onBackPressed(): Boolean {
        return if (x5WebView.canGoBack()) {
            x5WebView.goBack()
            false
        } else {
            true
        }
    }

    override fun onPageFinished(p0: String?) {
        return
    }

    private fun showActionBar() {
        getActionBar()?.show()
    }

    private fun hideActionBar() {
        getActionBar()?.hide()
    }

    private fun getActionBar(): ActionBar? {
        return (activity as MainActivity?)?.supportActionBar
    }

    override fun onDestroy() {
        super.onDestroy()
        showActionBar()
    }
}