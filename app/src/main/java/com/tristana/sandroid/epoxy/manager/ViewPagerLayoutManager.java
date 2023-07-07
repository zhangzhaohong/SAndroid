package com.tristana.sandroid.epoxy.manager;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.tristana.sandroid.epoxy.interfaces.OnViewPagerListener;

/**
 * @author koala
 * @version 1.0
 * @date 2023/7/6 13:51
 * @description
 */
public class ViewPagerLayoutManager extends LinearLayoutManager implements RecyclerView.OnChildAttachStateChangeListener {

    private static final String TAG = "ViewPagerLayoutManager";
    private final PagerSnapHelper mPagerSnapHelper;
    private OnViewPagerListener mOnViewPagerListener;
    private int mDrift;//位移，用来判断移动方向
    private int mCurrentPosition;//当前选中的项
    private boolean haveSelect;//初次选中标识

    public ViewPagerLayoutManager(Context context) {
        this(context, LinearLayoutManager.VERTICAL);
    }

    public ViewPagerLayoutManager(Context context, int orientation) {
        this(context, orientation, false);
    }

    public ViewPagerLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        mPagerSnapHelper = new PagerSnapHelper();
    }

    @Override
    public void onAttachedToWindow(RecyclerView recyclerView) {
        super.onAttachedToWindow(recyclerView);
        if (null != mPagerSnapHelper) {
            mPagerSnapHelper.attachToRecyclerView(recyclerView);
        }
        recyclerView.addOnChildAttachStateChangeListener(this);
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            if (null != mPagerSnapHelper) {
                View viewIdle = mPagerSnapHelper.findSnapView(ViewPagerLayoutManager.this);
                if (null != viewIdle) {
                    int position = getPosition(viewIdle);
                    LogUtils.d(TAG, "onScrollStateChanged-->position:" + position + ",currentPosition:" + mCurrentPosition);
                    //过滤重复选中
                    if (mOnViewPagerListener != null && this.mCurrentPosition != position) {
                        this.mCurrentPosition = position;
                        mOnViewPagerListener.onPageSelected(viewIdle, position, position == getItemCount() - 2);
                    }
                }
            }
        }
    }

    /**
     * 监听竖直方向的相对偏移量
     */
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.mDrift = dy;
        return super.scrollVerticallyBy(dy, recycler, state);
    }


    /**
     * 监听水平方向的相对偏移量
     */
    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.mDrift = dx;
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    /**
     * 设置监听
     */
    public void setOnViewPagerListener(OnViewPagerListener listener) {
        this.mOnViewPagerListener = listener;
    }

    /**
     * 默认初始选中用户scrollToPositionWithOffset中的项,不调用scrollToPositionWithOffset默认选中第0个
     *
     * @param view
     */
    @Override
    public void onChildViewAttachedToWindow(@NonNull View view) {
        if (!haveSelect) {
            this.haveSelect = true;
            this.mCurrentPosition = this.getPosition(view);
            if (null != mOnViewPagerListener) {
                mOnViewPagerListener.onPageSelected(view, mCurrentPosition, mCurrentPosition == getItemCount() - 2);
            }
        }
    }

    /**
     * 及时通知宿主销毁
     *
     * @param view
     */
    @Override
    public void onChildViewDetachedFromWindow(@NonNull View view) {
        int position = getPosition(view);
        LogUtils.d(TAG, "onChildViewDetachedFromWindow-->position:" + position + ",mCurrentPosition:" + mCurrentPosition);
        if (mCurrentPosition == position) {//只回调证在被选中的Item,宿主需要判断播放器播放时不能销毁正在播放的项
            if (mOnViewPagerListener != null)
                mOnViewPagerListener.onPageRelease(view, this.mDrift >= 0, position);
        }
    }

    public void onReset() {
        this.mCurrentPosition = 0;
        this.mDrift = 0;
        this.haveSelect = false;
    }
}
