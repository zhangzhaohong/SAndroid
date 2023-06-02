package com.tristana.sandroid.ui.setting

import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.therouter.router.Route
import com.therouter.TheRouter
import com.blankj.utilcode.util.*
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.util.QMUIResHelper
import com.qmuiteam.qmui.widget.dialog.QMUIDialog.EditTextDialogBuilder
import com.qmuiteam.qmui.widget.dialog.QMUIDialog.MessageDialogBuilder
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView
import com.tencent.smtt.sdk.TbsVideo
import com.tristana.library.tools.sharedPreferences.SpUtils
import com.tristana.sandroid.R
import com.tristana.sandroid.model.data.DataModel.*
import com.tristana.sandroid.model.data.SettingModel.*
import com.tristana.sandroid.ui.ad.AdWebViewFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@Route(path = SettingFragment.ROUTE)
class SettingFragment : Fragment() {

    companion object {
        const val ROUTE = "/app/settings"
    }

    private lateinit var mGroupListView: QMUIGroupListView
    private var na: String = "N/A"

    private var onClickListener = View.OnClickListener { v ->
        if (v is QMUICommonListItemView) {
            when (val text = v.text) {
                X5_ALLOW_THIRD_PART_APP -> {}
                X5_PRINT_DEBUG_INFO -> {}
                X5_DEBUG -> {
                    jumpToBrowser("http://debugx5.qq.com")
                }
                X5_TBS_DEBUG -> {
                    jumpToBrowser("http://debugtbs.qq.com")
                }
                X5_VIDEO_DEBUG -> {
                    val builder = EditTextDialogBuilder(activity)
                    builder
                        .setTitle("设置视频链接")
                        .setPlaceholder("设置视频链接")
                        .setInputType(InputType.TYPE_CLASS_TEXT)
                        .setDefaultText(
                            SpUtils.get(
                                context,
                                X5_VIDEO_DEBUG_SP,
                                ""
                            ) as String
                        )
                        .addAction(
                            "取消"
                        ) { dialog, _ -> dialog.dismiss() }
                        .addAction("确定") { dialog, _ ->
                            val input: CharSequence? = builder.editText.text
                            if (input.isNullOrEmpty()) {
                                dialog.dismiss()
                                Toast.makeText(activity, "非法的视频链接", Toast.LENGTH_SHORT).show()
                            } else {
                                SpUtils.put(context, X5_VIDEO_DEBUG_SP, input)
                                dialog.dismiss()
                                val bundle = Bundle()
                                bundle.putInt("screenMode", 102)
                                TbsVideo.openVideo(requireContext(), input.toString(), bundle)
                            }
                            v.detailText = input
                        }
                        .show()
                }
                LOGGER -> {}
                LOG_FILE_PREFIX -> {
                    run {
                        val builder = EditTextDialogBuilder(activity)
                        builder
                            .setTitle("设置$LOG_FILE_PREFIX")
                            .setPlaceholder("在此输入$LOG_FILE_PREFIX")
                            .setInputType(InputType.TYPE_CLASS_TEXT)
                            .setDefaultText(
                                SpUtils.get(
                                    context,
                                    LOG_FILE_PREFIX_SP,
                                    "AppLog"
                                ) as String
                            )
                            .addAction(
                                "取消"
                            ) { dialog, _ -> dialog.dismiss() }
                            .addAction("保存修改") { dialog, _ ->
                                // val input: CharSequence? = builder.editText.text
                                val input: CharSequence? = builder.editText.text
                                if (input.isNullOrEmpty()) {
                                    dialog.dismiss()
                                    Toast.makeText(activity, "保存失败", Toast.LENGTH_SHORT).show()
                                } else {
                                    SpUtils.put(context, LOG_FILE_PREFIX_SP, input)
                                    dialog.dismiss()
                                    needRestart()
                                }
                                v.detailText = input
                            }
                            .show()
                    }
                }
                LOG_2_LOCAL -> {}
                LOG_SAVE_DAY -> {
                    val builder = EditTextDialogBuilder(activity)
                    builder
                        .setTitle("设置$LOG_SAVE_DAY")
                        .setPlaceholder("在此输入$LOG_SAVE_DAY")
                        .setInputType(InputType.TYPE_CLASS_NUMBER)
                        .setDefaultText(
                            "${
                                SpUtils.get(
                                    context,
                                    LOG_SAVE_DAY_SP,
                                    3
                                ) as Int
                            }"
                        )
                        .addAction(
                            "取消"
                        ) { dialog, _ -> dialog.dismiss() }
                        .addAction("保存修改") { dialog, _ ->
                            val input: CharSequence? = builder.editText.text
                            if (input.isNullOrEmpty()) {
                                SpUtils.put(context, LOG_SAVE_DAY_SP, (-1))
                            } else {
                                SpUtils.put(context, LOG_SAVE_DAY_SP, input.toString().toInt())
                            }
                            v.detailText = "$input 天"
                            dialog.dismiss()
                            needRestart()
                        }
                        .show()
                }
                LOG_LOCAL_SIZE -> {
                    MessageDialogBuilder(activity)
                        .setTitle("提示")
                        .setMessage("是否清空本地日志目录？")
                        .addAction(
                            "取消"
                        ) { dialog, _ -> dialog.dismiss() }
                        .addAction(
                            "确定"
                        ) { dialog, _ ->
                            run {
                                dialog.dismiss()
                                FileUtils.deleteAllInDir(LogUtils.getConfig().dir)
                                refreshLogLocalSize(v)
                                ToastUtils.showLong("清空目录成功")
                            }
                        }
                        .show()
                }
                MAX_DOWNLOAD_CONCURRENT_LIMIT -> {
                    val builder = EditTextDialogBuilder(activity)
                    builder
                        .setTitle("设置$MAX_DOWNLOAD_CONCURRENT_LIMIT")
                        .setPlaceholder("在此输入$MAX_DOWNLOAD_CONCURRENT_LIMIT")
                        .setInputType(InputType.TYPE_CLASS_NUMBER)
                        .setDefaultText(
                            "${
                                SpUtils.get(
                                    context,
                                    MAX_DOWNLOAD_CONCURRENT_LIMIT_SP,
                                    3
                                ) as Int
                            }"
                        )
                        .addAction(
                            "取消"
                        ) { dialog, _ -> dialog.dismiss() }
                        .addAction("保存修改") { dialog, _ ->
                            val input: CharSequence? = builder.editText.text
                            if (input.isNullOrEmpty()) {
                                SpUtils.put(context, MAX_DOWNLOAD_CONCURRENT_LIMIT_SP, (3))
                            } else {
                                SpUtils.put(context, MAX_DOWNLOAD_CONCURRENT_LIMIT_SP, input.toString().toInt())
                            }
                            v.detailText = "$input 个"
                            dialog.dismiss()
                            needRestart()
                        }
                        .show()
                }
                DOWNLOAD_PROGRESS_REPORTING_INTERVAL -> {
                    val builder = EditTextDialogBuilder(activity)
                    builder
                        .setTitle("设置$DOWNLOAD_PROGRESS_REPORTING_INTERVAL")
                        .setPlaceholder("在此输入$DOWNLOAD_PROGRESS_REPORTING_INTERVAL")
                        .setInputType(InputType.TYPE_CLASS_NUMBER)
                        .setDefaultText(
                            "${
                                SpUtils.get(
                                    context,
                                    DOWNLOAD_PROGRESS_REPORTING_INTERVAL_SP,
                                    1000L
                                ) as Long
                            }"
                        )
                        .addAction(
                            "取消"
                        ) { dialog, _ -> dialog.dismiss() }
                        .addAction("保存修改") { dialog, _ ->
                            val input: CharSequence? = builder.editText.text
                            if (input.isNullOrEmpty()) {
                                SpUtils.put(context, DOWNLOAD_PROGRESS_REPORTING_INTERVAL_SP, (1000L))
                            } else {
                                SpUtils.put(context, DOWNLOAD_PROGRESS_REPORTING_INTERVAL_SP, input.toString().toLong())
                            }
                            v.detailText = "$input ms"
                            dialog.dismiss()
                            needRestart()
                        }
                        .show()
                }
                DOWNLOAD_AUTO_START -> {}
                DOWNLOAD_LOCAL_SIZE -> {
                    MessageDialogBuilder(activity)
                        .setTitle("提示")
                        .setMessage("是否清空本地下载目录？")
                        .addAction(
                            "取消"
                        ) { dialog, _ -> dialog.dismiss() }
                        .addAction(
                            "确定"
                        ) { dialog, _ ->
                            run {
                                dialog.dismiss()
                                FileUtils.deleteAllInDir(requireContext().getExternalFilesDir("download")?.absolutePath)
                                refreshDownloadLocalSize(v)
                                ToastUtils.showLong("清空目录成功")
                            }
                        }
                        .show()
                }
                RESET_SETTINGS -> {
                    MessageDialogBuilder(activity)
                        .setTitle("提示")
                        .setMessage("是否重置全部设置？\n该操作不可逆")
                        .addAction(
                            "取消"
                        ) { dialog, _ -> dialog.dismiss() }
                        .addAction(
                            "立即重置"
                        ) { dialog, _ ->
                            run {
                                dialog.dismiss()
                                SpUtils.clear(requireContext())
                                needRestart(false)
                            }
                        }
                        .show()

                }
                else -> {
                    Toast.makeText(activity, "$text is Clicked", Toast.LENGTH_SHORT).show()
                }
            }
            if (v.accessoryType == QMUICommonListItemView.ACCESSORY_TYPE_SWITCH) {
                v.switch.toggle()
            }
        }
    }

    private fun jumpToBrowser(url: String) {
        val bundle = Bundle()
        bundle.putString("url", url)
        val fragment = AdWebViewFragment()
        fragment.arguments = bundle
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.add(requireParentFragment().requireView().id, fragment)
        fragmentTransaction.addToBackStack(requireParentFragment().tag)
        fragmentTransaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TheRouter.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_setting, container, false)
        mGroupListView = root.findViewById(R.id.groupListView)
        val height =
            QMUIResHelper.getAttrDimen(context, com.qmuiteam.qmui.R.attr.qmui_list_item_height)

        val x5AllowThirdPartApp =
            createSwitchElement(X5_ALLOW_THIRD_PART_APP, height, X5_ALLOW_THIRD_PART_APP_SP)
        val x5DebugItem = createSwitchElement(X5_PRINT_DEBUG_INFO, height, X5_DEBUG_MODE_SP)
        val x5Debug: QMUICommonListItemView = createElement(X5_DEBUG, height)
        val x5TbsDebug: QMUICommonListItemView = createElement(X5_TBS_DEBUG, height)
        val x5VideoDebug: QMUICommonListItemView = createTextElement(X5_VIDEO_DEBUG, height)
        x5VideoDebug.detailText = SpUtils.get(context, X5_VIDEO_DEBUG_SP, "") as String
        val logger = createSwitchElement(LOGGER, height, LOGGER_SP, true, needRestart = true)
        val logFilePrefix: QMUICommonListItemView = createTextElement(LOG_FILE_PREFIX, height)
        logFilePrefix.detailText = SpUtils.get(context, LOG_FILE_PREFIX_SP, "AppLog") as String
        val log2Local = createSwitchElement(LOG_2_LOCAL, height, LOG_SAVE_2_LOCAL_SP, needRestart = true)
        val logSaveDay: QMUICommonListItemView = createTextElement(LOG_SAVE_DAY, height)
        logSaveDay.detailText = "${SpUtils.get(context, LOG_SAVE_DAY_SP, 3) as Int} 天"
        val logLocalSize: QMUICommonListItemView = createTextElement(LOG_LOCAL_SIZE, height)
        val resetSettings: QMUICommonListItemView = createElement(RESET_SETTINGS, height, true)
        val maxDownloadConcurrent: QMUICommonListItemView = createTextElement(MAX_DOWNLOAD_CONCURRENT_LIMIT, height)
        maxDownloadConcurrent.detailText = "${SpUtils.get(context, MAX_DOWNLOAD_CONCURRENT_LIMIT_SP, 3) as Int} 个"
        val downloadProgressReportingInterval: QMUICommonListItemView = createTextElement(DOWNLOAD_PROGRESS_REPORTING_INTERVAL, height)
        downloadProgressReportingInterval.detailText = "${SpUtils.get(context, DOWNLOAD_PROGRESS_REPORTING_INTERVAL_SP, 1000L) as Long} ms"
        val downloadLocalSize: QMUICommonListItemView = createTextElement(DOWNLOAD_LOCAL_SIZE, height)
        val downloadAutoStart = createSwitchElement(DOWNLOAD_AUTO_START, height, DOWNLOAD_AUTO_START_SP, true, needRestart = true)
        refreshLogLocalSize(logLocalSize)
        refreshDownloadLocalSize(downloadLocalSize)

        val size = QMUIDisplayHelper.dp2px(context, 20)
        QMUIGroupListView.newSection(context)
            .setTitle("基础设置")
            .setDescription("")
            .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
            .setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(context, 16), 0)
            .addTo(mGroupListView)
        QMUIGroupListView.newSection(requireContext())
            .setTitle("X5设置")
            .setDescription("")
            .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
            .addItemView(x5AllowThirdPartApp, onClickListener)
            .addItemView(x5DebugItem, onClickListener)
            .addItemView(x5Debug, onClickListener)
            .addItemView(x5TbsDebug, onClickListener)
            .addItemView(x5VideoDebug, onClickListener)
            .setShowSeparator(true)
            .addTo(mGroupListView)
        QMUIGroupListView.newSection(requireContext())
            .setTitle("日志设置")
            .setDescription("")
            .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
            .addItemView(logger, onClickListener)
            .addItemView(logFilePrefix, onClickListener)
            .addItemView(log2Local, onClickListener)
            .addItemView(logSaveDay, onClickListener)
            .addItemView(logLocalSize, onClickListener)
            .setShowSeparator(true)
            .addTo(mGroupListView)
        QMUIGroupListView.newSection(requireContext())
            .setTitle("下载设置")
            .setDescription("")
            .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
            .addItemView(maxDownloadConcurrent, onClickListener)
            .addItemView(downloadProgressReportingInterval, onClickListener)
            .addItemView(downloadAutoStart, onClickListener)
            .addItemView(downloadLocalSize, onClickListener)
            .setShowSeparator(true)
            .addTo(mGroupListView)
        QMUIGroupListView.newSection(requireContext())
            .setTitle("全局设置")
            .setDescription("以下设置项请谨慎操作")
            .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
            .addItemView(resetSettings, onClickListener)
            .setShowSeparator(true)
            .addTo(mGroupListView)
        return root
    }

    private fun createElement(
        name: String,
        minHeight: Int,
        showRedDot: Boolean = false
    ): QMUICommonListItemView {
        val element = mGroupListView.createItemView(
            null,
            name,
            null,
            QMUICommonListItemView.HORIZONTAL,
            QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON
        )
        element.showRedDot(showRedDot)
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

    private fun createTextElement(
        name: String,
        minHeight: Int,
        showRedDot: Boolean = false
    ): QMUICommonListItemView {
        val element = mGroupListView.createItemView(
            null,
            name,
            na,
            QMUICommonListItemView.HORIZONTAL,
            QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON
        )
        element.showRedDot(showRedDot)
        element?.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
        element?.minHeight = minHeight
        element?.detailTextView?.gravity = GravityCompat.END
        element?.detailTextView?.setPadding(0, 12, 0, 12)
        element?.detailTextView?.maxLines = 5
        element?.detailTextView?.ellipsize = TextUtils.TruncateAt.END
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            element?.detailTextView?.breakStrategy = LineBreaker.BREAK_STRATEGY_BALANCED
        }
        return element
    }


    private fun createSwitchElement(
        name: String,
        minHeight: Int,
        spName: String,
        defaultValue: Boolean = false,
        needRestart: Boolean = false
    ): QMUICommonListItemView {
        val element = mGroupListView.createItemView(
            null,
            name,
            null,
            QMUICommonListItemView.HORIZONTAL,
            QMUICommonListItemView.ACCESSORY_TYPE_SWITCH
        )
        element.switch.isChecked =
            SpUtils.get(context, spName, defaultValue) as Boolean;
        element.switch.setOnCheckedChangeListener { _, isChecked ->
            SpUtils.put(
                context,
                spName,
                isChecked
            )
            if (needRestart) {
                needRestart()
            }
        }
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

    private fun refreshLogLocalSize(item: QMUICommonListItemView) {
        MainScope().launch {
            var folderSize: String?
            withContext(Dispatchers.IO) {
                folderSize = FileUtils.getSize(LogUtils.getConfig().dir)
            }
            withContext(Dispatchers.Main) {
                item.detailText = folderSize
            }
        }
    }

    private fun refreshDownloadLocalSize(item: QMUICommonListItemView) {
        MainScope().launch {
            var folderSize: String?
            withContext(Dispatchers.IO) {
                folderSize = FileUtils.getSize(requireActivity().getExternalFilesDir("download")?.absolutePath)
            }
            withContext(Dispatchers.Main) {
                item.detailText = folderSize
            }
        }
    }

    private fun needRestart(later: Boolean = true) {
        val builder = MessageDialogBuilder(activity)
        builder
            .setTitle("提示")
            .setMessage("设置已保存，重启App生效")
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

}