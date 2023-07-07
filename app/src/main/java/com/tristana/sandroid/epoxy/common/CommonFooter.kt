package com.tristana.sandroid.epoxy.common

import android.annotation.SuppressLint
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.doOnAttach
import androidx.core.view.isVisible
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.lottie.LottieAnimationView
import com.qmuiteam.qmui.kotlin.dip
import com.tristana.sandroid.R

/**
 * @author koala
 * @date 2022/4/19 11:10
 * @version 1.0
 * @description
 */
@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.holder_common_footer)
abstract class CommonFooter : EpoxyModelWithHolder<CommonFooter.Holder>() {

    companion object {
        const val HUGE_BOTTOM_PADDING = 108F
        const val NORMAL_PADDING = 36F
        const val SMALL_PADDING = 16F
    }

    @EpoxyAttribute
    var hasMore: Boolean = false

    @EpoxyAttribute
    var bottomPadding: Float = HUGE_BOTTOM_PADDING

    @EpoxyAttribute
    var networkError: Boolean = false

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.container.doOnAttach {
            val layoutParams = holder.container.layoutParams
            if (layoutParams != null && layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                layoutParams.isFullSpan = true
            }
            holder.container.layoutParams = layoutParams
        }

        holder.container.isVisible = !networkError

        if (hasMore) {
            holder.footer.visibility = View.GONE
            holder.loadingView.visibility = View.VISIBLE
        } else {
            holder.footer.visibility = View.VISIBLE
            holder.loadingView.visibility = View.GONE
        }
        holder.container.setPadding(
            holder.footer.context.dip(16),
            holder.footer.context.dip(16),
            holder.footer.context.dip(16),
            holder.footer.context.dip(bottomPadding)
        )
    }

    override fun getSpanSize(totalSpanCount: Int, position: Int, itemCount: Int): Int {
        return totalSpanCount
    }

    class Holder : EpoxyHolder() {

        lateinit var footer: AppCompatTextView
        lateinit var loadingView: LottieAnimationView
        lateinit var container: FrameLayout

        override fun bindView(itemView: View) {
            footer = itemView.findViewById(R.id.footer)
            loadingView = itemView.findViewById(R.id.loadingView)
            container = itemView.findViewById(R.id.container)
        }
    }
}