package com.tristana.sandroid.respModel.aweme.live

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LiveItemDataModel(
    @SerializedName("stream_url")
    val streamUrl: LiveStreamDataModel?
) : Serializable
