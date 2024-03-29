package com.tristana.library.view.imageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.tristana.library.tools.http.HttpUtils;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class CustomImageView extends View {

    private final Context context;
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
    private float scaleMin = 1.0F;
    private float scaleMax = 3.0F;
    private final Paint mPainter = new Paint();
    private int placeHolderResId = 0;
    private boolean loadingFromNetWork = false;
    private boolean loadingFinish = false;
    private int loadingFailedPlaceHolderResId = 0;
    private Boolean longPicStatus = false;
    private Boolean isVertical = false;

    private void initViewSize() {
        viewWidth = getWidth();
        viewHeight = getHeight();
        if (viewWidth > 0 && viewHeight > 0) {
            isInit = true;
            longPicStatus = checkLongPic();
            isVertical = checkOrientation();
            LogUtils.i("[picInfo]" + "\n" +
                    "onShowInfo:" + "\n" +
                    "longPicStatus[" + longPicStatus + "]" + "\n" +
                    "isVertical[" + isVertical + "]");
            scaleX = 1.0F * viewWidth / bmpWidth;
            scaleY = 1.0F * viewHeight / bmpHeight;
            if (viewWidth < bmpWidth && viewHeight < bmpHeight) {
                scaleMin = 0.1F;
                scaleMax = 1.0F;
            }
            if (loadingFromNetWork && !loadingFinish) {
                scale = 1.0F;
            } else if (longPicStatus) {
                scale = Math.max(scaleX, scaleY);
            } else {
                scale = Math.min(scaleX, scaleY);
            }
            dX = (float) (viewWidth - bmpWidth) / 2;
            if (longPicStatus)
                dY = -(float) (viewHeight - bmpHeight) / 2;
            else
                dY = (float) (viewHeight - bmpHeight) / 2;
            LogUtils.i("[picDetail]" + "\n" +
                    "onShowInfo:" + "\n" +
                    "viewWidth[" + viewWidth + "]" + "\n" +
                    "viewHeight[" + viewHeight + "]" + "\n" +
                    "bmpWidth[" + bmpWidth + "]" + "\n" +
                    "bmpHeight[" + bmpHeight + "]" + "\n" +
                    "scaleX[" + scaleX + "]" + "\n" +
                    "scaleY[" + scaleY + "]" + "\n" +
                    "scale[" + scale + "]");
        }
    }

    public CustomImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initListener(context);
    }

    private void initListener(Context context) {
        mGestureDetector = new GestureDetector(context, new GestureDetectorImpl());
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetectorImpl());
    }

    private class GestureDetectorImpl extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (checkStatus()) {
                if (checkX()) {
                    dX -= distanceX;
                }
                //如果当前图片高度大于view高度，则支持上下滑动
                if (checkY()) {
                    dY -= distanceY;
                }
                invalidate();
                return true;
            } else {
                return false;
            }
        }

        /**
         * 检查图片当前高度是不是大于view的高度
         */
        private boolean checkY() {
            boolean status = false;
            if (1.0F * scale * bmpHeight > viewHeight) {
                status = true;
            }
            return status;
        }

        /**
         * 检查图片当前宽度是不是大于view的宽度
         */
        private boolean checkX() {
            boolean status = false;
            if (1.0F * scale * bmpWidth > viewWidth) {
                status = true;
            }
            return status;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (checkStatus()) {
                if (scale < scaleMax) {
                    scale = scaleMax;
                } else if (longPicStatus) {
                    scale = Math.max(scaleX, scaleY);
                } else {
                    scale = Math.min(scaleX, scaleY);
                }
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
            if (checkStatus()) {
                scale *= detector.getScaleFactor();
                scale = Math.max(scaleMin, Math.min(scaleMax, scale));
                invalidate();
                return true;
            } else {
                return false;
            }
        }
    }

    private Boolean checkStatus() {
        boolean status = false;
        if (!loadingFromNetWork || loadingFinish) {
            status = true;
        }
        return status;
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

    //回收Bitmap
    private static void recycleBitmap(Bitmap... bitmaps) {
        if (bitmaps == null) {
            return;
        }
        for (Bitmap bm : bitmaps) {
            if (null != bm && !bm.isRecycled()) {
                bm.recycle();
            }
        }
    }

    public void releaseBitmap() {
        recycleBitmap(bitmap);
    }

    /**
     * 得到bitmap的大小
     */
    public static int getBitmapSize(Bitmap bitmap) {
        long result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            result = bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            result = bitmap.getByteCount();
        } else {
            // 在低版本中用一行的字节x高度
            result = bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
        }
        return (int) result / 1024 / 1024;
    }

    private Bitmap compressBitmap(Bitmap bitmap) {
        float option = 1.0F;
        Bitmap newBitmap = bitmap;
        while (getBitmapSize(newBitmap) > 100 && (int) option * 10 > 0) {
            option = (option * 10 - 1) / 10;
            Matrix matrix = new Matrix();
            matrix.postScale(option, option);
            newBitmap = Bitmap.createBitmap(newBitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return newBitmap;
    }

    private Bitmap getBitmap(int resId) {
        Bitmap bitmap;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Drawable drawable = ContextCompat.getDrawable(context, resId);
            if (drawable == null)
                return null;
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
        Bitmap bitmap = getBitmap(resId);
        if (bitmap != null)
            setBitmapResource(bitmap, false);
    }

    /**
     * 设置bitmap图片资源，所有设置最终都会走到这里
     */
    public void setBitmapResource(Bitmap bitmap, boolean needCompress) {
        if (needCompress)
            bitmap = compressBitmap(bitmap);
        this.bitmap = bitmap;
        this.bmpWidth = bitmap.getWidth();
        this.bmpHeight = bitmap.getHeight();
        initViewSize();
        invalidate();
    }

    /**
     * 校验图片方向
     */
    private Boolean checkOrientation() {
        boolean status = false;
        if (bmpHeight > bmpWidth) {
            status = true;
        }
        return status;
    }

    /**
     * 检测是不是属于长图，即高度大于宽度*2
     */
    private Boolean checkLongPic() {
        boolean status = false;
        if (bmpHeight > 2 * bmpWidth) {
            status = true;
        }
        return status;
    }

    public void setPlaceHolder(int resId) {
        this.placeHolderResId = resId;
    }

    public void setLoadingFailedPlaceHolder(int resId) {
        this.loadingFailedPlaceHolderResId = resId;
    }

    /**
     * 从URL读取图片
     */
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
                Bitmap bitmap = new HttpUtils().getBitmap(url);
                if (bitmap != null) {
                    loadingFinish = true;
                    setBitmapResource(bitmap, true);
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
