package com.tristana.sandroid.ui.about

import com.tristana.sandroid.ui.about.AboutViewModel
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.tristana.sandroid.R
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.util.QMUIResHelper
import com.qmuiteam.qmui.widget.QMUILoadingView
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView
import com.tristana.sandroid.model.data.AboutModel.*

class AboutFragment : Fragment() {

    private var aboutViewModel: AboutViewModel? = null
    private lateinit var mGroupListView: QMUIGroupListView
    private var loading: String = "Loading"

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
        val height =
            QMUIResHelper.getAttrDimen(context, com.qmuiteam.qmui.R.attr.qmui_list_item_height)

        val appName = mGroupListView.createItemView(APP_NAME)
        appName.accessoryType = QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM
        appName.addAccessoryCustomView(QMUILoadingView(activity))

        val appPackageName = mGroupListView.createItemView(APP_PACKAGE_NAME)
        appName.accessoryType = QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM
        appPackageName.addAccessoryCustomView(QMUILoadingView(activity))

        val appVersionName = mGroupListView.createItemView(APP_VERSION_NAME)
        appVersionName.accessoryType = QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM
        appVersionName.addAccessoryCustomView(QMUILoadingView(activity))

        val appVersionCode = mGroupListView.createItemView(APP_VERSION_CODE)
        appVersionCode.accessoryType = QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM
        appVersionCode.addAccessoryCustomView(QMUILoadingView(activity))

        val appPathName = mGroupListView.createItemView(APP_PATH_NAME)
        appPathName.accessoryType = QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM
        appPathName.addAccessoryCustomView(QMUILoadingView(activity))

        val appSignatureNameSHA1 = mGroupListView.createItemView(APP_SIGNATURE_NAME_SHA1)
        appSignatureNameSHA1.accessoryType = QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM
        appSignatureNameSHA1.addAccessoryCustomView(QMUILoadingView(activity))

        val appSignatureNameSHA256 = mGroupListView.createItemView(APP_SIGNATURE_NAME_SHA256)
        appSignatureNameSHA256.accessoryType = QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM
        appSignatureNameSHA256.addAccessoryCustomView(QMUILoadingView(activity))

        val appSignatureNameMD5 = mGroupListView.createItemView(APP_SIGNATURE_NAME_MD5)
        appSignatureNameMD5.accessoryType = QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM
        appSignatureNameMD5.addAccessoryCustomView(QMUILoadingView(activity))

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
            .addItemView(appSignatureNameSHA1, onClickListener)
            .addItemView(appSignatureNameSHA256, onClickListener)
            .addItemView(appSignatureNameMD5, onClickListener)
            .setOnlyShowStartEndSeparator(true)
            .addTo(mGroupListView)

        aboutViewModel!!.getAppName.observe(viewLifecycleOwner) { text: String? ->
            appName.removeView(appName.accessoryContainerView)
            appName.detailText = text
        }
        aboutViewModel!!.getAppPackageName.observe(viewLifecycleOwner) { text: String? ->
            appPackageName.removeView(appPackageName.accessoryContainerView)
            appPackageName.detailText = text
        }
        aboutViewModel!!.getAppVersionName.observe(viewLifecycleOwner) { text: String? ->
            appVersionName.removeView(appVersionName.accessoryContainerView)
            appVersionName.detailText = text
        }
        aboutViewModel!!.getAppVersionCode.observe(viewLifecycleOwner) { text: String? ->
            appVersionCode.removeView(appVersionCode.accessoryContainerView)
            appVersionCode.detailText = text
        }
        aboutViewModel!!.getAppPathName.observe(viewLifecycleOwner) { text: String? ->
            appPathName.removeView(appPathName.accessoryContainerView)
            appPathName.detailText = text
        }
        aboutViewModel!!.getAppSignatureNameSHA1.observe(viewLifecycleOwner) { text: String? ->
            appSignatureNameSHA1.removeView(appSignatureNameSHA1.accessoryContainerView)
            appSignatureNameSHA1.detailText = text
        }
        aboutViewModel!!.getAppSignatureNameSHA256.observe(viewLifecycleOwner) { text: String? ->
            appSignatureNameSHA256.removeView(appSignatureNameSHA256.accessoryContainerView)
            appSignatureNameSHA256.detailText = text
        }
        aboutViewModel!!.getAppSignatureNameMD5.observe(viewLifecycleOwner) { text: String? ->
            appSignatureNameMD5.removeView(appSignatureNameMD5.accessoryContainerView)
            appSignatureNameMD5.detailText = text
        }
        return root
    }
}