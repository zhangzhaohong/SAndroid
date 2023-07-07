package com.tristana.sandroid.ui.ad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.event.tracker.ws.Constants
import com.event.tracker.ws.model.EventTrackerDataModel
import com.therouter.TheRouter
import com.therouter.router.Route
import com.tristana.library.customizeInterface.IOnPageFinishedInterface
import com.tristana.library.view.webView.X5WebView
import com.tristana.sandroid.MainActivity
import com.tristana.sandroid.MyApplication
import com.tristana.sandroid.R
import com.tristana.sandroid.customizeInterface.IOnBackPressedInterface

@Route(path = AdWebViewFragment.ROUTE)
class AdWebViewFragment : Fragment(), IOnBackPressedInterface, IOnPageFinishedInterface {

    companion object {
        const val ROUTE = "/app/browser/ad"
    }

    private var url: String? = null
    private lateinit var x5WebView: X5WebView
    private var adViewModel: AdViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TheRouter.inject(this)
        MyApplication.eventTrackerInstance?.sendEvent(
            Constants.EVENT_ON_OPENED_FRAGMENT,
            EventTrackerDataModel(ROUTE)
        )
    }
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
        val bundle = arguments
        if (bundle != null) {
            url = bundle.getString("url").toString()
        }
        if (url == null || url!!.trim().isEmpty()) {
            url = "http://about:blank"
        }
        x5WebView = root.findViewById(R.id.ad_web_viewer)
        x5WebView.init(requireActivity())
        x5WebView.loadUrl(url)
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