package com.tristana.sandroid.ui.video.recommend

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.GsonUtils
import com.tristana.library.tools.http.OkHttpRequestGenerator
import com.tristana.sandroid.MyApplication
import com.tristana.sandroid.http.PathCollection
import com.tristana.sandroid.models.HttpResponsePublicModel
import com.tristana.sandroid.models.video.recommend.AwemeDataModel
import com.tristana.sandroid.models.video.recommend.VideoRespDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideoRecommendViewModel : ViewModel() {

    var hasMore = MutableLiveData(true)
    var videoRecommendDataList = MutableLiveData<MutableList<AwemeDataModel>?>(ArrayList())

    init {
        videoRecommendDataList.value = ArrayList()
        loadMore()
    }

    suspend fun requestData() {
        return withContext(Dispatchers.IO) {
            OkHttpRequestGenerator.create(MyApplication.host + PathCollection.VIDEO_RECOMMEND)
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
                                    hasMore.value = videoData.hasMore == 1
                                    videoData.awemeList?.let { vidList ->
                                        videoRecommendDataList.value?.addAll(vidList)
                                    }
                                }
                            }
                        }

                    }
                }
        }
    }

    fun loadMore() {
        MainScope().launch {
            withContext(Dispatchers.IO) {
                requestData()
            }
        }
    }
}