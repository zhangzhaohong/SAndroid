package com.tristana.sandroid.ui.downloader

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Fetch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadManagerViewModel : ViewModel() {

    val fileInfoList = MutableLiveData<MutableList<Download>>()

    suspend fun getData(fetch: Fetch?, groupId: Int) {
        return withContext(Dispatchers.IO) {
            fetch?.getDownloadsInGroup(groupId) { taskList ->
                run {
                    fileInfoList.value?.clear()
                    val data: ArrayList<Download> = ArrayList()
                    data.addAll(taskList)
                    fileInfoList.value = data
                }
            }
        }
    }

    companion object {
        const val TAG = "DownloadManagerViewModel"
    }

    init {
        this.fileInfoList.value = ArrayList()
    }
}