package com.tristana.sandroid.ui.video.area.resolver

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.StringUtils
import com.tristana.library.tools.http.OkHttpRequestGenerator
import com.tristana.sandroid.MyApplication
import com.tristana.sandroid.http.PathCollection
import com.tristana.sandroid.respModel.HttpResponsePublicModel
import com.tristana.sandroid.respModel.aweme.AwemeResolverDataModel
import com.tristana.sandroid.respModel.aweme.live.LiveRespDataModel
import com.tristana.sandroid.respModel.aweme.music.MusicListInfoDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoResolverViewModel : ViewModel() {

    var link = MutableLiveData<String>(null)

    var resolverData = MutableLiveData<AwemeResolverDataModel?>(null)

    suspend fun doRequest() {
        return withContext(Dispatchers.IO) {
            OkHttpRequestGenerator.create(MyApplication.host, PathCollection.VIDEO_TIKTOK_API)
                .addParam("link", link.value).get().sync()?.let { response ->
                    try {
                        if (StringUtils.isEmpty(response)) return@withContext
                        val respData = GsonUtils.fromJson(response, HttpResponsePublicModel::class.java)
                        val itemInfoData = GsonUtils.fromJson(
                            GsonUtils.toJson(respData.data),
                            Map::class.java
                        )["item_info_data"]
                        val awemeDetailData = GsonUtils.fromJson(
                            GsonUtils.toJson(itemInfoData),
                            Map::class.java
                        )["aweme_detail"]
                        val resolverDataInfo =
                            AwemeResolverDataModel(null, null, null, null, null, null)
                        GsonUtils.fromJson(
                            GsonUtils.toJson(awemeDetailData),
                            Map::class.java
                        )?.let { detailData ->
                            detailData["mock_preview_picture_path"]?.let {
                                resolverDataInfo.mockPreviewPicturePath = it.toString()
                            }
                            GsonUtils.fromJson(
                                GsonUtils.toJson(detailData["video"]),
                                Map::class.java
                            )?.let { videoItemData ->
                                detailData["mock_preview_vid_path"]?.let {
                                    resolverDataInfo.mockPreviewVideoPath = it.toString()
                                }
                                detailData["mock_download_vid_path"]?.let {
                                    resolverDataInfo.mockDownloadVideoPath = it.toString()
                                }
                            }
                        }
                        val musicItemInfoData = GsonUtils.fromJson(
                            GsonUtils.toJson(respData.data),
                            Map::class.java
                        )["music_item_info_data"]
                        GsonUtils.fromJson(
                            GsonUtils.toJson(musicItemInfoData),
                            MusicListInfoDataModel::class.java
                        )?.let {
                            if (it.awemeList?.isNotEmpty() == true) {
                                resolverDataInfo.mockPreviewMusicPath =
                                    it.awemeList[0]?.mockPreviewMusicPath
                                resolverDataInfo.mockDownloadMusicPath =
                                    it.awemeList[0]?.mockDownloadMusicPath
                            }
                        }
                        val liveItemInfoData = GsonUtils.fromJson(
                            GsonUtils.toJson(respData.data),
                            Map::class.java
                        )["room_item_info_data"]
                        GsonUtils.fromJson(
                            GsonUtils.toJson(liveItemInfoData),
                            LiveRespDataModel::class.java
                        )?.let {
                            if (it.data?.data?.isNotEmpty() == true) {
                                val pathList = kotlin.collections.ArrayList<String?>().apply {
                                    add(it.data.data[0]?.streamUrl?.mockPreviewLivePath)
                                    add(it.data.data[0]?.streamUrl?.mockPreviewBackupLivePath)
                                }
                                resolverDataInfo.mockPreviewLivePath = pathList
                            }
                        }
                        withContext(Dispatchers.Main) {
                            resolverData.value = resolverDataInfo
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            resolverData.value = null
                        }
                    }
                }
        }
    }

}