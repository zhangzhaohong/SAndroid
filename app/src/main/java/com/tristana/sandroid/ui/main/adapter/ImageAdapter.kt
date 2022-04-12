package com.tristana.sandroid.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.FragmentUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.tristana.sandroid.R
import com.tristana.sandroid.model.bannerModel.BannerDataModel
import com.tristana.sandroid.ui.main.transform.GlideRoundTransform
import com.tristana.sandroid.ui.main.viewHolder.ImageViewHolder
import com.tristana.sandroid.ui.webView.X5WebViewFragment

/**
 * @author koala
 * @version 1.0
 * @date 2022/4/12 17:32
 * @description
 */
class ImageAdapter(
    private val activity: FragmentActivity,
    private val fragment: Fragment,
    private val context: Context,
    private val items: MutableList<out BannerDataModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_banner_image, parent, false)
        return ImageViewHolder(view)
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val imageViewHolder = holder as ImageViewHolder
        imageViewHolder.imageView.setOnClickListener {

        }
        val options = RequestOptions()
            .centerCrop() // .placeholder(R.drawable.ic_picture_loading) //预加载图片
            .error(R.drawable.ic_picture_load_failed) //加载失败图片
            .priority(Priority.HIGH) //优先级
            .diskCacheStrategy(DiskCacheStrategy.NONE) //缓存
            .transform(GlideRoundTransform(8)) //圆角
        Glide.with(context)
            .load(items[position].imagePath) // .transform(new GlideRoundTransform(48))
            .apply(options)
            .into(imageViewHolder.imageView)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}