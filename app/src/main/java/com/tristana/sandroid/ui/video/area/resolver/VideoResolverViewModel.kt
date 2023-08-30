package com.tristana.sandroid.ui.video.area.resolver

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tristana.library.tools.http.OkHttpRequestGenerator
import com.tristana.sandroid.MyApplication
import com.tristana.sandroid.http.PathCollection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoResolverViewModel : ViewModel() {

    var link = MutableLiveData<String>(null)

    var response = MutableLiveData<String>(null)

    suspend fun doRequest() {
        return withContext(Dispatchers.IO) {
            OkHttpRequestGenerator.create(MyApplication.host, PathCollection.VIDEO_TIKTOK_API)
                .addParam("link", link.value).get().sync()?.let { response ->

                }
        }
    }

}