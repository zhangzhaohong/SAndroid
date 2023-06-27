package com.tristana.sandroid.ui.video.recommend

import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.tristana.library.tools.http.OkHttpRequestGenerator
import com.tristana.sandroid.MyApplication
import com.tristana.sandroid.http.PathCollection
import com.tristana.sandroid.respModel.HttpResponsePublicModel
import com.tristana.sandroid.respModel.video.recommend.AwemeDataModel
import com.tristana.sandroid.respModel.video.recommend.VideoRespDataModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
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
        loadNext(canLoadMore = true, resolveVidPath = true)
    }

    private suspend fun requestData(isManual: Boolean) {
        return withContext(Dispatchers.IO) {
            OkHttpRequestGenerator.create(MyApplication.host + PathCollection.VIDEO_RECOMMEND)
                .get().sync()?.let { response ->
                    run {
                        if (isFirstLoad.value == true) {
                            withContext(Dispatchers.Main) {
                                isFirstLoad.value = false
                            }
                        }
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
                                    videoData.awemeList?.let { vidList ->
                                        tmpVideoRecommendDataList.addAll(vidList)
                                    }
                                    if (!isManual) {
                                        loadNext(canLoadMore = false, resolveVidPath = false)
                                    }
                                }
                            }
                        }

                    }
                }
        }
    }

    private suspend fun requestVidInfoData(awemeData: AwemeDataModel): String {
        return suspendCoroutine {
            OkHttpRequestGenerator.create(MyApplication.host + PathCollection.VIDEO_TIKTOK_API)
                .addParam("link", awemeData.shareUrl).get().sync()?.let { response ->
                    it.resume(response)
                }
        }
    }

    fun loadNext(canLoadMore: Boolean, resolveVidPath: Boolean) {
        MainScope().launch {
            if (tmpVideoRecommendDataList.isEmpty()) {
                if (canLoadMore) {
                    loadMore()
                }
            } else {
                val awemeData = tmpVideoRecommendDataList[0];
                val vidData = withContext(Dispatchers.IO) {
                    requestVidInfoData(awemeData)
                }
                videoRecommendDataList.value?.add(awemeData)
                tmpVideoRecommendDataList.removeAt(0)
            }
            hasMore.value =
                (tmpHasMore && tmpVideoRecommendDataList.isEmpty()) || tmpVideoRecommendDataList.isNotEmpty()
        }
    }

    fun loadMore(isManual: Boolean = false) {
        CoroutineScope(Dispatchers.IO).launch {
            requestData(isManual)
        }
    }

    fun getTmpDataListSize(): Int {
        return tmpVideoRecommendDataList.size
    }
}