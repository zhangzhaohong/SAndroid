package com.tristana.sandroid.respModel.aweme.music

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MusicListInfoDataModel(
    @SerializedName("aweme_list")
    val awemeList: ArrayList<MusicInfoDataModel?>?
) : Serializable
