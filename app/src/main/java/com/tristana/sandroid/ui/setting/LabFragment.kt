package com.tristana.sandroid.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.therouter.router.Route
import com.therouter.TheRouter
import com.qmuiteam.qmui.util.QMUIResHelper
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView
import com.tristana.sandroid.R

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
        return root
    }
}