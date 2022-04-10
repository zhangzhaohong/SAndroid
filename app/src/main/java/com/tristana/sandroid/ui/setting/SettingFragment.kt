package com.tristana.sandroid.ui.setting

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.AppUtils
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.util.QMUIResHelper
import com.qmuiteam.qmui.widget.dialog.QMUIDialog.EditTextDialogBuilder
import com.qmuiteam.qmui.widget.dialog.QMUIDialog.MessageDialogBuilder
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView
import com.tristana.customViewWithToolsLibrary.tools.sharedPreferences.SpUtils
import com.tristana.sandroid.R
import com.tristana.sandroid.model.data.DataModel
import com.tristana.sandroid.model.data.DataModel.LOG_FILE_PREFIX_SP
import com.tristana.sandroid.model.data.SettingModel.*


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
                X5_PRINT_DEBUG_INFO -> {}
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
                            .addAction("保存修改") { dialog, index ->
                                val input: CharSequence? = builder.editText.text
                                if (input.isNullOrEmpty()) {
                                    dialog.dismiss()
                                    Toast.makeText(activity, "保存失败", Toast.LENGTH_SHORT).show()
                                } else {
                                    SpUtils.put(context, LOG_FILE_PREFIX_SP, input)
                                    dialog.dismiss()
                                    needRestart()
                                }
                            }
                            .show()
                    }
                }
                LOG_2_LOCAL -> {}
                else -> {
                    Toast.makeText(activity, "$text is Clicked", Toast.LENGTH_SHORT).show()
                }
            }
            if (v.accessoryType == QMUICommonListItemView.ACCESSORY_TYPE_SWITCH) {
                v.switch.toggle()
            }
        }
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

        val x5DebugItem = mGroupListView.createItemView(X5_PRINT_DEBUG_INFO)
        x5DebugItem.accessoryType = QMUICommonListItemView.ACCESSORY_TYPE_SWITCH
        x5DebugItem.switch.isChecked =
            SpUtils.get(context, DataModel.X5_DEBUG_MODE_SP, false) as Boolean;
        x5DebugItem.switch.setOnCheckedChangeListener { _, isChecked ->
            SpUtils.put(
                context,
                DataModel.X5_DEBUG_MODE_SP,
                isChecked
            )
        }

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
            SpUtils.get(context, DataModel.LOG_SAVE_2_LOCAL_SP, false) as Boolean;
        log2Local.switch.setOnCheckedChangeListener { _, isChecked ->
            run {
                SpUtils.put(context, DataModel.LOG_SAVE_2_LOCAL_SP, isChecked)
                needRestart()
            }
        }

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
            .addItemView(x5DebugItem, onClickListener)
            .addItemView(redPointItem, onClickListener)
            .setOnlyShowStartEndSeparator(true)
            .addTo(mGroupListView)
        QMUIGroupListView.newSection(requireContext())
            .setTitle("日志设置")
            .setDescription("")
            .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
            .addItemView(logFilePrefix, onClickListener)
            .addItemView(log2Local, onClickListener)
            .setOnlyShowStartEndSeparator(true)
            .addTo(mGroupListView)
        return root
    }

    private fun needRestart() {
        MessageDialogBuilder(activity)
            .setTitle("提示")
            .setMessage("设置已保存，重启App生效")
            .addAction(
                "稍后重启"
            ) { dialog, _ -> dialog.dismiss() }
            .addAction(
                "立即重启"
            ) { dialog, _ ->
                run {
                    dialog.dismiss()
                    AppUtils.relaunchApp(true)
                }
            }
            .show()
    }

}