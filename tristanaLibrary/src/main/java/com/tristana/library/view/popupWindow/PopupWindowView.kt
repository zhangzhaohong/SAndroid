package com.tristana.library.view.popupWindow

import android.content.Context
import android.view.View
import android.view.animation.Animation
import androidx.appcompat.widget.AppCompatTextView
import com.tristana.library.R
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.ScaleConfig
import razerdp.util.animation.TranslationConfig


class PopupWindowView(context: Context?) : BasePopupWindow(context) {
    private lateinit var textView: AppCompatTextView

    init {
        setContentView(R.layout.item_popup_layout)
    }

    override fun onViewCreated(contentView: View) {
        textView = contentView.findViewById(R.id.tv_desc)
    }

    override fun onCreateShowAnimation(): Animation? {
        return AnimationHelper.asAnimation()
                .withTranslation(TranslationConfig.FROM_BOTTOM)
                .toShow()
    }

    override fun onCreateDismissAnimation(): Animation? {
        return AnimationHelper.asAnimation()
                .withScale(ScaleConfig.CENTER)
                .toDismiss()
    }

    fun setText(text: String): PopupWindowView {
        textView.text = text
        return this
    }

}