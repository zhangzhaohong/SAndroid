package com.tristana.sandroid.ui.music.area.operation

import android.content.Context
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.GsonUtils
import com.tristana.library.tools.http.OkHttpRequestGenerator
import com.tristana.sandroid.MyApplication
import com.tristana.sandroid.http.PathCollection
import com.tristana.sandroid.respModel.HttpResponsePublicModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicSearchOperationViewModel : ViewModel() {

    fun requestTips(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            requestTipsData(text)
        }
    }
    private suspend fun requestTipsData(text: String) {
        return withContext(Dispatchers.IO) {
            OkHttpRequestGenerator.create(MyApplication.host, PathCollection.KUGOU_SEARCH_TIPS)
                .addParam("text", text)
                .get().sync()?.let { response ->
                    run {
                        GsonUtils.fromJson(
                            response,
                            HttpResponsePublicModel::class.java
                        )?.let {

                        }
                    }
                }
        }
    }
}