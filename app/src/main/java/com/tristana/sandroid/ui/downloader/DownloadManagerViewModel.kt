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
            fetch?.getDownloadsInGroup(groupId) { taskList ->
                run {
                    fileInfoList.value?.clear()
                    val data: ArrayList<Download> = ArrayList()
                    data.addAll(taskList)
                    fileInfoList.value = sortData(data)
                }
            }
        }
    }

    fun addOrUpdate(download: Download) {
        val data: ArrayList<Download> = fileInfoList.value as ArrayList<Download>
        fileInfoList.value?.indexOfFirst { item -> item.id == download.id }?.let { index ->
            if (index >= 0 && index < data.size) {
                data[index] = download
            } else {
                data.add(0, download)
            }
        } ?: kotlin.run {
            data.add(0, download)
        }
        fileInfoList.value = sortData(data)
    }

    fun sortData(input: ArrayList<Download>): ArrayList<Download> {
        input.sortByDescending {
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