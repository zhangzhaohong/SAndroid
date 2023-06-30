package com.tristana.sandroid.video.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.tristana.library.tools.sharedPreferences.SpUtils;
import com.tristana.sandroid.dataModel.data.DataModel;
import com.tristana.sandroid.video.VideoInfoUtils;

import java.util.Objects;

import xyz.doikki.videoplayer.controller.ControlWrapper;
import xyz.doikki.videoplayer.controller.IControlComponent;
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory;
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory;
import xyz.doikki.videoplayer.player.AndroidMediaPlayerFactory;

/**
 * 调试信息
 */
public class DebugInfoView extends AppCompatTextView implements IControlComponent {

    private ControlWrapper mControlWrapper;

    public DebugInfoView(Context context) {
        super(context);
    }

    public DebugInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DebugInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        setBackgroundResource(android.R.color.black);
        setTextSize(10);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        setLayoutParams(lp);
    }


    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {

    }

    @Override
    public void onPlayStateChanged(int playState) {
        setText(getDebugString(playState));
        bringToFront();
    }

    public void onPlayStateChanged(int playState, Integer width, Integer height) {
        setText(getDebugString(playState, width, height));
        bringToFront();
    }

    /**
     * Returns the debugging information string to be shown by the target {@link TextView}.
     */
    protected String getDebugString(int playState) {
        return getCurrentPlayer() + VideoInfoUtils.playState2str(playState) + "\n"
                + "video width: " + mControlWrapper.getVideoSize()[0] + " , height: " + mControlWrapper.getVideoSize()[1];
    }

    protected String getDebugString(int playState, Integer width, Integer height) {
        return getCurrentPlayerBySetting() + VideoInfoUtils.playState2str(playState) + "\n"
                + "video width: " + width + " , height: " + height;
    }


    protected String getCurrentPlayer() {
        String player;
        Object playerFactory = VideoInfoUtils.getCurrentPlayerFactoryInVideoView(mControlWrapper);
        if (playerFactory instanceof ExoMediaPlayerFactory) {
            player = "ExoPlayer";
        } else if (playerFactory instanceof IjkPlayerFactory) {
            player = "IjkPlayer";
        } else if (playerFactory instanceof AndroidMediaPlayerFactory) {
            player = "MediaPlayer";
        } else {
            player = "unknown";
        }
        return String.format("player: %s ", player);
    }

    protected String getCurrentPlayerBySetting() {
        String player = "unknown";
        int videoTech;
        try {
            Object object = SpUtils.get(getContext(), DataModel.VIDEO_TECH_SP, 0);
            if (Objects.isNull(object)) {
                return String.format("player: %s ", player);
            } else if (object instanceof String) {
                videoTech = Integer.parseInt(String.valueOf(object));
            } else if (object instanceof Integer) {
                videoTech = (int) object;
            } else {
                return String.format("player: %s ", player);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return String.format("player: %s ", player);
        }
        player = switch (videoTech) {
            case 0, 1 -> "IjkPlayer";
            case 2 -> "ExoPlayer";
            case 3 -> "MediaPlayer";
            default -> "unknown";
        };
        return String.format("player: %s ", player);
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        bringToFront();
    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {

    }
}
