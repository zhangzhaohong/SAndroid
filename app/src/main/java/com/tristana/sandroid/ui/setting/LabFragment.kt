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
import com.blankj.utilcode.util.AppUtils
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.therouter.router.Route
import com.therouter.TheRouter
import com.qmuiteam.qmui.util.QMUIResHelper
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView
import com.tristana.library.tools.sharedPreferences.SpUtils
import com.tristana.sandroid.R
import com.tristana.sandroid.model.data.DataModel
import com.tristana.sandroid.model.data.DataModel.ROUTER_DEBUG_PATH_SP
import com.tristana.sandroid.model.data.DataModel.ROUTER_DEBUG_STATUS_SP
import com.tristana.sandroid.model.data.DataModel.ROUTER_WEB_VIEW_DEBUG_PATH_SP
import com.tristana.sandroid.model.data.SettingModel.ROUTER_CUSTOM_PATH
import com.tristana.sandroid.model.data.SettingModel.ROUTER_WEB_VIEW_CUSTOM_PATH
import com.tristana.sandroid.model.data.SettingModel.ROUTER_DEBUG_STATUS
import com.tristana.sandroid.model.data.SettingModel.ROUTER_WEB_VIEW_ACTIVITY
import com.tristana.sandroid.ui.router.RouterWebActivity
import utils.router.RouterUtils

/**
 * @author koala
 * @date 2023/6/1 21:53
 * @version 1.0
 * @description
 */
@Route(path = LabFragment.ROUTE)
class LabFragment : Fragment() {
    companion object {
        const val ROUTE = "/app/settings/lab"
    }

    private lateinit var mGroupListView: QMUIGroupListView
    private var na: String = "N/A"

    private var onClickListener = View.OnClickListener { v ->
        if (v is QMUICommonListItemView) {
            when (val text = v.text) {
                ROUTER_DEBUG_STATUS -> {}
                ROUTER_WEB_VIEW_ACTIVITY -> {
                    RouterUtils.routeWithDirect(
                        RouterWebActivity.ROUTE,
                        "file:///android_asset/scheme-page.html"
                    )
                }

                ROUTER_WEB_VIEW_CUSTOM_PATH -> {
                    val builder = QMUIDialog.EditTextDialogBuilder(activity)
                    builder
                        .setTitle("设置Router路径")
                        .setPlaceholder("设置Router路径")
                        .setInputType(InputType.TYPE_CLASS_TEXT)
                        .setDefaultText(
                            SpUtils.get(
                                context,
                                DataModel.ROUTER_WEB_VIEW_DEBUG_PATH_SP,
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
                                Toast.makeText(activity, "非法的Router路径", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                SpUtils.put(context, DataModel.ROUTER_WEB_VIEW_DEBUG_PATH_SP, input)
                                dialog.dismiss()
                                RouterUtils.routeWithDirect(
                                    RouterWebActivity.ROUTE,
                                    input.toString()
                                )
                            }
                            v.detailText = input
                        }
                        .show()
                }

                ROUTER_CUSTOM_PATH -> {
                    val builder = QMUIDialog.EditTextDialogBuilder(activity)
                    builder
                        .setTitle("设置Router路径")
                        .setPlaceholder("设置Router路径")
                        .setInputType(InputType.TYPE_CLASS_TEXT)
                        .setDefaultText(
                            SpUtils.get(
                                context,
                                DataModel.ROUTER_DEBUG_PATH_SP,
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
                                Toast.makeText(activity, "非法的Router路径", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                SpUtils.put(context, DataModel.ROUTER_DEBUG_PATH_SP, input)
                                dialog.dismiss()
                                RouterUtils.route(input.toString())
                            }
                            v.detailText = input
                        }
                        .show()
                }
            }
        }
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
        val root = inflater.inflate(R.layout.fragment_lab, container, false)
        mGroupListView = root.findViewById(R.id.groupListView)
        val height =
            QMUIResHelper.getAttrDimen(context, com.qmuiteam.qmui.R.attr.qmui_list_item_height)
        val routerDebugStatus =
            createSwitchElement(ROUTER_DEBUG_STATUS, height, ROUTER_DEBUG_STATUS_SP)
        val routerWebActivity = createElement(ROUTER_WEB_VIEW_ACTIVITY, height, false)
        val routerWebViewCustomPath = createTextElement(ROUTER_WEB_VIEW_CUSTOM_PATH, height, false)
        routerWebViewCustomPath.detailText =
            SpUtils.get(context, ROUTER_WEB_VIEW_DEBUG_PATH_SP, "") as String
        val routerCustomPath = createTextElement(ROUTER_CUSTOM_PATH, height, false)
        routerCustomPath.detailText = SpUtils.get(context, ROUTER_DEBUG_PATH_SP, "") as String
        val size = QMUIDisplayHelper.dp2px(context, 20)
        QMUIGroupListView.newSection(context)
            .setTitle("组件设置")
            .setDescription("")
            .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
            .setMiddleSeparatorInset(QMUIDisplayHelper.dp2px(context, 16), 0)
            .addTo(mGroupListView)
        QMUIGroupListView.newSection(requireContext())
            .setTitle("Router设置")
            .setDescription("")
            .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
            .addItemView(routerDebugStatus, onClickListener)
            .addItemView(routerWebActivity, onClickListener)
            .addItemView(routerWebViewCustomPath, onClickListener)
            .addItemView(routerCustomPath, onClickListener)
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

    private fun needRestart(later: Boolean = true) {
        val builder = QMUIDialog.MessageDialogBuilder(activity)
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