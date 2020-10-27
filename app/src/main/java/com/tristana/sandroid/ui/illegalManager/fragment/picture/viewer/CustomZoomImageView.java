package com.tristana.sandroid.ui.illegalManager.fragment.picture.viewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;

import com.tristana.sandroid.tools.log.Timber;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class CustomZoomImageView extends AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener, View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {
    private ArrayList<MotionEvent> events;
    private int mTouchStop;
    private Timber timber;
    /**
     * 多点手势触控缩放比率分析器
     */
    private ScaleGestureDetector mScaleGestureDetector;
    /***
     * 实现双击放大与缩小
     * */
    private GestureDetector mGestureDetector;
    /**
     * 缩放工具
     */
    private Matrix mMatrix;
    /**
     * 缩放的最小值
     */
    private float mMinScale;
    /**
     * 缩放的中间值
     */
    private float mMidScale;
    /**
     * 缩放的最大值
     */
    private float mMaxScale;
    private boolean isCheckTopAndBottom;
    private boolean isCheckLeftAndRight;
    private boolean isScaleing;
    private OnClickListener onClickListener;
    private boolean isInit;
    private int mLastPointerCount;
    private boolean isCanDrag;
    private float mLastX;
    private float mLastY;
    private double mTouchSlop;

    public CustomZoomImageView(@NonNull Context context) {
        super(context);
        timber = new Timber("CustomZoomImageView");
    }

    public CustomZoomImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomZoomImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.MATRIX);
        mMatrix = new Matrix();
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isScaleing || getScale() >= mMaxScale)
                    return true;
                isScaleing = true;
                float x = e.getX();
                float y = e.getY();
                if (getScale() < mMidScale) {
                    postDelayed(new AutoScaleRunnable(mMidScale, x, y), 16);
                } else {
                    postDelayed(new AutoScaleRunnable(mMinScale, x, y), 16);
                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (onClickListener != null) {
                    onClickListener.onClick(CustomZoomImageView.this);
                    return true;
                }
                return false;
            }
        });
        setOnTouchListener(this);
        mTouchStop = ViewConfiguration.get(context).getScaledTouchSlop();
        events = new ArrayList<>();
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (mGestureDetector.onTouchEvent(motionEvent))
            return true;
        //将触摸事件传递给ScaleGestureDetector
        if (motionEvent.getPointerCount() > 1)
            mScaleGestureDetector.onTouchEvent(motionEvent);
        float x = 0;
        float y = 0;
        int pointerCount = motionEvent.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            x += motionEvent.getX(i);
            y += motionEvent.getY(i);
        }
        x /= pointerCount;
        y /= pointerCount;
        if (mLastPointerCount != pointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }
        mLastPointerCount = pointerCount;
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                RectF rectF = getMatrixRectF();
                if ((rectF.width() > getWidth() + 0.01f || (rectF.height() > getHeight() + 0.01f))) {
                    try {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    } catch (Exception e) {
                        timber.d("onError:" + e.toString());
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = x - mLastX;
                float dy = y - mLastY;
                if (!isCanDrag) {
                    isCanDrag = isMoveAction(dx, dy);
                }
                if (isCanDrag) {
                    RectF rectF1 = getMatrixRectF();
                    if (getDrawable() != null) {
                        isCheckLeftAndRight = isCheckTopAndBottom = true;
                        if (rectF1.width() <= getWidth()) {
                            isCheckLeftAndRight = false;
                            dx = 0;
                        }
                        if (rectF1.height() <= getHeight()) {
                            isCheckTopAndBottom = false;
                            dy = 0;
                        }
                        mMatrix.postTranslate(dx, dy);
                        checkBorderAndCenterWhenScale();
                        setImageMatrix(mMatrix);
                    }
                }
                mLastX = x;
                mLastY = y;
                RectF rectF2 = getMatrixRectF();
                if ((rectF2.width() > getWidth() + 0.01f || (rectF2.height() > getHeight() + 0.01f))) {
                    if ((rectF2.right != getWidth()) && (rectF2.left != 0)) {
                        try {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        } catch (Exception e) {
                            timber.d("onError:" + e.toString());
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mLastPointerCount = 0;
                break;
        }
        return true;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        this.onClickListener = l;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        init();
        super.setImageBitmap(bm);
    }

    @Override
    public void setImageResource(int resId) {
        init();
        super.setImageResource(resId);
    }

    /**
     * 判断是否足以触发移动事件
     */
    private boolean isMoveAction(float dx, float dy) {
        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
    }

    @Override
    public void onGlobalLayout() {
        if (getDrawable() == null || getWidth() == 0 || getHeight() == 0)
            return;
        if (!isInit) {
            int width = getWidth();
            int height = getHeight();
            float screenWeight = height * 1.0F / width;
            int imageH = getDrawable().getIntrinsicHeight(); // 图片高度
            int imageW = getDrawable().getIntrinsicWidth(); // 图片宽度
            float imageWeight = imageH * 1.0f / imageW;
            //如果当前屏幕高宽比 大于等于 图片高宽比,就缩放图片
            if (screenWeight >= imageWeight) {
                float scale = 1.0f;
                //图片比当前View宽,但是比当前View矮
                timber.d("图片比当前View宽,但是比当前View矮");
                if (imageW > width && imageH < height) {
                    scale = width * 1.0f / imageW; //根据宽度缩放
                }

                //图片比当前View窄,但是比当前View高
                timber.d("图片比当前View窄,但是比当前View高");
                if (imageH > height && imageW < width) {
                    scale = height * 1.0f / imageH; //根据高度缩放
                }

                //图片高宽都大于当前View,那么就根据最小的缩放值来缩放
                timber.d("图片高宽都大于当前View,那么就根据最小的缩放值来缩放");
                if (imageH > height && imageW > width) {
                    scale = Math.min(width * 1.0f / imageW, height * 1.0f / imageH);
                    timber.d("max scale:" + scale);
                }

                if (imageH < height && imageW < width) {
                    scale = Math.min(width * 1.0f / imageW, height * 1.0f / imageH);
                    timber.d("min scale:" + scale);
                }

                //设置缩放比率
                mMinScale = scale;
                mMidScale = mMinScale * 2;
                mMaxScale = mMinScale * 4;

                //把图片移动到中心点
                int dx = getWidth() / 2 - imageW / 2;
                int dy = getHeight() / 2 - imageH / 2;

                //设置缩放(全图浏览模式,用最小的缩放比率去查看图片就好了)/移动位置
                mMatrix.postTranslate(dx, dy);
                mMatrix.postScale(mMinScale, mMinScale, (float) width / 2, (float) height / 2);
            } else {
                //将宽度缩放至屏幕比例缩放(长图,全图预览)
                //设置缩放比率
                mMaxScale = width * 1.0f / imageW;
                mMidScale = mMaxScale / 2;
                mMinScale = mMaxScale / 4;
                //因为是长图浏览,所以用最大的缩放比率去加载长图
                mMatrix.postScale(mMaxScale, mMaxScale, 0, 0);
            }
            setImageMatrix(mMatrix);
            isInit = true;
        }
    }

    /**
     * 重新初始化View
     */
    public void init() {
        isInit = false;
        mMatrix.reset();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        timber.d("注册OnGlobalLayoutListener");
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        timber.d("移除OnGlobalLayoutListener");
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    private class AutoScaleRunnable implements Runnable {

        private float mTargetScale;
        /***
         * 缩放中心点X,Y
         * */
        private float x;
        private float y;
        private float tmpScale;

        private final float BIGGER = 1.07f;
        private final float SMALLER = 0.93f;

        public AutoScaleRunnable(float mTargetScale, float x, float y) {
            this.mTargetScale = mTargetScale;
            this.x = x;
            this.y = y;

            if (getScale() < mTargetScale) {
                tmpScale = BIGGER;
            } else {
                tmpScale = SMALLER;
            }
        }


        @Override
        public void run() {
            mMatrix.postScale(tmpScale, tmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mMatrix);
            float currentScale = getScale();
            if ((tmpScale > 1.0f && currentScale < mTargetScale) || (tmpScale < 1.0f && currentScale > mTargetScale)) {
                postDelayed(this, 16);
            } else {
                float scale = mTargetScale / currentScale;
                mMatrix.postScale(scale, scale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mMatrix);
                isScaleing = false;
            }
        }
    }

    /**
     * 获取缩放比率
     */
    private float getScale() {
        float[] values = new float[9];
        mMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    /**
     * 在缩放的时候进行边界,位置 检查
     */
    private void checkBorderAndCenterWhenScale() {
        RectF rectF = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;
        int width = getWidth();
        int height = getHeight();

        if (rectF.top > 0 && isCheckTopAndBottom) {
            deltaY = -rectF.top;
        }

        if (rectF.bottom < height && isCheckTopAndBottom) {
            deltaY = height - rectF.bottom;
        }

        if (rectF.left > 0 && isCheckLeftAndRight) {
            deltaX = -rectF.left;
        }

        if (rectF.right < width && isCheckLeftAndRight) {
            deltaX = width - rectF.right;
        }

        mMatrix.postTranslate(deltaX, deltaY);
        setImageMatrix(mMatrix);

    }

    /**
     * 获取图片放大缩小后的宽高/top/left/right/bottom
     */
    private RectF getMatrixRectF() {
        RectF rectF = new RectF();
        Drawable drawable = getDrawable();
        if (drawable != null) {
            rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            mMatrix.mapRect(rectF);
        }
        return rectF;
    }

}
