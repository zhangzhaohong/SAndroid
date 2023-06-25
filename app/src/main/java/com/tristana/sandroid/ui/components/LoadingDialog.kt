package com.tristana.sandroid.ui.components

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.tristana.sandroid.R
import org.jetbrains.anko.dip

class LoadingDialog(
    context: Context,
    private val loadingText: String = "",
    private val isCancel: Boolean = false,
    private val cancelable: Boolean = true
) : Dialog(context, R.style.BottomDialogStyle) {

    private lateinit var dialogContainer: LinearLayout

    private lateinit var content: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)
        setCanceledOnTouchOutside(isCancel)
        setCancelable(cancelable)
        window?.let {
            val layoutParams = it.attributes
            val size = context.resources.getDimensionPixelSize(
                R.dimen.dialog_size
            )
            layoutParams.width = size
            layoutParams.height = size
            layoutParams.alpha = 1.0f
            layoutParams.dimAmount = 0.0f
            layoutParams.gravity = Gravity.CENTER

            it.decorView.setPadding(0, 0, 0, 0)
            it.attributes = layoutParams
        }
        dialogContainer = findViewById(R.id.dialog_container)
        content = findViewById(R.id.content)
        initView()
    }

    private fun initView() {
        val mask = GradientDrawable()
        mask.setColor(ContextCompat.getColor(context, R.color.b_80))
        mask.cornerRadii = floatArrayOf(
            context.dip(12).toFloat(),
            context.dip(12).toFloat(),
            context.dip(12).toFloat(),
            context.dip(12).toFloat(),
            context.dip(12).toFloat(),
            context.dip(12).toFloat(),
            context.dip(12).toFloat(),
            context.dip(12).toFloat()
        )
        dialogContainer.background = mask

        if (!TextUtils.isEmpty(loadingText)) {
            content.text = loadingText
            return
        }

        content.visibility = View.GONE

    }

    override fun dismiss() {
        try {
            super.dismiss()
        } catch (ignore: Exception) {
            /**
             * not attached to window manager fix这个错误
             * 为什么这么写？因为用户可能在loading的时候直接back / home掉app（不是杀掉）
             * 就会造成没有window，此时dismiss会crash
             */
        }
    }

}