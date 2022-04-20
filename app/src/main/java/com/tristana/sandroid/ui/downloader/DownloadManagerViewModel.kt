package com.tristana.sandroid.ui.downloader

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Fetch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadManagerViewModel : ViewModel() {

    var fileInfoList = MutableLiveData<MutableList<Download>>()

    suspend fun getData(fetch: Fetch?, groupId: Int) {
        return withContext(Dispatchers.IO) {
            kotlin.run {
                fetch?.getDownloadsInGroup(groupId) { taskList ->
                    val data: ArrayList<Download> = ArrayList()
                    data.addAll(taskList)
                    fileInfoList.value = sortData(data)
                }
            }
        }
    }

    fun addOrUpdate(download: Download, isAdd: Boolean = false) {
        val data: ArrayList<Download> = fileInfoList.value as ArrayList<Download>
        if (isAdd) {
            data.add(0, download)
        } else {
            fileInfoList.value?.indexOfFirst { item -> item.id == download.id }?.let { index ->
                if (index >= 0 && index < data.size) {
                    data[index] = download
                }
            }
        }
        fileInfoList.value = sortData(data)
    }

    private fun sortData(input: ArrayList<Download>?): ArrayList<Download>? {
        input?.sortByDescending {
            it.created
        }
        return input
    }

    companion object {
        const val TAG = "DownloadManagerViewModel"
    }

    init {
        this.fileInfoList.value = ArrayList()
    }
}