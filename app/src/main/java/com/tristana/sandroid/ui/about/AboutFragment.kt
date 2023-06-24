package com.tristana.sandroid.ui.about

import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ClickUtils.OnMultiClickListener
import com.blankj.utilcode.util.ToastUtils
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.util.QMUIResHelper
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView
import com.tristana.library.tools.sharedPreferences.SpUtils
import com.tristana.sandroid.R
import com.tristana.sandroid.model.data.AboutModel.ABIS
import com.tristana.sandroid.model.data.AboutModel.ANDROID_ID
import com.tristana.sandroid.model.data.AboutModel.APP_BUILD_INFO
import com.tristana.sandroid.model.data.AboutModel.APP_DEBUG_MODE
import com.tristana.sandroid.model.data.AboutModel.APP_NAME
import com.tristana.sandroid.model.data.AboutModel.APP_PACKAGE_NAME
import com.tristana.sandroid.model.data.AboutModel.APP_PATH_NAME
import com.tristana.sandroid.model.data.AboutModel.APP_ROOT_MODE
import com.tristana.sandroid.model.data.AboutModel.APP_SIGNATURE_NAME_MD5
import com.tristana.sandroid.model.data.AboutModel.APP_SIGNATURE_NAME_SHA1
import com.tristana.sandroid.model.data.AboutModel.APP_SIGNATURE_NAME_SHA256
import com.tristana.sandroid.model.data.AboutModel.APP_VERSION_CODE
import com.tristana.sandroid.model.data.AboutModel.APP_VERSION_NAME
import com.tristana.sandroid.model.data.AboutModel.DEVICE_ADB_MODE
import com.tristana.sandroid.model.data.AboutModel.DEVICE_ROOT_MODE
import com.tristana.sandroid.model.data.AboutModel.IS_EMULATOR
import com.tristana.sandroid.model.data.AboutModel.IS_TABLET
import com.tristana.sandroid.model.data.AboutModel.MAC_ADDRESS
import com.tristana.sandroid.model.data.AboutModel.MANU_FACTURER
import com.tristana.sandroid.model.data.AboutModel.MODEL
import com.tristana.sandroid.model.data.AboutModel.SDK_VERSION_CODE
import com.tristana.sandroid.model.data.AboutModel.SDK_VERSION_NAME
import com.tristana.sandroid.model.data.AboutModel.SERVER_BUILD_TIME
import com.tristana.sandroid.model.data.AboutModel.SERVER_ENV
import com.tristana.sandroid.model.data.AboutModel.SERVER_HOST
import com.tristana.sandroid.model.data.AboutModel.SERVER_VERSION
import com.tristana.sandroid.model.data.AboutModel.SYSTEM_APP_MODE
import com.tristana.sandroid.model.data.AboutModel.SYSTEM_VERSION_NAME
import com.tristana.sandroid.model.data.AboutModel.UNIQUE_DEVICE_ID
import com.tristana.sandroid.model.data.DataModel.ENABLE_SHOW_LAB_SP
import java.util.Optional

class AboutFragment : Fragment() {

    private var aboutViewModel: AboutViewModel? = null
    private lateinit var mGroupListView: QMUIGroupListView
    private var na: String = "N/A"

    private var onClickListener = View.OnClickListener { }

