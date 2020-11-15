package com.tristana.sandroid.ui.illegalManager.fragment.picture.viewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.tristana.sandroid.tools.http.HttpUtils;
import com.tristana.sandroid.tools.log.Timber;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class CustomImageView extends View {

    private final Context context;
    private final Timber timber;
    /**
     * isInit记录当前初始化状态
     */
    boolean isInit = false;
    private Bitmap bitmap = null;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    /**
     * viewWidth, viewHeight为屏幕宽高
     * bmpWidth, bmpHeight为图片初始宽高
     * dX, dY为XY坐标
     * scaleX, scaleY为XY缩放范围
     */
    private int viewWidth, viewHeight, bmpWidth, bmpHeight;
    private float dX, dY, scaleX, scaleY;
    /**
     * scale记录为当前缩放比例
     * scaleMin为缩放最小值
     * scaleMax为缩放最大值
     */
    private float scale = 1.0F;
    final float scaleMin = 1.0F;
    final float scaleMax = 3.0F;
    private final Paint mPainter = new Paint();
    private int placeHolderResId = 0;
    private boolean loadingFromNetWork = false;
    private boolean loadingFinish = false;
    private int loadingFailedPlaceHolderResId = 0;

    private void initViewSize() {
        viewWidth = getWidth();
        viewHeight = getHeight();
        if (viewWidth > 0 && viewHeight > 0) {
            isInit = true;
            scaleX = 1.0F * viewWidth / bmpWidth;
            scaleY = 1.0F * viewHeight / bmpHeight;
            if (loadingFromNetWork && !loadingFinish) {
                scale = 1.0F;
            } else {
                scale = Math.min(scaleX, scaleY);
            }
            dX = (float) (viewWidth - bmpWidth) / 2;
            dY = (float) (viewHeight - bmpHeight) / 2;
        }
    }

    public CustomImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initListener(context);
        timber = new Timber("CustomImageView");
    }

    private void initListener(Context context) {
        mGestureDetector = new GestureDetector(context, new GestureDetectorImpl());
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetectorImpl());
    }

    private class GestureDetectorImpl extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!loadingFromNetWork || loadingFromNetWork && loadingFinish) {
                dX -= distanceX;
                dY -= distanceY;
                invalidate();
                return true;
            } else {
                return false;
            }
        }
    }

    private class ScaleGestureDetectorImpl extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (!loadingFromNetWork || loadingFromNetWork && loadingFinish) {
                scale *= detector.getScaleFactor();
                scale = Math.max(scaleMin, Math.min(scaleMax, scale));
                invalidate();
                return true;
            } else {
                return false;
            }
        }
    }

    private void checkBounds() {
        if (scale > scaleX) {
            dX = Math.min(dX, (scale - 1) * ((float) bmpWidth / 2));
            dX = Math.max(dX, viewWidth - bmpWidth - (scale - 1) * ((float) bmpWidth / 2));
        } else {
            dX = Math.max(dX, (scale - 1) * ((float) bmpWidth / 2));
            dX = Math.min(dX, viewWidth - bmpWidth - (scale - 1) * ((float) bmpWidth / 2));
        }
        if (scale > scaleY) {
            dY = Math.min(dY, (scale - 1) * ((float) bmpHeight / 2));
            dY = Math.max(dY, viewHeight - bmpHeight - (scale - 1) * ((float) bmpHeight / 2));
        } else {
            dY = Math.max(dY, (scale - 1) * ((float) bmpHeight / 2));
            dY = Math.min(dY, viewHeight - bmpHeight - (scale - 1) * ((float) bmpHeight / 2));
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            performClick();
        }
        mGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap == null)
            return;
        if (!isInit)
            initViewSize();
        canvas.save();
        checkBounds();
        canvas.scale(scale, scale, dX + ((float) bmpWidth / 2), dY + ((float) bmpHeight / 2));
        canvas.drawBitmap(bitmap, dX, dY, mPainter);
        canvas.restore();
    }

    private Bitmap getBitmap(int resId) {
        Bitmap bitmap;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Drawable drawable = ContextCompat.getDrawable(context, resId);
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        }
        return bitmap;
    }

    /**
     * 设置图片资源
     */
    public void setPlaceHolderResource(int resId) {
        setBitmapResource(getBitmap(resId));
    }

    public void setBitmapResource(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.bmpWidth = bitmap.getWidth();
        this.bmpHeight = bitmap.getHeight();
        initViewSize();
        invalidate();
    }

    public void setPlaceHolder(int resId) {
        this.placeHolderResId = resId;
    }

    public void setLoadingFailedPlaceHolder(int resId) {
        this.loadingFailedPlaceHolderResId = resId;
    }

    public void loadImageFromUrl(final String url) {
        loadingFromNetWork = true;
        if (url == null)
            return;
        if (placeHolderResId != 0) {
            setPlaceHolderResource(placeHolderResId);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = HttpUtils.getBitmap(url);
                if (bitmap != null) {
                    loadingFinish = true;
                    setBitmapResource(bitmap);
                } else {
                    loadingFinish = false;
                    if (loadingFailedPlaceHolderResId != 0) {
                        setPlaceHolderResource(loadingFailedPlaceHolderResId);
                    }
                }
            }
        }).start();
    }

}
