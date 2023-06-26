package com.tristana.sandroid.epoxy.holder;

import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyHolder;

import butterknife.ButterKnife;

/**
 * @author koala
 * @version 1.0
 * @date 2023/6/26 22:34
 * @description
 */
public abstract class BaseEpoxyHolder extends EpoxyHolder {
    @CallSuper
    @Override
    protected void bindView(@NonNull View itemView) {
        ButterKnife.bind(this, itemView);
    }
}
