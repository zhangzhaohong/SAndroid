package com.tristana.sandroid.epoxy.manager

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

/**
 * @author koala
 * @date 2022/4/19 11:05
 * @version 1.0
 * @description
 */
class QuickScrollLinearLayoutManager(
    context: Context,
    orientation: Int,
    reverseLayout: Boolean,
    var millisecondsPerInch: Float? = MILLISECONDS_PER_INCH
) :
    LinearLayoutManager(
        context,
        orientation,
        reverseLayout
    ) {
    companion object {
        const val MILLISECONDS_PER_INCH = 3f
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State?,
        position: Int
    ) {
        val linearSmoothScroller: LinearSmoothScroller =
            object : LinearSmoothScroller(recyclerView.context) {

                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                    millisecondsPerInch?.let {
                        return it / displayMetrics.densityDpi
                    }?: kotlin.run {
                        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
                    }

                }
            }
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }
}