package com.tristana.sandroid.ui.video.recommend.control;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import com.android.iplayer.base.BaseControlWidget;
import com.android.iplayer.utils.PlayerUtils;
import com.tristana.sandroid.R;

/**
 * created by hty
 * 2022/8/22
 * Desc:UI控制器-底部功能交互控制
 * 1、自定义seekbar相关的控制器需要实现{@link #isSeekBarShowing()}方法，返回显示状态给Controller判断控制器是否正在显示中
 * 2、当单击BaseController空白区域时控制器需要处理显示\隐藏逻辑的情况下需要复写{@link #showControl(boolean)}和{@link #hideControl(boolean)}方法
 * 3、这个seekBar进度条组件还维护了底部的ProgressBar，SDK默认的UI交互是：当播放器处于列表模式时不显示，其它情况都显示
 */
public class ControlFunctionBarView extends BaseControlWidget implements View.OnClickListener {

    private ProgressBar mProgressBar;//底部进度条

    public ControlFunctionBarView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.player_control_functionbar;
    }

    @Override
    public void initViews() {
        mProgressBar = findViewById(R.id.controller_bottom_progress);
    }

    @Override
    public void onProgress(long currentDuration, long totalDuration) {
        if (null != mProgressBar && mProgressBar.getMax() == 0) {
            mProgressBar.setMax((int) (mControlWrapper.getPreViewTotalDuration() > 0 ? mControlWrapper.getPreViewTotalDuration() : totalDuration));
        }
        if (null != mProgressBar) mProgressBar.setProgress((int) currentDuration);
    }

    @Override
    public void onBuffer(int bufferPercent) {
        int percent = PlayerUtils.getInstance().formatBufferPercent(bufferPercent, mControlWrapper.getDuration());
        if (null != mProgressBar && mProgressBar.getSecondaryProgress() != percent) {
            mProgressBar.setSecondaryProgress(percent);
        }
    }

    private void resetProgressBar() {
        if (null != mProgressBar) {
            mProgressBar.setProgress(0);
            mProgressBar.setSecondaryProgress(0);
            mProgressBar.setMax(0);
        }
    }

    @Override
    public void onReset() {
        resetProgressBar();
    }

    @Override
    public void onClick(View view) {

    }
}
