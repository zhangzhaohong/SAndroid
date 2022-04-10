package com.tristana.customViewLibrary.view.popupWindow

import android.content.Context
import android.view.View
import android.view.animation.Animation
import androidx.appcompat.widget.AppCompatTextView
import com.tristana.customViewLibrary.R
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.ScaleConfig
import razerdp.util.animation.TranslationConfig


class PopupWindowView(context: Context?) : BasePopupWindow(context) {
    private lateinit var textView: AppCompatTextView

    /**
     *
     *
     * 返回一个contentView以作为PopupWindow的contentView
     *
     * <br></br>
     * **强烈建议使用[BasePopupWindow.createPopupById]，该方法支持读取View的xml布局参数，否则可能会出现与布局不一样的展示从而必须手动传入宽高等参数**
     */
    override fun onCreateContentView(): View {
        return createPopupById(R.layout.item_popup_layout)
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