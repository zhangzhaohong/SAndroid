package com.tristana.sandroid.ui.setting

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.AppUtils
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.util.QMUIResHelper
import com.qmuiteam.qmui.widget.dialog.QMUIDialog.EditTextDialogBuilder
import com.qmuiteam.qmui.widget.dialog.QMUIDialog.MessageDialogBuilder
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView
import com.tencent.smtt.sdk.TbsVideo
import com.tristana.customViewWithToolsLibrary.tools.sharedPreferences.SpUtils
import com.tristana.sandroid.R
import com.tristana.sandroid.model.data.DataModel.*
import com.tristana.sandroid.model.data.SettingModel.*
import com.tristana.sandroid.ui.webView.X5WebViewFragment


/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {
    private lateinit var mGroupListView: QMUIGroupListView

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
        val fragmentManager: FragmentManager = parentFragmentManager
        val fragment: Fragment = X5WebViewFragment()
        fragmentManager.beginTransaction().add(requireParentFragment().requireView().id, fragment)
            .addToBackStack(null).commit()
        val bundle = Bundle()
        bundle.putString("url", url)
        fragment.arguments = bundle
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_setting, container, false)
        mGroupListView = root.findViewById(R.id.settings_group_list_view)
        val height =
            QMUIResHelper.getAttrDimen(context, com.qmuiteam.qmui.R.attr.qmui_list_item_height)

        val x5AllowThirdPartApp = mGroupListView.createItemView(X5_ALLOW_THIRD_PART_APP)
        x5AllowThirdPartApp.accessoryType = QMUICommonListItemView.ACCESSORY_TYPE_SWITCH
        x5AllowThirdPartApp.switch.isChecked =
            SpUtils.get(context, X5_ALLOW_THIRD_PART_APP_SP, false) as Boolean;
        x5AllowThirdPartApp.switch.setOnCheckedChangeListener { _, isChecked ->
            SpUtils.put(
                context,
                X5_ALLOW_THIRD_PART_APP_SP,
                isChecked
            )
        }

        val x5DebugItem = mGroupListView.createItemView(X5_PRINT_DEBUG_INFO)
        x5DebugItem.accessoryType = QMUICommonListItemView.ACCESSORY_TYPE_SWITCH
        x5DebugItem.switch.isChecked =
            SpUtils.get(context, X5_DEBUG_MODE_SP, false) as Boolean;
        x5DebugItem.switch.setOnCheckedChangeListener { _, isChecked ->
            SpUtils.put(
                context,
                X5_DEBUG_MODE_SP,
                isChecked
            )
        }

        val x5Debug: QMUICommonListItemView = mGroupListView.createItemView(
            null,
            X5_DEBUG,
            null,
            QMUICommonListItemView.HORIZONTAL,
            QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON,
            height
        )
        x5Debug.showRedDot(false)

        val x5TbsDebug: QMUICommonListItemView = mGroupListView.createItemView(
            null,
            X5_TBS_DEBUG,
            null,
            QMUICommonListItemView.HORIZONTAL,
            QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON,
            height
        )
        x5TbsDebug.showRedDot(false)

        val x5VideoDebug: QMUICommonListItemView = mGroupListView.createItemView(
            null,
            X5_VIDEO_DEBUG,
            SpUtils.get(context, X5_VIDEO_DEBUG_SP, "") as String,
            QMUICommonListItemView.HORIZONTAL,
            QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON,
            height
        )
        x5VideoDebug.setTipPosition(QMUICommonListItemView.TIP_POSITION_RIGHT)
        x5VideoDebug.showRedDot(false)

        val redPointItem: QMUICommonListItemView = mGroupListView.createItemView(
            null,
            "红点显示在右边",
            "在右方的详细信息",
            QMUICommonListItemView.HORIZONTAL,
            QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON,
            height
        )
        redPointItem.setTipPosition(QMUICommonListItemView.TIP_POSITION_RIGHT)
        redPointItem.showRedDot(true)

        val logger = mGroupListView.createItemView(LOGGER)
        logger.accessoryType = QMUICommonListItemView.ACCESSORY_TYPE_SWITCH
        logger.switch.isChecked =
            SpUtils.get(context, LOGGER_SP, true) as Boolean;
        logger.switch.setOnCheckedChangeListener { _, isChecked ->
            SpUtils.put(
                context,
                LOGGER_SP,
                isChecked
            )
        }

        val logFilePrefix: QMUICommonListItemView = mGroupListView.createItemView(
            null,
            LOG_FILE_PREFIX,
            SpUtils.get(context, LOG_FILE_PREFIX_SP, "AppLog") as String,
            QMUICommonListItemView.HORIZONTAL,
            QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON,
            height
        )
        logFilePrefix.setTipPosition(QMUICommonListItemView.TIP_POSITION_RIGHT)
        logFilePrefix.showRedDot(false)

        val log2Local = mGroupListView.createItemView(LOG_2_LOCAL)
        log2Local.accessoryType = QMUICommonListItemView.ACCESSORY_TYPE_SWITCH
        log2Local.switch.isChecked =
            SpUtils.get(context, LOG_SAVE_2_LOCAL_SP, false) as Boolean;
        log2Local.switch.setOnCheckedChangeListener { _, isChecked ->
            run {
                SpUtils.put(context, LOG_SAVE_2_LOCAL_SP, isChecked)
                needRestart()
            }
        }

        val logSaveDay: QMUICommonListItemView = mGroupListView.createItemView(
            null,
            LOG_SAVE_DAY,
            "${SpUtils.get(context, LOG_SAVE_DAY_SP, 3) as Int} 天",
            QMUICommonListItemView.HORIZONTAL,
            QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON,
            height
        )
        logFilePrefix.setTipPosition(QMUICommonListItemView.TIP_POSITION_RIGHT)
        logFilePrefix.showRedDot(false)

        val resetSettings: QMUICommonListItemView = mGroupListView.createItemView(
            null,
            RESET_SETTINGS,
            null,
            QMUICommonListItemView.HORIZONTAL,
            QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON,
            height
        )
        resetSettings.showRedDot(true)

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
            .addItemView(redPointItem, onClickListener)
            .setOnlyShowStartEndSeparator(true)
            .addTo(mGroupListView)
        QMUIGroupListView.newSection(requireContext())
            .setTitle("日志设置")
            .setDescription("")
            .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
            .addItemView(logger, onClickListener)
            .addItemView(logFilePrefix, onClickListener)
            .addItemView(log2Local, onClickListener)
            .addItemView(logSaveDay, onClickListener)
            .setOnlyShowStartEndSeparator(true)
            .addTo(mGroupListView)
        QMUIGroupListView.newSection(requireContext())
            .setTitle("全局设置")
            .setDescription("以下设置项请谨慎操作")
            .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
            .addItemView(resetSettings, onClickListener)
            .setOnlyShowStartEndSeparator(true)
            .addTo(mGroupListView)
        return root
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