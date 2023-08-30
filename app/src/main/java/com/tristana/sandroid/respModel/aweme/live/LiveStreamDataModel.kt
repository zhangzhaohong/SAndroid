package com.tristana.sandroid.respModel.aweme.live

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LiveStreamDataModel(
    @SerializedName("mock_preview_live_path")
    val mockPreviewLivePath: String?,
    @SerializedName("mock_preview_live_path_backup")
    val mockPreviewBackupLivePath: String?,
) : Serializable
