package com.tristana.sandroid.ui.video.area.recommend.listener

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @author koala
 * @version 1.0
 * @date 2022/4/18 11:54
 * @description
 */
abstract class EndlessRecyclerOnScrollListener : RecyclerView.OnScrollListener() {
    //用来标记是否正在向上滑动
    private var isSlidingUpward = false
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        val manager = recyclerView.layoutManager as LinearLayoutManager?
        // 当不滑动时
        if (newState == RecyclerView.SCROLL_STATE_IDLE && manager != null) {
            //获取最后一个完全显示的itemPosition
            val lastItemPosition = manager.findLastCompletelyVisibleItemPosition()
            val itemCount = manager.itemCount

            // 判断是否滑动到了最后一个item，并且是向上滑动
            if (lastItemPosition == itemCount - 1 && isSlidingUpward) {
                //加载更多
                onLoadMore(false)
            }
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        // 大于0表示正在向上滑动，小于等于0表示停止或向下滑动
        isSlidingUpward = dy > 0
    }

    /**
     * 加载更多回调
     */
    abstract fun onLoadMore(isSingle: Boolean)

    abstract fun onRequestMore()

    fun getSlidingDirection(): Boolean {
        return isSlidingUpward
    }
}