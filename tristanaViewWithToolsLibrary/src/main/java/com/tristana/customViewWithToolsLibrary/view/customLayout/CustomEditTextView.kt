package com.tristana.customViewWithToolsLibrary.view.customLayout

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.tristana.customViewWithToolsLibrary.R
import com.tristana.customViewWithToolsLibrary.tools.log.Timber
import com.tristana.customViewWithToolsLibrary.view.popupWindow.PopupWindowView


class CustomEditTextView(context: Context, attrs: AttributeSet?) : LinearLayoutCompat(context, attrs), TextWatcher {

    private lateinit var inputWarning: AppCompatImageView
    private var hint: String = ""
    private var initStatus: Boolean = false
    private var enableShowPassword: Boolean = false
    private var enableClearEditText: Boolean = false
    private lateinit var inputClearIcon: AppCompatImageView
    private lateinit var inputShowPassword: AppCompatImageView
    private var hidePasswordIcon: Int = 0
    private var showPasswordIcon: Int = 0
    private var clearIconResId: Int = 0
    private var maxLines: Int = 1
    private var maxSize: Int = 20
    private var inputType: Int = InputType.TYPE_CLASS_TEXT
    private var iconResId: Int = 0
    private lateinit var inputIcon: AppCompatImageView
    private lateinit var inputEditText: AppCompatEditText
    var timber: Timber

    init {
        LayoutInflater.from(context).inflate(R.layout.view_custom_edit_text_view, this)
        initResources()
        timber = Timber().timber
    }

    private fun initResources() {
        inputIcon = this.findViewById(R.id.input_icon)
        inputEditText = this.findViewById(R.id.input_editText)
        inputShowPassword = this.findViewById(R.id.input_show_password)
        inputClearIcon = this.findViewById(R.id.input_clear_icon)
        inputWarning = this.findViewById(R.id.input_warning)
    }

