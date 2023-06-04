package com.tristana.sandroid.ui.webview

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.event.tracker.ws.Constants
import com.event.tracker.ws.model.EventTrackerDataModel
import com.therouter.router.Route
import com.tristana.library.customizeInterface.IOnPageFinishedInterface
import com.tristana.library.view.editTextView.CustomEditTextView
import com.tristana.library.view.webView.X5WebView
import com.tristana.sandroid.MainActivity
import com.tristana.sandroid.MyApplication
import com.tristana.sandroid.R
import com.tristana.sandroid.customizeInterface.IOnBackPressedInterface


@Suppress("CAST_NEVER_SUCCEEDS")
@Route(path = X5WebViewFragment.ROUTE)
class X5WebViewFragment : Fragment(), IOnBackPressedInterface, IOnPageFinishedInterface {

    companion object {
        const val ROUTE = "/app/browser"
    }

    private lateinit var webViewBack: AppCompatImageView
    private lateinit var webViewForward: AppCompatImageView
    private lateinit var webViewHome: AppCompatImageView
    private lateinit var webViewRefresh: AppCompatImageView
    private lateinit var webViewExit: AppCompatImageView
    private var defaultUrl: String = "https://www.baidu.com"
    private var url: String = ""
    private var x5ViewModel: X5ViewModel? = null
    private lateinit var x5WebView: X5WebView
    private lateinit var inputUrl: CustomEditTextView
    private lateinit var webViewEnter: AppCompatImageView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (x5ViewModel == null) x5ViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(X5ViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_web_viewer, container, false)
        val bundle = arguments
        if (bundle != null) {
            url = bundle.getString("url").toString()
        }
        if (url.isEmpty()) {
            url = "https://www.baidu.com"
        }
        x5WebView = root.findViewById(R.id.web_viewer)
        inputUrl = root.findViewById(R.id.input_url)
        webViewEnter = root.findViewById(R.id.webView_enter)
        webViewBack = root.findViewById(R.id.browser_back)
        webViewForward = root.findViewById(R.id.browser_forward)
        webViewHome = root.findViewById(R.id.browser_home)
        webViewRefresh = root.findViewById(R.id.browser_refresh)
        webViewExit = root.findViewById(R.id.browser_exit)
        x5WebView.init(requireActivity())
        x5WebView.loadUrl(url)
        inputUrl.initParameter(
            R.drawable.ic_browser_default,
            InputType.TYPE_CLASS_TEXT,
            0,
            1,
            "",
            0,
            0,
            false,
            R.drawable.ic_clear,
            true
        )
        webViewEnter.setOnClickListener {
            inputUrl.clearFocus()
            x5WebView.loadUrl(inputUrl.getText())
        }
        webViewBack.setOnClickListener {
            if (x5WebView.canGoBack()) {
                x5WebView.goBack()
            }
        }
        webViewForward.setOnClickListener {
            if (x5WebView.canGoForward()) {
                x5WebView.goForward()
            }
        }
        webViewHome.setOnClickListener {
            x5WebView.loadUrl(defaultUrl)
        }
        webViewRefresh.setOnClickListener {
            x5WebView.loadUrl(url)
        }
        webViewExit.setOnClickListener {
            x5WebView.destroy()
            requireActivity().onBackPressed()
        }
        x5WebView.onLoadFinishListener = this
        hideActionBar()
        MyApplication.eventTrackerInstance?.sendEvent(
            Constants.EVENT_ON_OPENED_FRAGMENT,
            EventTrackerDataModel(X5WebViewFragment.ROUTE)
        )
        return root
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

    /**
     * If you return true the back press will not be taken into account, otherwise the activity will act naturally
     * @return true if your processing has priority if not false
     */
    override fun onBackPressed(): Boolean {
        return if (x5WebView.canGoBack()) {
            x5WebView.goBack()
            false
        } else {
            true
        }
    }

    override fun onPageFinished(p0: String?) {
        inputUrl.setText(p0.toString())
        url = p0.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        showActionBar()
    }

}