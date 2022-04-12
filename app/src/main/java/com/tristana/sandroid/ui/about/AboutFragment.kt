package com.tristana.sandroid.ui.about

import android.graphics.text.LineBreaker
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.tristana.sandroid.R
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.AppUtils
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.util.QMUIResHelper
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView
import com.tristana.sandroid.model.data.AboutModel.*

class AboutFragment : Fragment() {

    private var aboutViewModel: AboutViewModel? = null
    private lateinit var mGroupListView: QMUIGroupListView
    private var na: String = "N/A"

    private var onClickListener = View.OnClickListener { }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (aboutViewModel == null) aboutViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(
                    AboutViewModel::class.java
                )
        val root = inflater.inflate(R.layout.fragment_about, container, false)
        mGroupListView = root.findViewById(R.id.groupListView)
        val appIcon: AppCompatImageView = root.findViewById(R.id.app_icon)
        appIcon.setImageDrawable(AppUtils.getAppIcon())
        val height =
            QMUIResHelper.getAttrDimen(context, com.qmuiteam.qmui.R.attr.qmui_list_item_height)
        initAppInfoSection(aboutViewModel!!, height)
        return root
    }

    private fun initAppInfoSection(aboutViewModel: AboutViewModel, height: Int) {
        val appName = createElement(APP_NAME)
        val appPackageName = createElement(APP_PACKAGE_NAME)
        val appVersionName = createElement(APP_VERSION_NAME)
        val appVersionCode = createElement(APP_VERSION_CODE)
        val appPathName = createElement(APP_PATH_NAME)
        val appRootMode = createElement(APP_ROOT_MODE)
        val appDebugMode = createElement(APP_DEBUG_MODE)
        val systemAppMode = createElement(SYSTEM_APP_MODE)
        val appSignatureNameSHA1 = createElement(APP_SIGNATURE_NAME_SHA1)
        val appSignatureNameSHA256 = createElement(APP_SIGNATURE_NAME_SHA256)
        val appSignatureNameMD5 = createElement(APP_SIGNATURE_NAME_MD5)

        val size = QMUIDisplayHelper.dp2px(context, 20)
        QMUIGroupListView.newSection(requireContext())
            .setTitle("App信息")
            .setDescription("")
            .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
            .addItemView(appName, onClickListener)
            .addItemView(appPackageName, onClickListener)
            .addItemView(appVersionName, onClickListener)
            .addItemView(appVersionCode, onClickListener)
            .addItemView(appPathName, onClickListener)
            .addItemView(appRootMode, onClickListener)
            .addItemView(appDebugMode, onClickListener)
            .addItemView(systemAppMode, onClickListener)
            .addItemView(appSignatureNameSHA1, onClickListener)
            .addItemView(appSignatureNameSHA256, onClickListener)
            .addItemView(appSignatureNameMD5, onClickListener)
            .setShowSeparator(true)
            .addTo(mGroupListView)

        aboutViewModel.getAppName.observe(viewLifecycleOwner) { text: String? ->
            updateElement(appName, text, height)
        }
        aboutViewModel.getAppPackageName.observe(viewLifecycleOwner) { text: String? ->
            updateElement(appPackageName, text, height)
        }
        aboutViewModel.getAppVersionName.observe(viewLifecycleOwner) { text: String? ->
            updateElement(appVersionName, text, height)
        }
        aboutViewModel.getAppVersionCode.observe(viewLifecycleOwner) { text: String? ->
            updateElement(appVersionCode, text, height)
        }
        aboutViewModel.getAppPathName.observe(viewLifecycleOwner) { text: String? ->
            updateElement(appPathName, text, height)
        }
        aboutViewModel.getAppRootMode.observe(viewLifecycleOwner) { text: String? ->
            updateElement(appRootMode, text, height)
        }
        aboutViewModel.getAppDebugMode.observe(viewLifecycleOwner) { text: String? ->
            updateElement(appDebugMode, text, height)
        }
        aboutViewModel.getSystemAppMode.observe(viewLifecycleOwner) { text: String? ->
            updateElement(systemAppMode, text, height)
        }
        aboutViewModel.getAppSignatureNameSHA1.observe(viewLifecycleOwner) { text: String? ->
            updateElement(appSignatureNameSHA1, text, height)
        }
        aboutViewModel.getAppSignatureNameSHA256.observe(viewLifecycleOwner) { text: String? ->
            updateElement(appSignatureNameSHA256, text, height)
        }
        aboutViewModel.getAppSignatureNameMD5.observe(viewLifecycleOwner) { text: String? ->
            updateElement(appSignatureNameMD5, text, height)
        }
    }

    private fun createElement(name: String): QMUICommonListItemView {
        return mGroupListView.createItemView(
            null,
            name,
            na,
            QMUICommonListItemView.HORIZONTAL,
            QMUICommonListItemView.ACCESSORY_TYPE_NONE
        )
    }

    private fun updateElement(element: QMUICommonListItemView?, text: String?, minHeight: Int) {
        element?.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
        element?.minHeight = minHeight
        element?.detailText = text
        element?.detailTextView?.gravity = GravityCompat.END
        element?.detailTextView?.setPadding(0, 12, 0, 12)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            element?.detailTextView?.breakStrategy = LineBreaker.BREAK_STRATEGY_BALANCED
        }
    }
}