    private fun getBitmap(resId: Int): Bitmap? {
        val bitmap: Bitmap
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            val drawable = ContextCompat.getDrawable(context, resId) ?: return null
            bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        } else {
            bitmap = BitmapFactory.decodeResource(context.resources, resId)
        }
        return bitmap
    }

    private fun initIcon(resId: Int = 0) {
        this.iconResId = resId
    }

    private fun initClearIcon(resId: Int = 0, enableClearEditText: Boolean = false) {
        this.clearIconResId = resId
        this.enableClearEditText = enableClearEditText
    }

    private fun initShowPassWordIcon(resId_1: Int = 0, resId_2: Int = 0, enableShowPassword: Boolean = false) {
        this.showPasswordIcon = resId_1
        this.hidePasswordIcon = resId_2
        this.enableShowPassword = enableShowPassword
    }

    private fun initEditText(inputType: Int, maxSize: Int = 20, maxLines: Int = 1, hint: String = "") {
        this.inputType = inputType
        this.maxSize = maxSize
        this.maxLines = maxLines
        this.hint = hint
    }

    private fun initView() {
        this.inputWarning.visibility = View.GONE
        if (iconResId != 0) {
            this.inputIcon.visibility = View.VISIBLE
            this.inputIcon.background = BitmapDrawable(context.resources, getBitmap(iconResId))
        } else {
            this.inputIcon.visibility = View.GONE
        }
        if (this.hint.isNotEmpty()) {
            this.inputEditText.hint = this.hint
        }
        this.inputEditText.inputType = this.inputType
        if (this.maxSize != 0) {
            this.inputEditText.maxEms = this.maxSize
        }
        if (this.maxLines != 0) {
            this.inputEditText.maxLines = this.maxLines
        }
        if (this.clearIconResId != 0) {
            this.inputClearIcon.visibility = View.GONE
            this.inputClearIcon.background = BitmapDrawable(context.resources, getBitmap(this.clearIconResId))
        }
        this.inputEditText.addTextChangedListener(this)
    }

    private fun refreshShowPasswordIcon() {
        if (this.showPasswordIcon == 0 && this.hidePasswordIcon == 0 || !enableShowPassword) {
            this.inputShowPassword.visibility = View.GONE
            return
        }
        when (this.inputType) {
            InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT -> {
                this.inputShowPassword.visibility = View.VISIBLE
                this.inputShowPassword.background = BitmapDrawable(context.resources, getBitmap(this.showPasswordIcon))
                this.inputShowPassword.setOnClickListener {
                    this.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_CLASS_TEXT
                    this.inputEditText.inputType = this.inputType
                    refreshShowPasswordIcon()
                }
            }
            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_CLASS_TEXT -> {
                this.inputShowPassword.visibility = View.VISIBLE
                this.inputShowPassword.background = BitmapDrawable(context.resources, getBitmap(this.hidePasswordIcon))
                this.inputShowPassword.setOnClickListener {
                    this.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
                    this.inputEditText.inputType = this.inputType
                    refreshShowPasswordIcon()
                }
            }
            else -> {
                this.inputShowPassword.visibility = View.GONE
            }
        }
    }

    fun setText(text: String) {
        if (!initStatus)
            throw NullPointerException("Need to init view first!")
        else
            inputEditText.setText(text)
    }

    fun getText(): String {
        return inputEditText.text.toString()
    }

    fun showError(resId: Int, msg: String, enableShowMsg: Boolean = false) {
        if (!initStatus)
            throw NullPointerException("Need to init view first!")
        else {
            this.inputWarning.visibility = View.VISIBLE
            val bitmap = getBitmap(resId) ?: return
            this.inputWarning.background = BitmapDrawable(context.resources, bitmap)
            if (enableShowMsg) {
                this.inputWarning.setOnClickListener {
                    PopupWindowView(context).setText(msg).showPopupWindow()
//                    initPopWindow(msg, this.inputWarning)
//                    ToastUtils.showLongToast(context, msg)
                }
            }
        }
    }

    private fun initPopWindow(msg: String, view: View) {
        val popupView: View = LayoutInflater.from(context).inflate(R.layout.item_popup_window, this, false)
        val tipsTextView: AppCompatTextView = popupView.findViewById(R.id.tips_text)
        //构造popupWindow
        val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        )
        popupWindow.animationStyle = R.anim.anim_pop
        popupWindow.isOutsideTouchable = true
        popupWindow.setTouchInterceptor { _, _ ->
            performClick()
            false
        }
        popupWindow.setBackgroundDrawable(ColorDrawable(0x00000000))
        popupWindow.showAsDropDown(view, -200, 0)
        tipsTextView.text = msg
    }

    fun dismissError() {
        this.inputWarning.visibility = View.GONE
    }

    fun initParameter(
            iconResId: Int = 0,//左侧图标，为0则不会显示
            inputType: Int = InputType.TYPE_CLASS_TEXT,//输入框类型
            maxSize: Int = 20,//最大输入长度
            maxLines: Int = 1,//最大行数
            hint: String = "",//输入框提示，为空字符串则不显示
            showPasswordIcon: Int = 0,//显示密码图标
            hidePasswordIcon: Int = 0,//不显示密码图标
            enableShowPassword: Boolean = false,//是否允许显示密码（只在输入密码时有用）
            clearIconResId: Int = 0,//清空输入框内容图标
            enableClearEditText: Boolean = false//是否允许清空输入框
    ) {
        initIcon(iconResId)
        initEditText(inputType, maxSize, maxLines, hint)
        initShowPassWordIcon(showPasswordIcon, hidePasswordIcon, enableShowPassword)
        initClearIcon(clearIconResId, enableClearEditText)
        initView()
        initStatus = true
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (p0.isNullOrEmpty()) {
            this.inputClearIcon.visibility = View.GONE
            this.inputShowPassword.visibility = View.GONE
        } else if (this.enableClearEditText) {
            refreshShowPasswordIcon()
            this.inputClearIcon.visibility = View.VISIBLE
            this.inputClearIcon.setOnClickListener {
                inputEditText.setText("")
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {

    }

}