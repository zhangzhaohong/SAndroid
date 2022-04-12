package com.tristana.sandroid.ui.main.viewHolder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.tristana.sandroid.R;

/**
 * @author koala
 * @version 1.0
 * @date 2022/4/12 18:05
 * @description
 */
public class ImageViewHolder extends RecyclerView.ViewHolder {

    private final AppCompatImageView imageView;

    public ImageViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.banner_img);
    }

    public AppCompatImageView getImageView() {
        return imageView;
    }
}
