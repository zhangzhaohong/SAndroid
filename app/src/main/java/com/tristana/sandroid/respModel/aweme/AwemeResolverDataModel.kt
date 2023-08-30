package com.tristana.sandroid.respModel.aweme

import java.io.Serializable

/**
 * @author koala
 * @date 2023/6/30 15:37
 * @version 1.0
 * @description
 */
data class AwemeResolverDataModel(
    var mockPreviewPicturePath: String?,
    var realMusicPath: String?,
    var mockPreviewMusicPath: String?,
    var mockDownloadMusicPath: String?,
    var realVideoPath: String?,
    var mockPreviewVideoPath: String?,
    var mockDownloadVideoPath: String?,
    var mockPreviewLivePath: ArrayList<String?>?,
) : Serializable
