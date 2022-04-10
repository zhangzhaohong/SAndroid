package com.tristana.sandroid.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.util.QMUIResHelper
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView
import com.tristana.sandroid.R
import com.tristana.sandroid.model.data.DataModel
import com.tristana.CustomViewWithToolsLibrary.tools.sharedPreferences.SpUtils

/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {
    private lateinit var mGroupListView: QMUIGroupListView

    private var onClickListener = View.OnClickListener { v ->
        if (v is QMUICommonListItemView) {
            val text = v.text
            Toast.makeText(activity, "$text is Clicked", Toast.LENGTH_SHORT).show()
            if (v.accessoryType == QMUICommonListItemView.ACCESSORY_TYPE_SWITCH) {
                v.switch.toggle()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_setting, container, false)
        mGroupListView = root.findViewById(R.id.groupListView)
        val height = QMUIResHelper.getAttrDimen(context, com.qmuiteam.qmui.R.attr.qmui_list_item_height)
        val x5DebugItem = mGroupListView.createItemView("打印 X5 debug日志")
        x5DebugItem.accessoryType = QMUICommonListItemView.ACCESSORY_TYPE_SWITCH
        x5DebugItem.switch.isChecked =
            SpUtils.get(context, DataModel.X5_DEBUG_MODE, false) as Boolean;
        x5DebugItem.switch.setOnCheckedChangeListener { _, isChecked -> SpUtils.put(context, DataModel.X5_DEBUG_MODE, isChecked) }
        val item: QMUICommonListItemView = mGroupListView.createItemView(
                null,
                "红点显示在右边",
                "在右方的详细信息",
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON,
                height)
        item.setTipPosition(QMUICommonListItemView.TIP_POSITION_RIGHT)
        item.showRedDot(true)
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
                .addItemView(item, onClickListener)
                .setOnlyShowStartEndSeparator(true)
                .addTo(mGroupListView)
        return root
    }

}