package com.tristana.sandroid.ui.downloader

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Fetch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadManagerViewModel : ViewModel() {

    var pageNum = MutableLiveData(1)
    private var pageSize = MutableLiveData(10)
    var hasMore = MutableLiveData(true)
    var taskListData = MutableLiveData<MutableList<Download>>(ArrayList())
    var fileInfoList = MutableLiveData<MutableList<Download>>(ArrayList())

    suspend fun getData(fetch: Fetch?, groupId: Int) {
        return withContext(Dispatchers.IO) {
            kotlin.run {
                fetch?.getDownloadsInGroup(groupId) { taskList ->
                    val data: ArrayList<Download> = ArrayList()
                    data.addAll(taskList)
                    sortData(data)?.let { currentTaskListData ->
                        taskListData.value = currentTaskListData
                        val pageData: ArrayList<Download> = ArrayList()
                        var size = pageSize.value!!
                        currentTaskListData.forEach {
                            if (size > 0) {
                                pageData.add(it)
                                size--
                            } else {
                                return@forEach
                            }
                        }
                        fileInfoList.value = pageData
                        hasMore.value = data.size != pageData.size
                    }
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
}