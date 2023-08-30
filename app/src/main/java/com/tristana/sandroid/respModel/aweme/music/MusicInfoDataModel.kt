package com.tristana.sandroid.respModel.aweme.music

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MusicInfoDataModel(
    @SerializedName("mock_preview_music_path")
    val mockPreviewMusicPath: String?,
    @SerializedName("mock_download_music_path")
    val mockDownloadMusicPath: String?,
    ) : Serializable
