package com.tristana.sandroid.ui.video.recommend

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ObjectUtils
import com.blankj.utilcode.util.StringUtils
import com.tristana.library.tools.http.OkHttpRequestGenerator
import com.tristana.sandroid.MyApplication
import com.tristana.sandroid.http.PathCollection
import com.tristana.sandroid.respModel.HttpResponsePublicModel
import com.tristana.sandroid.respModel.video.recommend.AwemeDataModel
import com.tristana.sandroid.respModel.video.recommend.VideoRespDataModel
import com.tristana.sandroid.ui.video.recommend.cache.PreloadManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class VideoRecommendViewModel : ViewModel() {

    var isFirstLoad = MutableLiveData(true)
    var hasMore = MutableLiveData(true)
    var videoRecommendDataList = MutableLiveData<MutableList<AwemeDataModel>?>(ArrayList())
    private var tmpVideoRecommendDataList: ArrayList<AwemeDataModel> = ArrayList()
    private var tmpHasMore: Boolean = true

    init {
        videoRecommendDataList.value = ArrayList()
    }

    private suspend fun requestData(isManual: Boolean, context: Context) {
        return withContext(Dispatchers.IO) {
            OkHttpRequestGenerator.create(MyApplication.host, PathCollection.VIDEO_RECOMMEND)
                .get().sync()?.let { response ->
                    run {
                        GsonUtils.fromJson(
                            response,
                            HttpResponsePublicModel::class.java
                        )?.let {
                            GsonUtils.fromJson(
                                GsonUtils.toJson(it.data),
                                VideoRespDataModel::class.java
                            )?.let { videoData ->
                                withContext(Dispatchers.Main) {
                                    tmpHasMore = videoData.hasMore == 1
                                    videoData.awemeList?.forEach { awemeData ->
                                        if ((videoRecommendDataList.value?.filter { item -> item.awemeId == awemeData.awemeId }?.size
                                                ?: 0) > 0 || ObjectUtils.isNotEmpty(awemeData.cellRoom)
                                        ) {
                                            return@forEach
                                        }
                                        tmpVideoRecommendDataList.add(awemeData)
                                    }
                                    if (!isManual) {
                                        loadNext(
                                            canLoadMore = false,
                                            resolveVidPath = true,
                                            continueLoadNext = true,
                                            context = context
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }

    private suspend fun requestVidInfoData(awemeData: AwemeDataModel): String? {
        return suspendCoroutine {
            OkHttpRequestGenerator.create(MyApplication.host, PathCollection.VIDEO_TIKTOK_API)
                .addParam("link", awemeData.shareUrl).get().sync()?.let { response ->
                    if (StringUtils.isEmpty(response)) it.resume(null)
                    val respData = GsonUtils.fromJson(response, HttpResponsePublicModel::class.java)
                    val itemInfoData = GsonUtils.fromJson(
                        GsonUtils.toJson(respData.data),
                        Map::class.java
                    )["item_info_data"]
                    val awemeDetailData = GsonUtils.fromJson(
                        GsonUtils.toJson(itemInfoData),
                        Map::class.java
                    )["aweme_detail"]
                    val videoData = GsonUtils.fromJson(
                        GsonUtils.toJson(awemeDetailData),
                        Map::class.java
                    )["video"]
                    val videoPath = GsonUtils.fromJson(
                        GsonUtils.toJson(videoData),
                        Map::class.java
                    )["real_path"].toString()
                    it.resume(videoPath)
                }
        }
    }

    fun loadNext(
        canLoadMore: Boolean,
        resolveVidPath: Boolean,
        continueLoadNext: Boolean = false,
        context: Context
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            if (tmpVideoRecommendDataList.isEmpty()) {
                if (canLoadMore) {
                    loadMore(context = context)
                }
            } else {
                val awemeData = tmpVideoRecommendDataList[0];
                if (resolveVidPath && StringUtils.isEmpty(awemeData.videoPath)) {
                    try {
                        var videoPath: String? = null
                        awemeData.video?.let {
                            if (it.playAddr?.urlList?.isNotEmpty() == true) {
                                videoPath = it.playAddr.urlList[0]
                            }
                        }
                        if (videoPath.isNullOrEmpty()) {
                            videoPath = withContext(Dispatchers.IO) {
                                requestVidInfoData(awemeData)
                            }
                        }
                        awemeData.videoPath = videoPath
                        PreloadManager.getInstance(context).addPreloadTask(
                            awemeData.videoPath,
                            videoRecommendDataList.value?.size ?: 0
                        );
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                videoRecommendDataList.value?.add(awemeData)
                tmpVideoRecommendDataList.removeAt(0)
                if (continueLoadNext) {
                    loadNext(
                        canLoadMore = true,
                        resolveVidPath = true,
                        continueLoadNext = false,
                        context = context
                    )
                }
            }
            hasMore.value =
                (tmpHasMore && tmpVideoRecommendDataList.isEmpty()) || tmpVideoRecommendDataList.isNotEmpty()
            if (videoRecommendDataList.value?.isNotEmpty() == true && isFirstLoad.value == true) {
                isFirstLoad.value = false
            }
        }
    }

    fun loadMore(isManual: Boolean = false, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            requestData(isManual, context)
        }
    }

    fun getTmpDataListSize(): Int {
        return tmpVideoRecommendDataList.size
    }
}