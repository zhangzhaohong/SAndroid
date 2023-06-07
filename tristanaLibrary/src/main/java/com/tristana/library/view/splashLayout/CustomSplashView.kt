package com.tristana.library.view.splashLayout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.tristana.library.R
import com.tristana.library.tools.http.HttpUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomSplashView(context: Context, attrs: AttributeSet?) :
    LinearLayoutCompat(context, attrs) {
    private var splashClickable: Boolean = false
    private var enterTextSplashOnClick: String = ""
    private var splashUrl: String = ""
    private var loadFromNetWorkStatus: Boolean = false
    private lateinit var mTimer: CountDownTimer
    private lateinit var operation: () -> Unit
    private var appVer: String = ""
    private var appNameTextSize: Long = 20
    private lateinit var appName: String
    private var iconResId: Int = 0
    private var splashResId: Int = 0
    private lateinit var destination: () -> Unit
    private var enterStatus: Boolean = false
    private var enterText: String = ""
    private var canNotEnterText: String = ""
    private var needTimer: Boolean = false
    private var durationTime: Long = 0
    private var canNotEnterTime: Long = 0
    private var enterBtnDisabled = false
    private lateinit var splashIcon: AppCompatImageView
    private lateinit var splashAppName: AppCompatTextView
    private lateinit var splashPic: AppCompatImageView
    private lateinit var splashEnter: AppCompatTextView
    private lateinit var splashAppVer: AppCompatTextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_splash, this)
        initResources()
    }

    /**
     * 查找部分组件
     * */
    private fun initResources() {
        splashIcon = this.findViewById(R.id.splash_icon)
        splashAppName = this.findViewById(R.id.splash_appName)
        splashPic = this.findViewById(R.id.splash_pic)
        splashEnter = this.findViewById(R.id.splash_enter)
        splashAppVer = this.findViewById(R.id.splash_appVer)
    }

    /**
     * enterStatus即是否允许跳过
     * enterText即右上角文案
     * enterTextSplashOnClick也为右上角文案
     * needTimber是否需要及时器
     * durationTime即总时长
     * */
    private fun enableEnter(
        enterStatus: Boolean = false,
        enterText: String = "",
        canNotEnterText: String = "",
        enterTextSplashOnClick: String = "",
        needTimer: Boolean = false,
        durationTime: Long = 0,
        canNotEnterTime: Long = 0,
        destination: () -> Unit = {}
    ) {
        this.enterStatus = enterStatus
        this.enterText = enterText
        this.canNotEnterText = canNotEnterText
        this.enterTextSplashOnClick = enterTextSplashOnClick
        this.needTimer = needTimer
        this.durationTime = durationTime
        this.canNotEnterTime = canNotEnterTime
        this.destination = destination
        mTimer = object : CountDownTimer(durationTime * 1000, 1 * 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(p0: Long) {
                if (p0 / 1000 > durationTime - canNotEnterTime) {
                    splashEnter.text = canNotEnterText + " ${(p0 / 1000)}秒"
                    enterBtnDisabled = true
                } else {
                    splashEnter.text = enterText + " ${(p0 / 1000)}秒"
                    enterBtnDisabled = false
                }
            }

            override fun onFinish() {
                this.cancel()
                destination.invoke()
            }

        }
    }

    /**
     * splashUrl即图片url
     * 后者则为从网络加载的开关
     * */
    private fun loadFromNetwork(splashUrl: String = "", loadFromNetWorkStatus: Boolean = false) {
        this.splashUrl = splashUrl
        this.loadFromNetWorkStatus = loadFromNetWorkStatus
    }

    private fun setSplashAppName(appName: String = "", appNameTextSize: Long = 20) {
        this.appName = appName
        this.appNameTextSize = appNameTextSize
    }

    private fun setSplashAppVer(appVer: String = "") {
        this.appVer = appVer
    }

    /**
     * 设置splash底部图标
     * */
    private fun setIconResId(resId: Int = 0) {
        this.iconResId = resId
    }

    private fun setSplashResIdWithUnit(
        resId: Int = 0,
        splashClickable: Boolean = false,
        operation: () -> Unit = {}
    ) {
        this.splashResId = resId
        this.splashClickable = splashClickable
        this.operation = operation
    }

    private fun getBitmap(resId: Int): Bitmap? {
        val bitmap: Bitmap
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            val drawable = ContextCompat.getDrawable(context, resId) ?: return null
            bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        } else {
            bitmap = BitmapFactory.decodeResource(context.resources, resId)
        }
        return bitmap
    }

    private fun initView() {
        if (this.enterStatus) {
            this.splashEnter.visibility = View.VISIBLE
            this.splashEnter.text = this.enterText
            this.splashEnter.setOnClickListener {
                if (!enterBtnDisabled) {
                    mTimer.cancel()
                    this.splashEnter.visibility = View.GONE
                    this.destination.invoke()
                }
            }
        } else {
            this.splashEnter.visibility = View.GONE
        }
        if (this.iconResId != 0) {
            this.splashIcon.background =
                BitmapDrawable(context.resources, getBitmap(this.iconResId))
        }
        this.appName.let {
            this.splashAppName.text = it
        }
        this.appNameTextSize.let {
            this.splashAppName.textSize = it.toFloat()
        }
        this.appVer.isNotEmpty().let {
            if (it) {
                this.splashAppVer.visibility = View.VISIBLE
                this.splashAppVer.text = this.appVer
            } else {
                this.splashAppVer.visibility = View.GONE
            }
        }
        if (splashResId != 0) {
            updateSplash(getBitmap(this.splashResId))
        }
        if (this.needTimer) {
            mTimer.start()
        }
        if (splashUrl.isNotEmpty() && loadFromNetWorkStatus) {
            var bitmap: Bitmap?
            MainScope().launch {
                withContext(Dispatchers.IO) {
                    bitmap = HttpUtils().getBitmap(splashUrl)
                    withContext(Dispatchers.Main) {
                        if (bitmap != null) {
                            updateSplash(compressBitmap(bitmap!!))
                        }
                    }
                }
            }
        }
    }

    /**
     * 得到bitmap的大小
     */
    @SuppressLint("ObsoleteSdkInt")
    private fun getBitmapSize(bitmap: Bitmap): Int {
        val result: Long = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {    //API 19
                bitmap.allocationByteCount.toLong()
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1 -> { //API 12
                bitmap.byteCount.toLong()
            }

            else -> {
                // 在低版本中用一行的字节x高度
                bitmap.rowBytes * bitmap.height.toLong() //earlier version
            }
        }
        return result.toInt() / 1024 / 1024
    }

    private fun compressBitmap(bitmap: Bitmap): Bitmap {
        var option = 1.0f
        var newBitmap = bitmap
        while (getBitmapSize(newBitmap) > 100 && option.toInt() * 10 > 0) {
            option = (option * 10 - 1) / 10
            val matrix = Matrix()
            matrix.postScale(option, option)
            newBitmap =
                Bitmap.createBitmap(newBitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
        return newBitmap
    }

    private fun updateSplash(bitmap: Bitmap?) {
        if (bitmap == null)
            return
        this.splashPic.background = BitmapDrawable(context.resources, bitmap)
        if (splashClickable) {
            this.splashPic.setOnClickListener {
                mTimer.cancel()
                this.operation.invoke()
                this.splashEnter.text = "进入体验"
            }
        }
    }

    /**
     * init初始化view
     * */
    fun initParameter(
        enterStatus: Boolean = false,//是否允许跳过，默认为false
        enterText: String = "",//跳过splash相关文案设定
        canNotEnterText: String = "",//禁用跳过先关文案设定
        enterTextSplashOnClick: String = "",//点击splash内容后跳过splash相关文案设定
        needTimer: Boolean = false,//是否需要倒计时计时器
        durationTime: Long = 0,//splash持续显示时间
        canNotEnterTime: Long = 0,//禁用跳过时长
        destination: () -> Unit = {},//倒计时/跳过完成后代码操作
        iconResId: Int = 0,//splash底部resId
        appName: String = "",//splash底部app名称
        appNameTextSize: Long = 20,//splash底部app名称字体大小
        appVer: String = "",//splash底部所显示版本号
        splashResId: Int = 0,//splash广告位本地图片
        splashClickable: Boolean = false,//splash是否允许点击跳转
        operation: () -> Unit = {},//点击广告位相关操作
        splashUrl: String = "",//splash广告位图片Url地址
        loadFromNetWorkStatus: Boolean = false//splash从网络加载内容
    ) {
        //init view parameter
        enableEnter(
            enterStatus,
            enterText,
            canNotEnterText,
            enterTextSplashOnClick,
            needTimer,
            durationTime,
            canNotEnterTime,
            destination
        )
        setIconResId(iconResId)
        setSplashAppName(
            appName,
            appNameTextSize
        )
        setSplashAppVer(appVer)
        setSplashResIdWithUnit(
            splashResId,
            splashClickable,
            operation
        )
        loadFromNetwork(
            splashUrl,
            loadFromNetWorkStatus
        )
        initView()
    }
}