package com.tristana.sandroid.ui.about

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.tristana.sandroid.R
import androidx.fragment.app.Fragment
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
        val height =
            QMUIResHelper.getAttrDimen(context, com.qmuiteam.qmui.R.attr.qmui_list_item_height)

        val appName = mGroupListView.createItemView(
            null,
            APP_NAME,
            na,
            QMUICommonListItemView.HORIZONTAL,
            QMUICommonListItemView.ACCESSORY_TYPE_NONE
        )

        val appPackageName = mGroupListView.createItemView(
            null,
            APP_PACKAGE_NAME,
            na,
            QMUICommonListItemView.HORIZONTAL,
            QMUICommonListItemView.ACCESSORY_TYPE_NONE
        )

        val appVersionName = mGroupListView.createItemView(
            null,
            APP_VERSION_NAME,
            na,
            QMUICommonListItemView.HORIZONTAL,
            QMUICommonListItemView.ACCESSORY_TYPE_NONE
        )

        val appVersionCode = mGroupListView.createItemView(
            null,
            APP_VERSION_CODE,
            na,
            QMUICommonListItemView.HORIZONTAL,
            QMUICommonListItemView.ACCESSORY_TYPE_NONE
        )

        val appPathName = mGroupListView.createItemView(
            null,
            APP_PATH_NAME,
            na,
            QMUICommonListItemView.VERTICAL,
            QMUICommonListItemView.ACCESSORY_TYPE_NONE,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val appSignatureNameSHA1 = mGroupListView.createItemView(
            null,
            APP_SIGNATURE_NAME_SHA1,
            na,
            QMUICommonListItemView.VERTICAL,
            QMUICommonListItemView.ACCESSORY_TYPE_NONE,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val appSignatureNameSHA256 = mGroupListView.createItemView(
            null,
            APP_SIGNATURE_NAME_SHA256,
            na,
            QMUICommonListItemView.VERTICAL,
            QMUICommonListItemView.ACCESSORY_TYPE_NONE,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val appSignatureNameMD5 = mGroupListView.createItemView(
            null,
            APP_SIGNATURE_NAME_MD5,
            na,
            QMUICommonListItemView.VERTICAL,
            QMUICommonListItemView.ACCESSORY_TYPE_NONE,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

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
            appName.detailText = text
        }
        aboutViewModel!!.getAppPackageName.observe(viewLifecycleOwner) { text: String? ->
            appPackageName.detailText = text
        }
        aboutViewModel!!.getAppVersionName.observe(viewLifecycleOwner) { text: String? ->
            appVersionName.detailText = text
        }
        aboutViewModel!!.getAppVersionCode.observe(viewLifecycleOwner) { text: String? ->
            appVersionCode.detailText = text
        }
        aboutViewModel!!.getAppPathName.observe(viewLifecycleOwner) { text: String? ->
            appPathName.detailText = text
        }
        aboutViewModel!!.getAppSignatureNameSHA1.observe(viewLifecycleOwner) { text: String? ->
            appSignatureNameSHA1.detailText = text
        }
        aboutViewModel!!.getAppSignatureNameSHA256.observe(viewLifecycleOwner) { text: String? ->
            appSignatureNameSHA256.detailText = text
        }
        aboutViewModel!!.getAppSignatureNameMD5.observe(viewLifecycleOwner) { text: String? ->
            appSignatureNameMD5.detailText = text
        }
        return root
    }
}