    private fun onMultiClickListener(count: Int = 10): OnMultiClickListener {
        return object : OnMultiClickListener(count) {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onTriggerClick(v: View) {
                if (v is QMUICommonListItemView) {
                    if (APP_VERSION_NAME == v.text) {
                        SpUtils.put(requireContext(), ENABLE_SHOW_LAB_SP, true)
                        needRestart(false, "您已成功进入开发者模式")
                        ToastUtils.showShort("您已成功进入开发者模式")
                    }
                }
            }

            override fun onBeforeTriggerClick(v: View, current: Int) {
                if (v is QMUICommonListItemView) {
                    if (APP_VERSION_NAME == v.text) {
                        val times = count - current
                        if (times <= count / 2) {
                            ToastUtils.showShort("再按${times}即可进入开发者模式")
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun needRestart(later: Boolean = true, text: String?) {
        val builder = QMUIDialog.MessageDialogBuilder(activity)
        builder
            .setTitle("提示")
            .setMessage(Optional.ofNullable(text).orElse("设置已保存，重启App生效"))
            .setCancelable(later)
            .setCanceledOnTouchOutside(later)
        if (later) {
            builder.addAction(
                "稍后重启"
            ) { dialog, _ -> dialog.dismiss() }
        }
        builder.addAction(
            "立即重启"
        ) { dialog, _ ->
            run {
                dialog.dismiss()
                AppUtils.relaunchApp(true)
            }
        }
        builder.show()
    }

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
        initDeviceInfoSection(aboutViewModel!!, height)
        initServerInfo(aboutViewModel!!, height)
        return root
    }

    private fun initDeviceInfoSection(aboutViewModel: AboutViewModel, height: Int) {
        val deviceRootMode = createElement(DEVICE_ROOT_MODE, height)
        val deviceAdbMode = createElement(DEVICE_ADB_MODE, height)
        val sdkVersionName = createElement(SDK_VERSION_NAME, height)
        val sdkVersionCode = createElement(SDK_VERSION_CODE, height)
        val systemVersionName = createElement(SYSTEM_VERSION_NAME, height)
        val androidId = createElement(ANDROID_ID, height)
        val macAddress = createElement(MAC_ADDRESS, height)
        val manuFacturer = createElement(MANU_FACTURER, height)
        val model = createElement(MODEL, height)
        val abis = createElement(ABIS, height)
        val isTablet = createElement(IS_TABLET, height)
        val isEmulator = createElement(IS_EMULATOR, height)
        val uniqueDeviceId = createElement(UNIQUE_DEVICE_ID, height)

        val size = QMUIDisplayHelper.dp2px(context, 20)
        QMUIGroupListView.newSection(requireContext())
            .setTitle("设备信息")
            .setDescription("")
            .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
            .addItemView(deviceRootMode, onClickListener)
            .addItemView(deviceAdbMode, onClickListener)
            .addItemView(sdkVersionName, onClickListener)
            .addItemView(sdkVersionCode, onClickListener)
            .addItemView(systemVersionName, onClickListener)
            .addItemView(androidId, onClickListener)
            .addItemView(macAddress, onClickListener)
            .addItemView(manuFacturer, onClickListener)
            .addItemView(model, onClickListener)
            .addItemView(abis, onClickListener)
            .addItemView(isTablet, onClickListener)
            .addItemView(isEmulator, onClickListener)
            .addItemView(uniqueDeviceId, onClickListener)
            .setShowSeparator(true)
            .addTo(mGroupListView)

        aboutViewModel.deviceRootMode.observe(viewLifecycleOwner) { text: String? ->
            updateElement(deviceRootMode, text)
        }
        aboutViewModel.deviceAdbMode.observe(viewLifecycleOwner) { text: String? ->
            updateElement(deviceAdbMode, text)
        }
        aboutViewModel.sdkVersionName.observe(viewLifecycleOwner) { text: String? ->
            updateElement(sdkVersionName, text)
        }
        aboutViewModel.sdkVersionCode.observe(viewLifecycleOwner) { text: String? ->
            updateElement(sdkVersionCode, text)
        }
        aboutViewModel.systemVersionName.observe(viewLifecycleOwner) { text: String? ->
            updateElement(systemVersionName, text)
        }
        aboutViewModel.androidId.observe(viewLifecycleOwner) { text: String? ->
            updateElement(androidId, text)
        }
        aboutViewModel.macAddress.observe(viewLifecycleOwner) { text: String? ->
            updateElement(macAddress, text)
        }
        aboutViewModel.manuFacturer.observe(viewLifecycleOwner) { text: String? ->
            updateElement(manuFacturer, text)
        }
        aboutViewModel.model.observe(viewLifecycleOwner) { text: String? ->
            updateElement(model, text)
        }
        aboutViewModel.abis.observe(viewLifecycleOwner) { text: String? ->
            updateElement(abis, text)
        }
        aboutViewModel.isTablet.observe(viewLifecycleOwner) { text: String? ->
            updateElement(isTablet, text)
        }
        aboutViewModel.isEmulator.observe(viewLifecycleOwner) { text: String? ->
            updateElement(isEmulator, text)
        }
        aboutViewModel.uniqueDeviceId.observe(viewLifecycleOwner) { text: String? ->
            updateElement(uniqueDeviceId, text)
        }

    }

    private fun initAppInfoSection(aboutViewModel: AboutViewModel, height: Int) {
        val appName = createElement(APP_NAME, height)
        val appPackageName = createElement(APP_PACKAGE_NAME, height)
        val appVersionName = createElement(APP_VERSION_NAME, height)
        val appVersionCode = createElement(APP_VERSION_CODE, height)
        val appBuildInfo = createElement(APP_BUILD_INFO, height)
        val appPathName = createElement(APP_PATH_NAME, height)
        val appRootMode = createElement(APP_ROOT_MODE, height)
        val appDebugMode = createElement(APP_DEBUG_MODE, height)
        val systemAppMode = createElement(SYSTEM_APP_MODE, height)
        val appSignatureNameSHA1 = createElement(APP_SIGNATURE_NAME_SHA1, height)
        val appSignatureNameSHA256 = createElement(APP_SIGNATURE_NAME_SHA256, height)
        val appSignatureNameMD5 = createElement(APP_SIGNATURE_NAME_MD5, height)

        val size = QMUIDisplayHelper.dp2px(context, 20)
        QMUIGroupListView.newSection(requireContext())
            .setTitle("App信息")
            .setDescription("")
            .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
            .addItemView(appName, onClickListener)
            .addItemView(appPackageName, onClickListener)
            .addItemView(
                appVersionName,
                if (SpUtils.get(context, ENABLE_SHOW_LAB_SP, false) as Boolean) {
                    onClickListener
                } else {
                    onMultiClickListener()
                }
            )
            .addItemView(appVersionCode, onClickListener)
            .addItemView(appBuildInfo, onClickListener)
            .addItemView(appPathName, onClickListener)
            .addItemView(appRootMode, onClickListener)
            .addItemView(appDebugMode, onClickListener)
            .addItemView(systemAppMode, onClickListener)
            .addItemView(appSignatureNameSHA1, onClickListener)
            .addItemView(appSignatureNameSHA256, onClickListener)
            .addItemView(appSignatureNameMD5, onClickListener)
            .setShowSeparator(true)
            .addTo(mGroupListView)

        aboutViewModel.appName.observe(viewLifecycleOwner) { text: String? ->
            updateElement(appName, text)
        }
        aboutViewModel.appPackageName.observe(viewLifecycleOwner) { text: String? ->
            updateElement(appPackageName, text)
        }
        aboutViewModel.appVersionName.observe(viewLifecycleOwner) { text: String? ->
            updateElement(appVersionName, text)
        }
        aboutViewModel.appVersionCode.observe(viewLifecycleOwner) { text: String? ->
            updateElement(appVersionCode, text)
        }
        aboutViewModel.appBuildInfo.observe(viewLifecycleOwner) { text: String? ->
            updateElement(appBuildInfo, text)
        }
        aboutViewModel.appPathName.observe(viewLifecycleOwner) { text: String? ->
            updateElement(appPathName, text)
        }
        aboutViewModel.appRootMode.observe(viewLifecycleOwner) { text: String? ->
            updateElement(appRootMode, text)
        }
        aboutViewModel.appDebugMode.observe(viewLifecycleOwner) { text: String? ->
            updateElement(appDebugMode, text)
        }
        aboutViewModel.systemAppMode.observe(viewLifecycleOwner) { text: String? ->
            updateElement(systemAppMode, text)
        }
        aboutViewModel.appSignatureNameSHA1.observe(viewLifecycleOwner) { text: String? ->
            updateElement(appSignatureNameSHA1, text)
        }
        aboutViewModel.appSignatureNameSHA256.observe(viewLifecycleOwner) { text: String? ->
            updateElement(appSignatureNameSHA256, text)
        }
        aboutViewModel.appSignatureNameMD5.observe(viewLifecycleOwner) { text: String? ->
            updateElement(appSignatureNameMD5, text)
        }
    }

    private fun initServerInfo(aboutViewModel: AboutViewModel, height: Int) {
        val serverHost = createElement(SERVER_HOST, height)
        val serverEnv = createElement(SERVER_ENV, height)
        val serverVersion = createElement(SERVER_VERSION, height)
        val serverBuildTime = createElement(SERVER_BUILD_TIME, height)

        val size = QMUIDisplayHelper.dp2px(context, 20)
        QMUIGroupListView.newSection(requireContext())
            .setTitle("Server信息")
            .setDescription("")
            .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
            .addItemView(serverHost, onClickListener)
            .addItemView(serverEnv, onClickListener)
            .addItemView(serverVersion, onClickListener)
            .addItemView(serverBuildTime, onClickListener)
            .setShowSeparator(true)
            .addTo(mGroupListView)

        aboutViewModel.serverHost.observe(viewLifecycleOwner) { text: String? ->
            updateElement(serverHost, text)
        }

        aboutViewModel.serverEnv.observe(viewLifecycleOwner) { text: String? ->
            updateElement(serverEnv, text)
        }

        aboutViewModel.serverVersion.observe(viewLifecycleOwner) { text: String? ->
            updateElement(serverVersion, text)
        }

        aboutViewModel.serverBuildTime.observe(viewLifecycleOwner) { text: String? ->
            updateElement(serverBuildTime, text)
        }
    }

    private fun createElement(name: String, minHeight: Int): QMUICommonListItemView {
        val element = mGroupListView.createItemView(
            null,
            name,
            na,
            QMUICommonListItemView.HORIZONTAL,
            QMUICommonListItemView.ACCESSORY_TYPE_NONE
        )
        element?.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
        element?.minHeight = minHeight
        element?.detailTextView?.gravity = GravityCompat.END
        element?.detailTextView?.setPadding(0, 12, 0, 12)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            element?.detailTextView?.breakStrategy = LineBreaker.BREAK_STRATEGY_BALANCED
        }
        return element
    }

    private fun updateElement(element: QMUICommonListItemView?, text: String?) {
        element?.detailText = text
    }
}