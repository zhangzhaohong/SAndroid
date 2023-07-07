package com.tristana.sandroid.respModel.video.recommend

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @author koala
 * @version 1.0
 * @date 2023/6/30 17:11
 * @description
 */
data class CoverDataModel(
    @SerializedName("url_list")
    val urlList: ArrayList<String> = ArrayList()
) : Serializable